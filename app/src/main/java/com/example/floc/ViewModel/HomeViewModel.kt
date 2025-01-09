package com.example.floc.ViewModel

import android.app.AppOpsManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.floc.MockLocationService
import com.example.floc.Payments.PaymentRepository
import com.example.floc.authentication.repository.UserLoginRepository
import com.example.floc.ui.LatLng
import com.example.floc.util.Resource
import com.example.floc.util.UiEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.library.BuildConfig
import javax.inject.Inject


//class MockLocationViewModel(application: Application) : AndroidViewModel(application) {
//    private val locationManager: LocationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    private val mockProviderName = LocationManager.GPS_PROVIDER
//
//    private val _mockLocationStatus = MutableStateFlow<MockLocationStatus>(MockLocationStatus.Idle)
//    val mockLocationStatus: StateFlow<MockLocationStatus> = _mockLocationStatus
//
//    init {
//        setupMockLocationProvider()
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun setupMockLocationProvider() {
//        try {
//            locationManager.removeTestProvider(mockProviderName)
//        } catch (e: IllegalArgumentException) {
//            // Provider was not previously set, ignore this exception
//        }
//
//        try {
//            locationManager.addTestProvider(
//                mockProviderName,
//                false, false, false, false, false,
//                true, true, ProviderProperties.POWER_USAGE_LOW, ProviderProperties.ACCURACY_FINE
//            )
//            locationManager.setTestProviderEnabled(mockProviderName, true)
//            _mockLocationStatus.value = MockLocationStatus.ProviderReady
//        } catch (e: SecurityException) {
//            _mockLocationStatus.value = MockLocationStatus.Error("Failed to set up mock provider: ${e.message}")
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    fun setMockLocation(latitude: Double, longitude: Double) {
//        val mockLocation = Location(mockProviderName).apply {
//            this.latitude = latitude
//            this.longitude = longitude
//            this.accuracy = 1.0f
//            this.altitude = 0.0
//            this.time = System.currentTimeMillis()
//            this.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                this.bearingAccuracyDegrees = 0.1f
//                this.verticalAccuracyMeters = 0.1f
//                this.speedAccuracyMetersPerSecond = 0.01f
//            }
//        }
//
//        try {
//            locationManager.setTestProviderLocation(mockProviderName, mockLocation)
//            _mockLocationStatus.value = MockLocationStatus.Success(latitude, longitude)
//        } catch (e: IllegalArgumentException) {
//            setupMockLocationProvider()
//            try {
//                locationManager.setTestProviderLocation(mockProviderName, mockLocation)
//                _mockLocationStatus.value = MockLocationStatus.Success(latitude, longitude)
//            } catch (e: Exception) {
//                _mockLocationStatus.value = MockLocationStatus.Error("Failed to set mock location: ${e.message}")
//            }
//        }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        try {
//            locationManager.removeTestProvider(mockProviderName)
//        } catch (e: IllegalArgumentException) {
//            // Provider was not set, ignore this exception
//        }
//    }
//
////    private val locationManager: LocationManager =
////        application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
////
////    // Function to set mock location
////    @SuppressLint("MissingPermission")
////    fun setMockLocation(latitude: Double, longitude: Double) {
////        val mockLocation = Location(LocationManager.GPS_PROVIDER).apply {
////            this.latitude = latitude
////            this.longitude = longitude
////            this.accuracy = 1.0f
////            this.altitude = 0.0
////            this.time = System.currentTimeMillis()
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
////                this.elapsedRealtimeNanos = System.nanoTime()
////            }
////        }
////
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
////            // Android 12 (API level 31) and above
////            mockLocation.isMock = true
////        }
////
////        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation)
////    }
//
//    // Call this to enable the mock location provider
//    fun enableMockLocationProvider() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                // Set up the mock location provider for GPS
//                locationManager.addTestProvider(
//                    LocationManager.GPS_PROVIDER,
//                    false,
//                    false,
//                    false,
//                    false,
//                    true,
//                    true,
//                    true,
//                    ProviderProperties.POWER_USAGE_LOW,
//                    ProviderProperties.ACCURACY_FINE
//                )
//                locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    // Call this to disable the mock location provider
//    fun disableMockLocationProvider() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//}
data class UserLocation(
    val latitude: Double =0.0,
    val longitude: Double =0.0,
    val nickName: String? = null,
    val time: Long = System.currentTimeMillis() // Optional nickname
)
@HiltViewModel
class MockLocationViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val paymentRepository: PaymentRepository,
    private val userLoginRepository: UserLoginRepository

) : ViewModel() {
    private val _mockAppEnabled = MutableStateFlow<Boolean>(false)
    val mockAppEnabled: StateFlow<Boolean> = _mockAppEnabled
    private val _saveLocation = MutableStateFlow<Resource<Boolean>>(Resource.Unspecified())
    val saveLocation: StateFlow<Resource<Boolean>> = _saveLocation
    private  val _savedLocations = MutableStateFlow<Resource<List<UserLocation>>>(Resource.Unspecified())
    val savedLocations: StateFlow<Resource<List<UserLocation>>> = _savedLocations
    private val _clickCount = MutableStateFlow<Int>(0)
    val clickCount: StateFlow<Int> = _clickCount
    private val maxFreeClicks=10
    private val _isActiveUser= MutableStateFlow<Boolean>(false)
    val isActiveUser = _isActiveUser.asStateFlow()
   private val _isLoading =MutableStateFlow<Boolean>(true)
    val isLoading = _isLoading.asStateFlow()
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    init{
        CheckUser()
    }
    fun CheckUser(){
        _isLoading.value=true
        viewModelScope.launch {
            val res = paymentRepository.checkPlanStatus()
            if(res is Resource.Success && res.data == true){
                _isActiveUser.value = res.data
            }
            else{
                fetchClickCount()
            }
        _isLoading.value= false
        }

    }
    fun fetchClickCount() {

        val userId = auth.currentUser?.uid ?: return
        val userDoc = firestore.collection("user").document(userId)

        userDoc.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val currentClickCount = document.getLong("remainingClicks")?.toInt() ?: maxFreeClicks
                _clickCount.value = currentClickCount
            }
        }.addOnFailureListener {
            // Handle any errors
            Log.d("fetch", it.message.toString())
        }
    }
    fun decrementClickCount() {
        val userId = auth.currentUser?.uid ?: return
        val userDoc = firestore.collection("user").document(userId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userDoc)
//            val remainingClicks = snapshot.getLong("remainingClicks") ?: return@runTransaction
//            val remainingClicks = snapshot.getLong("remainingClicks") ?: maxFreeClicks
            val remainingClicks = (snapshot.getLong("remainingClicks") ?: maxFreeClicks).toLong()
            if (remainingClicks > 0) {
                transaction.update(userDoc, "remainingClicks", remainingClicks - 1)
            } else {
                // User has no remaining clicks; prompt for payment
//                Toast.makeText(context, "Please make a payment to continue using the app.", Toast.LENGTH_LONG).show()

            }

        }.addOnSuccessListener {
            // Success
            Log.d("dec", "Click count decremented successfully")
            fetchClickCount()
        }.addOnFailureListener {
            // Handle failure
            Log.d("dec", it.message.toString())
        }
    }



    fun setMockLocation(context: Context) {
        if (isMockLocationAppSet(context)) {
            _mockAppEnabled.value = true
            startMockLocationService(context)
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Location Set Successfully, Now you can switch App"))
            }

        } else {
//            Toast.makeText(context, "Please set this app as the mock location provider in Developer Options.", Toast.LENGTH_LONG).show()
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Please set this app as the mock location provider in Developer Options."))
            }
        }
    }


    private fun isMockLocationAppSet(context: Context):Boolean{
        var isMockLocation = false
        try {
            val opsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            isMockLocation = (opsManager.checkOp(
                AppOpsManager.OPSTR_MOCK_LOCATION,
                Process.myUid(),
                context.packageName
            ) == AppOpsManager.MODE_ALLOWED)
        } catch (e: Exception) {
            // If an exception occurs, return the current value of isMockLocation
        }
        return isMockLocation
    }

