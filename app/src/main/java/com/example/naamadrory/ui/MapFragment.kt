package com.example.naamadrory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.naamadrory.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var pendingLatLng: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val israel = LatLng(31.0461, 34.8516)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(israel, 6f))

        pendingLatLng?.let {
            zoom(it.latitude, it.longitude)
            pendingLatLng = null
        }
    }

    fun zoom(lat: Double, lon: Double) {
        val location = LatLng(lat, lon)

        if (googleMap != null) {
            googleMap?.clear()
            googleMap?.addMarker(MarkerOptions().position(location).title("High Score"))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
        } else {
            pendingLatLng = location
        }
    }
}
