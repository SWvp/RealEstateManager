package com.kardabel.realestatemanager.model

import com.google.android.gms.maps.model.LatLng


data class UserLocation(
    val userLocation: LatLng,
    val zoomFocus: Float

)
