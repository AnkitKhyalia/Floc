package com.example.floc

import android.app.AppOpsManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Process
import android.os.SystemClock
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat


class MockLocationService : Service() {
    private val locationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updatePeriod = 200L

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {

        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
//        Handler(Looper.getMainLooper()).postDelayed({
//            startForeground(NOTIFICATION_ID, createNotification())
//        }, 1000)

        if (isMockLocationEnabled()) {

            initializeMockLocation()
            startPeriodicMockLocationUpdates()
        } else {
            showMockLocationNotEnabledNotification()
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun isMockLocationEnabled(): Boolean {
        var isMockLocation = false
        try {
            val opsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            isMockLocation = (opsManager.checkOp(
                AppOpsManager.OPSTR_MOCK_LOCATION,
                Process.myUid(),
                packageName  // Use packageName directly in a service
            ) == AppOpsManager.MODE_ALLOWED)
        } catch (e: Exception) {
            e.printStackTrace()
            // Log the exception and return the current value of isMockLocation
        }
        return isMockLocation
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val notificationChannelId = "mock_location_channel"
        val channelName = "Mock Location Service"
        val channel = NotificationChannel(
            notificationChannelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification=  NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Floc is Active")
            .setContentText("Providing mock location in the background.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        manager.notify(NOTIFICATION_ID, notification)

        return notification

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showMockLocationNotEnabledNotification() {
        val notificationChannelId = "mock_location_error_channel"
        val channelName = "Mock Location Error"
        val channel = NotificationChannel(
            notificationChannelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Mock Location Not Enabled")
            .setContentText("Please set floc as mock location app in developer settings. For Help See Tutorial")
            .setSmallIcon(R.drawable.ic_launcher_foreground)

            .build()

        manager.notify(ERROR_NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeMockLocation() {
        val mockProviderName = LocationManager.GPS_PROVIDER

        try {
            if (locationManager.allProviders.contains(mockProviderName)) {
                locationManager.removeTestProvider(mockProviderName)
            }

            locationManager.addTestProvider(
                mockProviderName,
                false, false, false, false, true, true, true,
                ProviderProperties.POWER_USAGE_LOW,
                ProviderProperties.ACCURACY_FINE
            )

            locationManager.setTestProviderEnabled(mockProviderName, true)

            reapplySavedMockLocation()
        } catch (e: SecurityException) {
            e.printStackTrace()
            stopSelf()
            showMockLocationNotEnabledNotification()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun reapplySavedMockLocation() {
        val savedLocation = getSavedMockLocation(this)
        if (savedLocation != null) {
            val (lat, lon) = savedLocation
            updateMockLocation(lat, lon)
        }
    }

    private fun updateMockLocation(lat: Double, lon: Double) {
        val mockProviderName = LocationManager.GPS_PROVIDER

        val mockLocation = Location(mockProviderName).apply {
            latitude = lat
            longitude = lon
            accuracy = 1.0f
            time = System.currentTimeMillis()
            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }

        locationManager.setTestProviderLocation(mockProviderName, mockLocation)
    }

    private fun startPeriodicMockLocationUpdates() {
        handler.post(object : Runnable {
            override fun run() {
                reapplySavedMockLocation()
                handler.postDelayed(this, updatePeriod)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        stopMockLocation()
    }

    private fun stopMockLocation() {
        try {
            val mockProviderName = LocationManager.GPS_PROVIDER
            if (locationManager.allProviders.contains(mockProviderName)) {
                locationManager.removeTestProvider(mockProviderName)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val ERROR_NOTIFICATION_ID = 2
        private const val POPUP_NOTIFICATION_ID = 3
    }
}