package com.sample.restuarant.search.view

import com.google.android.gms.maps.model.LatLngBounds

/* @author Dinesh Kumar 
   @creation_date 3/13/2020*/

interface MapActionsCallback {
    fun onMapPanned(bounds: LatLngBounds?)

    fun showMaxPanReachedMsg()
}