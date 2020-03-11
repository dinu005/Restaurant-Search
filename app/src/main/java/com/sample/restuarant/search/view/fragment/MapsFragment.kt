package com.sample.restuarant.search.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.sample.restuarant.search.R

class MapsFragment : MapFragment() {

    private val logTag = "MapsFragment"
    private var googleMap: GoogleMap? = null

    private val callback = OnMapReadyCallback { googleMap ->
        Log.d(logTag, "on map ready callback")
        this.googleMap = googleMap
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        Log.d(logTag, "map fragment $mapFragment")
        mapFragment?.getMapAsync(callback)
    }
}