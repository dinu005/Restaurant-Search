package com.sample.restuarant.search.view

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.sample.restuarant.search.R
import com.sample.restuarant.search.model.RestaurantModel
import com.sample.restuarant.search.view.common.MapsUtility

/* @author Dinesh Kumar 
   @creation_date 3/12/2020*/

class MapViewController(private val mapsUtility: MapsUtility, val context: Context) {

    var googleMap: GoogleMap? = null
    var currentLocation: Location? = null
    var maxDistance: Float = 1000.0F
    var mapActionsCallback: MapActionsCallback? = null

    companion object {
        const val MAX_THRESHOLD = 100000F
    }

    fun disablePOIsOnMap(googleMap: GoogleMap) {
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                context, R.raw.style_json
            )
        )
    }

    fun onMapsLoaded(latLng: LatLng) {
        Log.i(MainActivity.TAG, "loading map with current location")
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = latLng.latitude
        location.longitude = latLng.longitude

        val distanceTo = this.currentLocation?.distanceTo(location)
        Log.i(MainActivity.TAG, "Distance from current location $distanceTo")
        googleMap?.let { map ->
            val center = CameraUpdateFactory.newLatLng(latLng)
            val zoom = CameraUpdateFactory.zoomTo(15f)
            map.moveCamera(zoom)
            map.animateCamera(center)
            map.setOnCameraIdleListener {
                val bounds = map.projection.visibleRegion.latLngBounds
                Log.i(
                    MainActivity.TAG,
                    "camera bounds ne ${bounds.northeast}, sw ${bounds.southwest}"
                )
                mapActionsCallback?.onMapPanned(bounds)
            }
        }
    }

    fun loadRestaurantsOnMap(restaurants: List<RestaurantModel>) {
        restaurants.forEach { restaurant ->
            Log.i(MainActivity.TAG, "adding markers to $googleMap for ${restaurants.size}")
            googleMap?.let { map ->
                val location = LatLng(
                    restaurant.restaurantLocationModel.latitude,
                    restaurant.restaurantLocationModel.longitude
                )
                map.addMarker(
                    mapsUtility.addRestaurantMarkerToMap(
                        restaurant.restaurantName, restaurant.restaurantCategoryModel.categoryName,
                        location, context
                    )
                ).showInfoWindow()
            }
        }
    }

    fun moveToUsersCurrentLocation() {
        googleMap?.let { map ->
            map.uiSettings.setAllGesturesEnabled(true)
            currentLocation?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                val center = CameraUpdateFactory.newLatLng(latLng)
                val zoom = CameraUpdateFactory.zoomTo(15f)
                map.moveCamera(zoom)
                map.animateCamera(center)
                if (isZoomBeyondThreshold()) {
                    maxDistance = MAX_THRESHOLD
                }
            }
        }
    }

    fun isUserZoomedOut(distance: Float) = distance > maxDistance

    fun isZoomBeyondThreshold() = maxDistance > MAX_THRESHOLD

    fun freezeZoomAndShowMsg() {
        googleMap?.uiSettings?.setAllGesturesEnabled(false)
    }
}