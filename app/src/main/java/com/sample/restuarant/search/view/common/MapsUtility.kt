package com.sample.restuarant.search.view.common

import android.content.Context
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sample.restuarant.search.R
import java.math.RoundingMode

/* @author Dinesh Kumar 
   @creation_date 3/11/2020*/

class MapsUtility {

    fun isUserInSameLocation(currentLocation: Location?, newLocation: Location): Boolean {
        return (currentLocation != null && (isSameGeoPosition(
            currentLocation.latitude,
            newLocation.latitude
        ) && isSameGeoPosition(
            currentLocation.longitude,
            newLocation.longitude
        )))
    }

    private fun isSameGeoPosition(oldPosition: Double?, newPosition: Double): Boolean {
        return oldPosition?.toBigDecimal()?.setScale(
            3,
            RoundingMode.HALF_EVEN
        ) == newPosition.toBigDecimal().setScale(3, RoundingMode.HALF_EVEN)
    }

    fun addMarkersToMap(title: String, latLong: LatLng): MarkerOptions {
        return MarkerOptions().position(latLong).title(title)
    }

    fun addRestaurantMarkerToMap(
        title: String,
        category: String,
        latLong: LatLng,
        context: Context
    ): MarkerOptions {
        val bitmapDrawable =
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_restaurant)
        val markerOptions = MarkerOptions().position(latLong).title(title)
            .icon(BitmapDescriptorFactory.fromBitmap(bitmapDrawable))
            .snippet(category)
        return markerOptions
    }

    fun getDistanceFromLatLngAndLocation(location: Location?, latlng: LatLng): Float {
        val newLocation = Location(LocationManager.GPS_PROVIDER)
        newLocation.latitude = latlng.latitude
        newLocation.longitude = latlng.longitude

        var distance = 0.0F
        location?.let {
            distance = it.distanceTo(newLocation)
        }
        return distance
    }
}