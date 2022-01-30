package com.kardabel.realestatemanager.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityMainBinding
import com.kardabel.realestatemanager.databinding.ActivityMapBinding
import com.kardabel.realestatemanager.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback {

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
            mapViewModel.getMapInfo.observe(this, { (latLng, zoom) ->
                googleMap.clear()

                // MOVE THE CAMERA TO THE USER LOCATION
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        zoom
                    )
                )
                // DISPLAY BLUE DOT FOR USER LOCATION
                googleMap.isMyLocationEnabled = true
            })
        }
    }
}