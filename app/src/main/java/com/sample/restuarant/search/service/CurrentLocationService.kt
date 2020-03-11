package com.sample.restuarant.search.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

class CurrentLocationService : Service() {

    private val binder = LocationBinder()
    private val tag = "LocationService"

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var settingsClient: SettingsClient

    private lateinit var locationRequest: LocationRequest

    private lateinit var locationSettingsRequest: LocationSettingsRequest

    private lateinit var locationCallback: LocationCallback

    private lateinit var location: Location

    private lateinit var currentLocationCallback: CurrentLocationCallback

    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        settingsClient = LocationServices.getSettingsClient(this)

        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopLocationUpdates()
        return super.onUnbind(intent)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = TimeUnit.MINUTES.toMillis(2)
        locationRequest.fastestInterval = TimeUnit.MINUTES.toMillis(1)
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if (locationResult != null) {
                    location = locationResult.lastLocation
                    Log.d(tag, "current location $location")
                    currentLocationCallback.onLocationReceived(location)
                }
            }
        }
    }

    fun setCurrentLocationCallback(callback: CurrentLocationCallback) {
        currentLocationCallback = callback
    }

    fun requestLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                Log.i(tag, "requesting location updates")
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            }
            .addOnFailureListener {
                Log.i(tag, "Failed to get location updates", it)
            }
    }

    private fun buildLocationSettingsRequest() {
        locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest).build()
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    inner class LocationBinder : Binder() {
        fun getLocationService() = this@CurrentLocationService
    }

    interface CurrentLocationCallback {
        fun onLocationReceived(location: Location)
    }

}
