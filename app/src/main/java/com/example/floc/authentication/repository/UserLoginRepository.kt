package com.example.floc.authentication.repository

import android.util.Log
import com.example.floc.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

import javax.inject.Inject

class UserLoginRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val randomId =UUID.randomUUID().toString()
    suspend fun signInWithEmailAndPassword(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user!!

            // Step 2: Fetch the user document using uid instead of email
            val userDocument = firestore.collection("user").document(user.uid).get().await()
            val storedDeviceId = userDocument.getString("deviceId")

            // Step 3: Check if deviceId is already assigned and doesn't match the current randomId
            if (storedDeviceId != null && storedDeviceId != randomId) {
                // If the device ID doesn't match, return an error
                auth.signOut()
                return Resource.Error("This account is already signed in on another device.")
            }

            // Step 4: Update Firestore with the new deviceId (randomId) after successful login
            firestore.collection("user").document(user.uid).update("deviceId", randomId).await()

            // Step 5: Return success with the FirebaseUser object
            Resource.Success(user)
        } catch (e: Exception) {
            Log.d("userlogin",e.message.toString())
            Resource.Error("Password is Incorrect")

        }
    }
    suspend fun signOut(userUid: String) {
        try {

            firestore.collection("user").document(userUid).update("deviceId", FieldValue.delete()).await()
            auth.signOut()
            Log.d("SignOut", "Successfully signed out and cleared device ID.")
        } catch (e: Exception) {
            Log.e("SignOut", "Error during sign-out: ${e.message}")
        }
    }
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}