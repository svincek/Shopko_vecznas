package com.example.shopko

import com.google.android.gms.maps.model.LatLng

fun interface DistanceCalculator {
    fun calculate(start: LatLng, end: LatLng): Float
}