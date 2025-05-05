package com.example.shopko.utils.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Dohvaća geografske koordinate (latitude, longitude) iz adrese kao teksta.
 *
 * @return [LatLng] objekt s koordinatama adrese ili `null` ako adresa nije pronađena.
 *
 * Ova funkcija podržava i starije i novije verzije Androida:
 * - Za Android 13 (API 33) i novije koristi asinkroni `GeocodeListener`.
 * - Za starije koristi blokirajući pristup `getFromLocationName`.
 *
 * Potrebno je imati internetsku vezu i aktiviran Geocoder servis na uređaju.
 */

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
