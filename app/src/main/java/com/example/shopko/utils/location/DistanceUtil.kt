package com.example.shopko.utils.location

import android.location.Location
import com.google.android.gms.maps.model.LatLng

object DistanceUtil {
    fun calculateDistance(start: LatLng, end: LatLng): Float {
        val result: FloatArray = FloatArray(1)
        Location.distanceBetween(
            start.latitude,
            start.longitude,
            end.latitude,
            end.longitude,
            result
        )
        return result[0]
    }
}