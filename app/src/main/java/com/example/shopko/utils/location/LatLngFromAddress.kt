package com.example.shopko.utils.location

import android.content.Context
import android.location.Geocoder
import android.location.Address
import android.os.Build
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object LatLngFromAddress {
    suspend fun getLatLngFromAddressSuspend(
        context: Context,
        address: String
    ): LatLng? = suspendCoroutine { cont ->
        val geocoder = Geocoder(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(address, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (addresses.isNotEmpty()) {
                        val result = addresses[0]
                        cont.resume(LatLng(result.latitude, result.longitude))
                    } else {
                        Log.e("Geocoder", "No location found for: $address")
                        cont.resume(null)
                    }
                }

                override fun onError(errorMessage: String?) {
                    Log.e("Geocoder", "Geocode failed: $errorMessage")
                    cont.resume(null)
                }
            })
        } else {
            try {
                @Suppress("DEPRECATION")
                val results = geocoder.getFromLocationName(address, 1)
                if (!results.isNullOrEmpty()) {
                    val result = results[0]
                    cont.resume(LatLng(result.latitude, result.longitude))
                } else {
                    Log.e("Geocoder", "No results found for: $address")
                    cont.resume(null)
                }
            } catch (e: Exception) {
                Log.e("Geocoder", "Exception: ${e.message}")
                cont.resume(null)
            }
        }
    }
}
