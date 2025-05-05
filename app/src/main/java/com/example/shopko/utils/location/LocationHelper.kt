package com.example.shopko.utils.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Pomoćna klasa za dohvaćanje korisnikove trenutne lokacije korištenjem Google Play Services FusedLocationProviderClient.
 *
 * @constructor Prima [Context] aplikacije ili aktivnosti za pristup servisima.
 */

class LocationHelper(private val context: Context) {

    private val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getLastLocationSuspend(): Location? = suspendCancellableCoroutine { cont ->
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
            .setMinUpdateIntervalMillis(5000L)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                cont.resume(locationResult.lastLocation) {}
                fusedLocationProvider.removeLocationUpdates(this)
            }
        }

        fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }
}