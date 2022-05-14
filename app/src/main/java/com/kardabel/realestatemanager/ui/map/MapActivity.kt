package com.kardabel.realestatemanager.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityMapBinding
import com.kardabel.realestatemanager.model.Poi
import com.kardabel.realestatemanager.ui.details.DetailsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener {

    private var map: GoogleMap? = null

    private var _binding: ActivityMapBinding? = null
    private val binding get() = _binding!!

    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        // CUSTOM MAP WITHOUT POI WE DON'T NEED
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        map = googleMap

        // CHECK IF USER CHOSE TO SHARE HIS LOCATION
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            setUserLocation()
            map?.isMyLocationEnabled = true

        }

        // SET A LISTENER FOR MARKER CLICK
        map?.setOnMarkerClickListener(this)
    }

    private fun setUserLocation() {

        mapViewModel.getMapInfo.observe(this) { (poiList, location) ->

            map?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.userLocation.latitude,
                        location.userLocation.longitude
                    ), location.zoomFocus
                )
            )

            setPoi(poiList)

        }
    }

    private fun setPoi(poiList: List<Poi>) {

        var marker: Marker?

        // Add Marker for each poi
        for (poi in poiList) {
            if (poi.propertyLatLng != null) {
                marker = map?.addMarker(
                    MarkerOptions()
                        .position(poi.propertyLatLng)
                        .icon(
                            getBitmapFromVectorDrawable(
                                this,
                                R.drawable.property_marker
                            )?.let {
                                BitmapDescriptorFactory.fromBitmap(
                                    it
                                )
                            }
                        )

                )
                marker?.tag = poi.propertyId
            }
        }
    }

    private fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context!!, drawableId)
        assert(drawable != null)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val propertyId = marker.tag.toString()
        mapViewModel.onPropertyClicked(propertyId.toLong())
        startActivity(
            Intent(
                this,
                DetailsActivity::class.java
            )
        )
        return false

    }
}