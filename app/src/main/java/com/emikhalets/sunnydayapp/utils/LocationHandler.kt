package com.emikhalets.sunnydayapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import java.util.*

class LocationHandler {

    companion object {

        // Granting location permissions

        fun checkLocationPermissions(context: Context) =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        fun getCityAndCountry(context: Context, lat: Double, lon: Double): String {
            val address = Geocoder(context, Locale.getDefault())
                .getFromLocation(lat, lon, 1).first()
            return "${address.locality}, ${address.countryName}"
        }
    }
}