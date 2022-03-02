package com.kardabel.realestatemanager.model

import com.google.android.gms.maps.model.LatLng

data class Poi(
    val propertyId: Long,
    val propertyLatLng: LatLng?
)