//    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMockLocationService(context: Context) {
        val serviceIntent = Intent(context, MockLocationService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
//        val intent = Intent(context, MockLocationService::class.java)
//        val pendingIntent = PendingIntent.getForegroundService(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
    }
    fun stopMockLocationService(context: Context) {
        val intent = Intent(context, MockLocationService::class.java)
        context.stopService(intent)
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowSnackbar("Mock location service stopped"))
        }
    }
    suspend fun logout(){

        viewModelScope.launch {
            val currentUserUid = auth.currentUser?.uid
            if (currentUserUid != null) {
//                userLoginRepository.signOut(currentUserUid)
                firestore.collection("user").document(currentUserUid).update("deviceId", FieldValue.delete()).await()
                auth.signOut()

            } else {
                Log.e("Logout", "No user is currently logged in.")
            }
        }.join()
    }
    fun saveLocation( latitude: Double, longitude: Double,nickName:String?){
        val location = UserLocation(latitude, longitude, nickName)
        firestore.collection("user").document(auth.currentUser?.uid ?: "").collection("locations").document().set(

            location
        ).addOnSuccessListener {
            _saveLocation.value = Resource.Success(true)
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Location saved successfully"))
            }
        }.addOnFailureListener {
            _saveLocation.value = Resource.Error(it.message.toString())
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Failed to save location"))
            }
        }
    }
    fun getSavedLocation(){
            firestore.collection("user").document(auth.currentUser?.uid ?: "").collection("locations").orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener { result->
                val locations = mutableListOf<UserLocation>()
                for (document in result) {

                    val location = document.toObject(UserLocation::class.java)
                    locations.add(location)
                }
                Log.d("viewmodel", locations.toString())

                _savedLocations.value = Resource.Success(locations)
            }.addOnFailureListener{
                _savedLocations.value = Resource.Error(it.message.toString())
            }


    }
    fun checkLatLng(latitude: String, longitude: String):Boolean{
        val lat = latitude.toDoubleOrNull()
        val lon = longitude.toDoubleOrNull()
        if (lat != null && lon != null) {
            return true
        }
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowSnackbar("Latitude or Longitude are not in correct format"))
        }
        return false
    }

}
sealed class MockLocationStatus {
    object Idle : MockLocationStatus()
    object ProviderReady : MockLocationStatus()
    data class Success(val latitude: Double, val longitude: Double) : MockLocationStatus()
    data class Error(val message: String) : MockLocationStatus()
}
