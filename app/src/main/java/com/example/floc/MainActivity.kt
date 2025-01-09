package com.example.floc

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.floc.ui.theme.FlocTheme
import com.example.floc.MockLocationService
import com.example.floc.NavigationGraph.MainNavigation
import com.example.floc.Payments.PaymentRepository
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import javax.inject.Inject
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log


@AndroidEntryPoint
class MainActivity : ComponentActivity(),PaymentResultListener {
    @Inject
    lateinit var paymentRepository: PaymentRepository

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var isRequestingPermissions = false
    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().apply {
            userAgentValue = BuildConfig.APPLICATION_ID
        }
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Check which permissions were granted/denied
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val foregroundServiceGranted = permissions[Manifest.permission.FOREGROUND_SERVICE] ?: false
            val foregroundServiceLocationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions[Manifest.permission.FOREGROUND_SERVICE_LOCATION] ?: false
            } else {
                true
            }
            if (fineLocationGranted && foregroundServiceGranted && foregroundServiceLocationGranted) {
                startMockLocationService()
            } else {
                // Handle the case where permissions are not granted
            }
        }
//        enableEdgeToEdge()
        setContent {
            FlocTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//
//                }
                MainNavigation(navController = rememberNavController(),minimizeApp = {minimizeApp()} )
            }
        }
    }
    private fun minimizeApp() {
//        moveTaskToBack(true)
    }

    override fun onResume() {
        super.onResume()
        // Start the mock location service again to reapply the mock location

//        val intent = Intent(this, MockLocationService::class.java)
//        ContextCompat.startForegroundService(this, intent)
        checkAndRequestPermissions()
    }
    private fun checkAndRequestPermissions() {
        if (!isRequestingPermissions) {
            if (hasLocationPermissions()) {
                startMockLocationService()
            } else {
//                requestLocationPermissions()
            }
        }
    }
    private fun startMockLocationService() {
        val intent = Intent(this, MockLocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
    // Handle Razorpay payment result
    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        paymentRepository.savePaymentToFirebase(razorpayPaymentID,"success")

        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()

    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
    }
@SuppressLint("InlinedApi")
fun requestLocationPermissions() {
    Log.d("MainActivity", "Requesting permissions")
    val permissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
    }

        if (!isRequestingPermissions) {
            isRequestingPermissions = true
            requestPermissionLauncher.launch(permissions.toTypedArray())
        } else {

            showPermissionSettingsDialog()
        }
//    }
}
     fun showPermissionSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage("This app needs location permission to function properly. Please grant them in the app settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
     private fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            startActivity(this)
        }
    }
    private fun hasLocationPermissions(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val foregroundServiceGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.FOREGROUND_SERVICE
        ) == PackageManager.PERMISSION_GRANTED
        val foregroundServiceLocationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.FOREGROUND_SERVICE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        return fineLocationGranted && foregroundServiceGranted && foregroundServiceLocationGranted
    }
//     fun requestLocationPermissions() {
//        requestPermissionLauncher.launch(
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.FOREGROUND_SERVICE
//            )
//        )
//    }


    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

}
fun saveMockLocation(context: Context, latitude: Double, longitude: Double) {
    val sharedPreferences = context.getSharedPreferences("mock_location_prefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("latitude", latitude.toString())
        putString("longitude", longitude.toString())
        apply()
    }
}

fun getSavedMockLocation(context: Context): Pair<Double, Double>? {
    val sharedPreferences = context.getSharedPreferences("mock_location_prefs", Context.MODE_PRIVATE)
    val lat = sharedPreferences.getString("latitude", null)?.toDoubleOrNull()
    val lon = sharedPreferences.getString("longitude", null)?.toDoubleOrNull()
    return if (lat != null && lon != null) Pair(lat, lon) else null
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlocTheme {
        Greeting("Android")
    }
}