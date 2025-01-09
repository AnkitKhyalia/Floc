package com.example.floc.ui

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.floc.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
class FixedAspectRatioMapView(context: Context) : MapView(context) {
    private var aspectRatio = 1f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = (width / aspectRatio).toInt()
        setMeasuredDimension(width, height)
    }

    fun setAspectRatio(ratio: Float) {
        aspectRatio = ratio
        requestLayout()
    }
}

@Composable
fun OSMMap(
    initialLatLng: LatLng? = null,
    onMapClick: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var marker: Marker? by remember { mutableStateOf(null) }
    DisposableEffect(key1 = mapView) {
        // Called when the map is displayed
        mapView.onResume()

        onDispose {
            // Called when the composable is disposed (navigating away)
            mapView.onPause()
            mapView.tileProvider.clearTileCache() // Clear tile cache here
        }
    }
    // Store the map controller and only update it when the initialLatLng changes
    val mapController = remember { mapView.controller as? MapController }

    // Track the initialLatLng and only change the map's center when it changes
    LaunchedEffect(initialLatLng) {
        val startLatLng = initialLatLng ?: LatLng(25.0, 75.0)
        mapController?.setZoom(10.0)
        mapController?.setCenter(GeoPoint(startLatLng.latitude, startLatLng.longitude))

        // Add or update the marker when initialLatLng changes
        if (initialLatLng != null) {
            // Remove the old marker if exists
            marker?.let { mapView.overlays.remove(it) }

            // Add new marker to the initial coordinates
            marker = Marker(mapView).apply {
                position = GeoPoint(initialLatLng.latitude, initialLatLng.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Selected Pin"
            }
            mapView.overlays.add(marker)
            mapView.invalidate() // Redraw the map with the new marker
        }
    }

    DisposableEffect(key1 = mapView) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
             // Keep the aspect ratio of the map
    ) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        ) { view ->
            view.setMultiTouchControls(true)
        }

        // Centered pin icon to represent the current map center
        IconButton(
            onClick = {
                val center = mapView.mapCenter
                val geoPoint = GeoPoint(center.latitude, center.longitude)
                val latLng = LatLng(center.latitude, center.longitude)

                // Remove the old marker
                marker?.let { mapView.overlays.remove(it) }

                // Add a new marker at the center of the map
                marker = Marker(mapView).apply {
                    position = geoPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Dropped Pin"
                }
                mapView.overlays.add(marker)
                mapView.invalidate() // Redraw the map with the new marker

                onMapClick(latLng)
            },
            modifier = Modifier.align(Alignment.Center).size(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Center Pin",
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center),
                tint = Color(0xFFEB726D),


                )
        }



    }

}

data class LatLng(
    val latitude: Double,
    val longitude: Double,
)

@Composable
fun MapScreen(modifier: Modifier,
              initialLatLng: LatLng? = null, // Initial map position if provided
              onMapClick: (LatLng) -> Unit) {


    Column(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            OSMMap (initialLatLng= initialLatLng){ latLng ->
                // Debug log
                onMapClick(latLng)
            }
        }
//        Text(
//            text = if (selectedLocation != null)
//                "Selected Location: Latitude: ${selectedLocation?.latitude}, Longitude: ${selectedLocation?.longitude}"
//            else
//                "No location selected",
//            modifier = Modifier.padding(16.dp),
//            color = Color.White
//        )

    }

}
