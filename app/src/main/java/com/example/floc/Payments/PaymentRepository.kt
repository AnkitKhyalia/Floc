package com.example.floc.Payments

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.floc.data.Payment
import com.example.floc.data.Plan
import com.example.floc.util.Resource
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.razorpay.Checkout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.util.Calendar

import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume

class PaymentRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val _planId= MutableStateFlow<String>("")
    private val _amount = MutableStateFlow<Double>(0.0)
    private val _planDuration =MutableStateFlow<Int>(6)
    fun startPayment(context: Context, amount: Double, email: String, contact: String,planId:String) {
        _planId.value = planId
        _amount.value = amount
        val razorpayApiKey = ""
        val checkout = Checkout()
        checkout.setKeyID(razorpayApiKey)

        try {
            val options = JSONObject().apply {
                put("name", "floc")
                put("description", "Payment Description")
                put("image", "")
                put("currency", "INR")
                put("amount", (amount * 100).toInt())
                put("prefill.email", email)
                put("prefill.contact", contact)
                put("theme.color", "#3399cc")
                put("send_sms_hash", true)
                put("remember_customer",false)

            }
            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 3)
            options.put("retry", retryObj)
            val hiddenObj = JSONObject()
            hiddenObj.put("upi", false)
//            hiddenObj.put("contact", true)
//            hiddenObj.put("email", true)
            options.put("hidden", hiddenObj)
            val readOnlyObj = JSONObject()
            readOnlyObj.put("contact", true)
            readOnlyObj.put("email",true)
            options.put("readonly", readOnlyObj)

            val methodOptions = JSONObject()
            methodOptions.put("upi", false)   // UPI should now appear as a payment option
            methodOptions.put("netbanking", false)  // Optional: Enable netbanking
            methodOptions.put("card", true) // Optional: Enable card payments
            methodOptions.put("paylater", false)
            methodOptions.put("wallet", false)


            options.put("method", methodOptions)

            checkout.open(context as Activity, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun savePaymentToFirebase(paymentID: String?,status:String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val paymentDate = FieldValue.serverTimestamp()
        Log.d("userId",userId?:"null user id")
        val paymentData = hashMapOf(
            "userId" to userId ,
            "paymentID" to paymentID,
            "status" to status,
            "amount" to _amount.value,
            "paymentDate" to paymentDate,  // Server-side timestamp
            "planId" to _planId.value,

        )




        firestore.collection("payments")
            .add(paymentData)
            .addOnSuccessListener { documentReference ->
                
//                val userId= auth.currentUser?.uid
                if(userId != null) {
                    updateUserWithPayment(userId, documentReference.id)

//                    Log.d("PaymentRepository", "Payment saved successfully")

                }

            }
            .addOnFailureListener {
                Log.e("PaymentRepository", "Error saving payment details", it)
            }
    }

    suspend fun checkPlanStatus(): Resource<Boolean> {
        val userId = auth.currentUser?.uid ?: return Resource.Error("User not logged in")

        return try {
            // Store the current server timestamp in a temporary field to retrieve it later
            val tempDocRef = firestore.collection("serverTime").document("currentTime")

            // Set the server timestamp
            tempDocRef.set(hashMapOf("timestamp" to FieldValue.serverTimestamp())).await()

            // Retrieve the server timestamp
            val timestampSnapshot = tempDocRef.get().await()
            val currentTimestamp = timestampSnapshot.getTimestamp("timestamp")

            // Fetch the user document along with the server timestamp
            val userDocumentSnapshot = firestore.collection("user").document(userId).get().await()

            if (userDocumentSnapshot.exists()) {
                // Check if the activePlan exists in the document
                val activePlan = userDocumentSnapshot.get("activePlan") as? Map<*, *>

                // If no activePlan, consider it as no active plan (Success(false))
                if (activePlan == null) {
                    Log.d("ActivePlan", "No active plan found for the user.")
                    return Resource.Success(false)
                }

                val expiryDate = activePlan["expiryDate"] as? Timestamp

                // If the expiryDate is not available, return Success(false)
                if (expiryDate == null || currentTimestamp == null) {
                    Log.d("ActivePlan", "Expiry date or current timestamp not available.")
                    return Resource.Success(false)
                }

                // Compare the expiryDate with the current server timestamp
                return if (expiryDate.seconds > currentTimestamp.seconds) {
                    Log.d("ActivePlan", "User has an active plan until: $expiryDate")
                    Resource.Success(true)
                } else {
                    Log.d("ActivePlan", "User's plan has expired.")
                    Resource.Success(false)
                }
            } else {
                Log.d("User", "User document does not exist.")
                return Resource.Success(false)  // If the user document doesn't exist, assume no active plan
            }
        } catch (e: Exception) {
            Log.e("Error", "Exception occurred", e)
            Resource.Error("Error fetching user data or server timestamp")
        }
    }


    private fun updateUserWithPayment(userId: String, paymentDocumentId: String) {
        firestore.collection("user").document(userId)
            .get()
            .addOnSuccessListener { userSnapshot ->
                if (!userSnapshot.exists()) {
                    Log.e("UserFetch", "User document does not exist")
                    return@addOnSuccessListener
                }

                // Retrieve user data or initialize an empty map if no data exists
                val userData = userSnapshot.data ?: hashMapOf<String, Any>()
                Log.d("userData",userData.toString())

                // Safely retrieve the 'activePlan' field or create a new map if it doesn't exist
                val activePlan = (userData["activePlan"] as? Map<*, *>)?.toMutableMap() ?: mutableMapOf()
                Log.d("userData",userData.toString())

                // Update or set the necessary fields
                    runBlocking {
                        activePlan["expiryDate"] = getExpiryDate(duration = _planDuration.value)
                    }
                activePlan["planId"] = _planId.value


                // Prepare user update data
                val userUpdateData = hashMapOf(
                    "activePlan" to activePlan,  // Update or create 'activePlan' field
                    "lastPaymentId" to paymentDocumentId
                )

                // Update user's active plan data in Firestore
                firestore.collection("user").document(userId)
                    .update(userUpdateData)
                    .addOnSuccessListener {
                        Log.d("UserUpdate", "User plan updated successfully.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("UserUpdate", "Error updating user", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("UserFetch", "Error fetching user", e)
            }
    }
    private suspend fun getExpiryDate(duration:Int):Timestamp{
        try {
           val timestampDocRef= firestore.collection("serverTime").document("currentTime")
            timestampDocRef.set(hashMapOf("timestamp" to FieldValue.serverTimestamp())).await()

            // Step 2: Retrieve the stored timestamp
            val timestampSnapshot = timestampDocRef.get().await()
            val currentTimestamp = timestampSnapshot.getTimestamp("timestamp")

            val newExpiryDate = extendExpiryDate(currentTimestamp, duration)

            return newExpiryDate

        }
        catch (e:Exception){
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, duration)

            return Timestamp(calendar.time)
        }
    }
    private fun extendExpiryDate(currentExpiryDate: Timestamp?, monthsToAdd: Int): Timestamp {
        val calendar = Calendar.getInstance()

        // If there's a current expiry date, set it; otherwise, use the current date
        currentExpiryDate?.let {
            calendar.time = it.toDate()
        }

        // Add the specified number of months
        calendar.add(Calendar.MONTH, monthsToAdd)

        // Return the new expiry date as a Timestamp
        return Timestamp(calendar.time)
    }



    suspend fun getPaymentHistory():Resource<List<Payment>>{
        val userId = auth.currentUser?.uid
        val firestore = FirebaseFirestore.getInstance()
        val paymentHistory = MutableList<Payment>(0){Payment()}
        firestore.collection("payments")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val item = document.toObject(Payment::class.java)
                    if(item!=null){
                    paymentHistory.add(item)
                    }
                    Log.d("PaymentHistory", "Payment: ${document.data}")
                }

            }
            .addOnFailureListener { e ->
                Log.e("PaymentHistory", "Error fetching payment history", e)
            }
        return Resource.Success(paymentHistory)
    }
    suspend fun fetchPlans(): Resource<List<Plan>> = suspendCancellableCoroutine { continuation ->
        val plansList = mutableListOf<Plan>()

        firestore.collection("plans")
            .get()
            .addOnSuccessListener { querySnapshot ->
                 querySnapshot.documents.mapNotNull { document ->
                    val plan = document.toObject(Plan::class.java)
                    Log.d("FetchPlans", "Fetched plan: ${plan?.name}")
                    if(plan!=null){

                    plansList.add(plan)
                    }
                }

                Log.d("all plans", plansList.toString())
                continuation.resume(Resource.Success(plansList)) // Resume the coroutine with the result
            }
            .addOnFailureListener { e ->
                Log.e("FetchPlans", "Error fetching plans", e)
                continuation.resume(Resource.Error("Error fetching plans")) // Return error state with empty list
            }
    }
}