package com.sample.restuarant.search.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.sample.restuarant.search.R
import com.sample.restuarant.search.model.RestaurantModel
import com.sample.restuarant.search.service.CurrentLocationService
import com.sample.restuarant.search.view.GoogleMapCameraHandler.Companion.BOUNDS_KEY
import com.sample.restuarant.search.view.GoogleMapCameraHandler.Companion.FETCH_NEW_RESTAURANTS
import com.sample.restuarant.search.view.common.MapsUtility
import com.sample.restuarant.search.viewmodel.RestaurantViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : RestaurantBaseActivity(), CurrentLocationService.CurrentLocationCallback {

    private val tag = "MainActivity"

    companion object {
        const val CURRENT_LOCATION_KEY = "currentLocation"
        const val CURRENT_MAX_DISTANCE = "maxDistance"
        const val MAX_THRESHOLD = 100000F
    }

    @Inject
    lateinit var restaurantViewModel: RestaurantViewModel

    @Inject
    lateinit var mapsUtility: MapsUtility

    private lateinit var googleMapCameraHandler: GoogleMapCameraHandler

    private var googleMap: GoogleMap? = null

    private var currentLocation: Location? = null
    private var maxDistance: Float = 1000.0F

    private var locationService: CurrentLocationService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val locationBinder = service as CurrentLocationService.LocationBinder
            locationService = locationBinder.getLocationService()
            locationService?.setCurrentLocationCallback(this@MainActivity)
            if (permissionUtils.isLocationPermissionEnabled(this@MainActivity)) {
                locationService?.requestLocationUpdates()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(tag, "Location service is disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            currentLocation = savedInstanceState.getParcelable(CURRENT_LOCATION_KEY)
            maxDistance = savedInstanceState.getFloat(CURRENT_MAX_DISTANCE)
        }

        current_location_icon.setOnClickListener {
            moveToUsersCurrentLocation()
        }

        googleMapCameraHandler = GoogleMapCameraHandler(this)
        val restaurantListLiveData = restaurantViewModel.getRestaurantsObservableData()
        restaurantListLiveData.observe(this@MainActivity, Observer { restaurants ->
            Log.i(tag, "restaurants ${restaurants.size}")
            loadRestaurantsOnMap(restaurants)
        })
    }

    override fun onStart() {
        super.onStart()
        Intent(this, CurrentLocationService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(CURRENT_LOCATION_KEY, currentLocation)
        outState.putFloat(CURRENT_MAX_DISTANCE, maxDistance)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    override fun onLocationPermissionGranted() {
        Log.i(tag, "onLocationPermissionGranted")
        locationService?.let {
            it.setCurrentLocationCallback(this@MainActivity)
            it.requestLocationUpdates()
        }
    }

    override fun onLocationReceived(location: Location) {
        Log.d(tag, "current location $location")

        if (isUserInSameLocation(location) && this.googleMap != null) {
            Log.i(tag, "You seems to be in same location ${this.currentLocation}")
        } else {
            this.currentLocation = location
            val mapsFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapsFragment.getMapAsync { googleMap ->
                Log.i(tag, "updating current location")
                val latLng = LatLng(location.latitude, location.longitude)
                disablePOIsOnMap(googleMap)
                this.googleMap = googleMap
                onMapsLoaded(latLng)
            }

            if (!isUserInSameLocation(location)) {
                requestRestaurantsWithNewRadius(location)
            }
        }
    }

    override fun onMapBoundsChanged(bounds: LatLngBounds?) {
        Log.i(tag, "onMapBoundsChanged NE ${bounds?.northeast}, SW ${bounds?.southwest}")
        var distance = 0.0F
        bounds?.let {
            distance = mapsUtility.getDistanceFromLatLngAndLocation(currentLocation, it.northeast)
                .coerceAtLeast(
                    mapsUtility.getDistanceFromLatLngAndLocation(
                        currentLocation,
                        it.southwest
                    )
                )
        }

        Log.i(tag, "distance between bounds $distance, and maxDistance $maxDistance")
        if (isZoomBeyondThreshold()) {
            freezeZoomAndShowMsg()
        }

        if (isUserZoomedOut(distance)) {
            maxDistance = distance
            currentLocation?.let {
                requestRestaurantsWithNewRadius(it)
            }
        } else {
            Log.i(tag, "Probably the user might have zoomed in")
        }
    }

    private fun isUserZoomedOut(distance: Float) = distance > maxDistance

    private fun disablePOIsOnMap(googleMap: GoogleMap) {
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json
            )
        )
    }

    private fun isUserInSameLocation(location: Location): Boolean {
        return (this.currentLocation != null
                && (mapsUtility.isSameGeoPosition(this.currentLocation?.latitude, location.latitude)
                && mapsUtility.isSameGeoPosition(
            this.currentLocation?.longitude,
            location.longitude
        )))
    }

    private fun onMapsLoaded(latLng: LatLng) {
        Log.i(tag, "loading map with current location")
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = latLng.latitude
        location.longitude = latLng.longitude

        val distanceTo = this.currentLocation?.distanceTo(location)
        Log.i(tag, "Distance from current location $distanceTo")
        googleMap?.let { map ->
            val center = CameraUpdateFactory.newLatLng(latLng)
            val zoom = CameraUpdateFactory.zoomTo(15f)
            map.moveCamera(zoom)
            map.animateCamera(center)
            map.setOnCameraIdleListener {
                val bounds = map.projection.visibleRegion.latLngBounds
                Log.i(tag, "camera bounds ne ${bounds.northeast}, sw ${bounds.southwest}")
                googleMapCameraHandler.removeMessages(FETCH_NEW_RESTAURANTS)
                notifyBoundsChangedWithDelay(bounds)
            }
        }

        restaurantViewModel.getRestaurantsObservableData().value.let {
            it?.let {
                loadRestaurantsOnMap(it)
            }
        }
    }

    private fun notifyBoundsChangedWithDelay(bounds: LatLngBounds?) {
        val bundle = Bundle()
        bundle.putParcelable(BOUNDS_KEY, bounds)
        val message = Message.obtain()
        message.what = FETCH_NEW_RESTAURANTS
        message.data = bundle

        googleMapCameraHandler.sendMessageDelayed(
            message,
            TimeUnit.SECONDS.toMillis(2)
        )
    }

    private fun loadRestaurantsOnMap(restaurants: List<RestaurantModel>) {
        restaurants.forEach { restaurant ->
            Log.i(tag, "adding markers to $googleMap for ${restaurants.size}")
            googleMap?.let { map ->
                val location = LatLng(
                    restaurant.restaurantLocationModel.latitude,
                    restaurant.restaurantLocationModel.longitude
                )
                map.addMarker(
                    mapsUtility.addRestaurantMarkerToMap(
                        restaurant.restaurantName, location, this@MainActivity
                    )
                ).showInfoWindow()
            }
        }
    }

    private fun moveToUsersCurrentLocation() {
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

    private fun isZoomBeyondThreshold() = maxDistance > MAX_THRESHOLD

    private fun requestRestaurantsWithNewRadius(it: Location): Disposable? {
        return restaurantViewModel.syncRestaurants(
            it.latitude,
            it.longitude,
            maxDistance.toInt()
        ).doOnSubscribe { Log.d(tag, "Subscribing for new data $maxDistance") }
            .subscribe()
    }

    private fun freezeZoomAndShowMsg() {
        googleMap?.uiSettings?.setAllGesturesEnabled(false)
        showSnackbar(
            getString(R.string.max_zoom_level),
            getString(R.string.ok),
            View.OnClickListener {
                moveToUsersCurrentLocation()
            })
    }
}
