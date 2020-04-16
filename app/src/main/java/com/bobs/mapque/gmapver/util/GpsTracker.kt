package com.bobs.mapque.gmapver.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import com.bobs.baselibrary.ext.toast
import com.bobs.baselibrary.util.loge
import com.bobs.mapque.gmapver.ui.MainActivity
import com.bobs.mapque.gmapver.util.ext.checkLocationPermission
import com.bobs.mapque.gmapver.util.ext.checkPermission
import com.gun0912.tedpermission.PermissionListener
import java.io.IOException

@SuppressLint("MissingPermission")
class GpsTracker(private val context: Context) {

    enum class EnabledLocationStatus{
        DISABLE,
        PERMISSIONGRANTED,
        PERMISSIONDENIED
    }

    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private val geocoder: Geocoder by lazy { Geocoder(context) }
    private var location: Location? = null

    private val MIN_DISTANCE_UPDATES: Float = 10F
    private val MIN_TIME_UPDATES: Long = 1000 * 60

    fun isEnableGetLocation(out: (EnabledLocationStatus) -> Unit) {
        // provider들 가능 여부 체크
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            out(EnabledLocationStatus.DISABLE)
        } else {
            checkLocationPermission(
                context,
                object : PermissionListener {
                    override fun onPermissionGranted() {
                        out(EnabledLocationStatus.PERMISSIONGRANTED)
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        out(EnabledLocationStatus.PERMISSIONDENIED)
                    }
                }
            )
        }
    }

    fun getCurrentLocation(): Location {
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (isGpsEnabled || isNetworkEnabled) {
            location = getLastKnownLocation()
        }

        return location!!
    }

    fun getLastKnownLocation(): Location? {
        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers.toList()) {
            val l = locationManager.getLastKnownLocation(provider) ?: continue

            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                bestLocation = l
            }
        }

        return bestLocation
    }

    fun getAddressName(location: Location): String{
        var list: List<Address>? = listOf()

        try {
            list = geocoder.getFromLocation(location.latitude,location.longitude, 30)
        } catch (e: IOException){
            e.printStackTrace()
        }

        list?.run {
            return list[0].getAddressLine(0)
        }

        return ""
    }

    fun getSearchAddress(query: String): List<Address>? {
        var list: List<Address>? = null

        try{
            list = geocoder.getFromLocationName(query, 30)
        }catch (e: IOException){
            e.printStackTrace()
        }

        return list
    }

    fun checkLocationServicesStatus(): Boolean =
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
}