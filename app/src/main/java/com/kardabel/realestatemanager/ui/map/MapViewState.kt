package com.kardabel.realestatemanager.ui.map

import com.google.android.gms.maps.model.LatLng
import com.kardabel.realestatemanager.model.Poi
import com.kardabel.realestatemanager.model.UserLocation

data class MapViewState (
    val poi: List<Poi>,
    val userLocation: UserLocation,

    )