package com.sample.restuarant.search.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.sample.restuarant.search.R
import com.sample.restuarant.search.service.CurrentLocationService
import com.sample.restuarant.search.view.GoogleMapCameraHandler.Companion.BOUNDS_KEY
import com.sample.restuarant.search.view.GoogleMapCameraHandler.Companion.FETCH_NEW_RESTAURANTS
import com.sample.restuarant.search.view.common.MapsUtility
import com.sample.restuarant.search.viewmodel.RestaurantViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : RestaurantBaseActivity(), CurrentLocationService.CurrentLocationCallback,
    MapActionsCallback, View.OnClickListener {

    companion object {
        const val TAG = "MainActivity"
        const val CURRENT_LOCATION_KEY = "currentLocation"
        const val CURRENT_MAX_DISTANCE = "maxDistance"
    }

    @Inject
    lateinit var restaurantViewModel: RestaurantViewModel

    @Inject
    lateinit var mapsUtility: MapsUtility

    @Inject
    lateinit var mapViewController: MapViewController

    private lateinit var googleMapCameraHandler: GoogleMapCameraHandler

    private var locationService: CurrentLocationService? = null

    private var isDeviceRotated: Boolean = false

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
            Log.i(TAG, "Location service is disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            mapViewController.currentLocation =
                savedInstanceState.getParcelable(CURRENT_LOCATION_KEY)
            mapViewController.maxDistance = savedInstanceState.getFloat(CURRENT_MAX_DISTANCE)
            isDeviceRotated = true
        }

        current_location_icon.setOnClickListener(this)

        mapViewController.mapActionsCallback = this

        googleMapCameraHandler = GoogleMapCameraHandler(this)
        val restaurantListLiveData = restaurantViewModel.getRestaurantsObservableData()
        restaurantListLiveData.observe(this@MainActivity, Observer { restaurants ->
            Log.i(TAG, "restaurants ${restaurants.size}")
            mapViewController.loadRestaurantsOnMap(restaurants)
        })
    }

    override fun onStart() {
        super.onStart()
        Intent(this, CurrentLocationService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(CURRENT_LOCATION_KEY, mapViewController.currentLocation)
        outState.putFloat(CURRENT_MAX_DISTANCE, mapViewController.maxDistance)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    override fun onLocationPermissionGranted() {
        Log.i(TAG, "onLocationPermissionGranted")
        locationService?.let {
            it.setCurrentLocationCallback(this@MainActivity)
            it.requestLocationUpdates()
        }
    }

    override fun onLocationReceived(location: Location) {
        Log.d(TAG, "current location $location")
        val isSameLocation =
            mapsUtility.isUserInSameLocation(mapViewController.currentLocation, location)
        if (isSameLocation && !isDeviceRotated) {
            Log.i(TAG, "You seems to be in same location ${mapViewController.currentLocation}")
        } else {
            isDeviceRotated = false
            mapViewController.currentLocation = location
            val mapsFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            Log.i(TAG, "maps fragment in activity $mapsFragment")
            mapsFragment?.getMapAsync { googleMap ->
                Log.i(TAG, "updating current location")
                val latLng = LatLng(location.latitude, location.longitude)
                mapViewController.disablePOIsOnMap(googleMap)
                mapViewController.googleMap = googleMap
                mapViewController.onMapsLoaded(latLng)
                restaurantViewModel.getRestaurantsObservableData().value.let {
                    it?.let {
                        mapViewController.loadRestaurantsOnMap(it)
                    }
                }
            }

            if (!isSameLocation) {
                requestRestaurantsWithNewRadius(location)
            }
        }
    }

    override fun onMapBoundsChanged(bounds: LatLngBounds?) {
        Log.i(TAG, "onMapBoundsChanged NE ${bounds?.northeast}, SW ${bounds?.southwest}")
        var distance = 0.0F
        bounds?.let {
            distance = mapsUtility.getDistanceFromLatLngAndLocation(
                mapViewController.currentLocation,
                it.northeast
            )
                .coerceAtLeast(
                    mapsUtility.getDistanceFromLatLngAndLocation(
                        mapViewController.currentLocation,
                        it.southwest
                    )
                )
        }

        Log.i(
            TAG,
            "distance between bounds $distance, and maxDistance ${mapViewController.maxDistance}"
        )
        if (mapViewController.isZoomBeyondThreshold()) {
            mapViewController.freezeZoomAndShowMsg()
        }

        if (mapViewController.isUserZoomedOut(distance)) {
            mapViewController.maxDistance = distance
            mapViewController.currentLocation?.let {
                requestRestaurantsWithNewRadius(it)
            }
        } else {
            Log.i(TAG, "Probably the user might have zoomed in")
        }
    }

    override fun onMapPanned(bounds: LatLngBounds?) {
        val bundle = Bundle()
        bundle.putParcelable(BOUNDS_KEY, bounds)
        val message = Message.obtain()
        message.what = FETCH_NEW_RESTAURANTS
        message.data = bundle

        googleMapCameraHandler.removeMessages(FETCH_NEW_RESTAURANTS)
        googleMapCameraHandler.sendMessageDelayed(
            message,
            TimeUnit.SECONDS.toMillis(2)
        )
    }

    override fun showMaxPanReachedMsg() {
        showSnackbar(
            getString(R.string.max_zoom_level),
            getString(R.string.ok),
            View.OnClickListener {
                mapViewController.moveToUsersCurrentLocation()
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.current_location_icon -> {
                mapViewController.moveToUsersCurrentLocation()
            }
        }
    }

    private fun requestRestaurantsWithNewRadius(it: Location): Disposable? {
        return restaurantViewModel.syncRestaurants(
            it.latitude,
            it.longitude,
            mapViewController.maxDistance.toInt()
        ).doOnSubscribe { Log.d(TAG, "Subscribing for new data ${mapViewController.maxDistance}") }
            .subscribe()
    }
}
