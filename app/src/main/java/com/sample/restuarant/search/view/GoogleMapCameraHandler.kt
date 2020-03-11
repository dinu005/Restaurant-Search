package com.sample.restuarant.search.view

import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

/* @author Dinesh Kumar 
   @creation_date 3/11/2020*/

class GoogleMapCameraHandler(callback: RestaurantBaseActivity) : Handler() {

    private val reference: WeakReference<RestaurantBaseActivity> = WeakReference(callback)

    companion object {
        val FETCH_NEW_RESTAURANTS = 1294
        val BOUNDS_KEY = "new_bounds"
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            FETCH_NEW_RESTAURANTS -> {
                reference.get()?.onMapBoundsChanged(msg.data.getParcelable(BOUNDS_KEY))
            }
        }
    }
}