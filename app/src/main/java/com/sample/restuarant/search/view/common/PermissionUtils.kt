package com.sample.restuarant.search.view.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.android.support.DaggerAppCompatActivity

/* @author Dinesh Kumar 
   @creation_date 3/11/2020*/

class PermissionUtils {

    fun isLocationPermissionEnabled(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun showLocationPermissionDialog(activity: DaggerAppCompatActivity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode
        )
    }

}