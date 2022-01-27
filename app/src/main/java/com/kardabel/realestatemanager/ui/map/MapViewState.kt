package com.kardabel.realestatemanager.ui.map

import com.google.android.gms.maps.model.LatLng

data class MapViewState (
    val latLng: LatLng,
    val zoom: Float
        )