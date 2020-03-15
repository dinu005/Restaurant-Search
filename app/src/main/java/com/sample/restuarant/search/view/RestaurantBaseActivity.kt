package com.sample.restuarant.search.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.sample.restuarant.search.BuildConfig
import com.sample.restuarant.search.R
import com.sample.restuarant.search.view.common.PermissionUtils
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class RestaurantBaseActivity : DaggerAppCompatActivity() {

    private val tag = "RestaurantBaseActivity"

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 100
    }

    @Inject
    lateinit var permissionUtils: PermissionUtils

    override fun onResume() {
        super.onResume()
        if (!permissionUtils.isLocationPermissionEnabled(this)) {
            requestPermission()
        }
    }

    private fun requestPermission() {
        val shouldShowRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        if (shouldShowRationale) {
            showSnackbar(
                getString(R.string.permission_msg),
                getString(R.string.allow),
                View.OnClickListener {
                    permissionUtils.showLocationPermissionDialog(this, REQUEST_LOCATION_PERMISSION)
                })
        } else {
            Log.i(tag, "Requesting permission")
            permissionUtils.showLocationPermissionDialog(this, REQUEST_LOCATION_PERMISSION)
        }
    }

    protected fun showSnackbar(msg: String, action: String, listener: View.OnClickListener) {
        Snackbar.make(
            findViewById(android.R.id.content), msg,
            BaseTransientBottomBar.LENGTH_INDEFINITE
        )
            .setAction(action, listener)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showSnackbar(
                        getString(R.string.permission_denied_msg),
                        getString(R.string.allow),
                        View.OnClickListener {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        })
                } else {
                    onLocationPermissionGranted()
                }
            }
        }
    }

    abstract fun onLocationPermissionGranted()

    abstract fun onMapBoundsChanged(bounds: LatLngBounds?)
}
