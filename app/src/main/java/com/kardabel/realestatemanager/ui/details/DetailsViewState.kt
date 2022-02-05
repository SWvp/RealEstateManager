package com.kardabel.realestatemanager.ui.details

import android.graphics.Bitmap


data class DetailsViewState(
    val propertyId: Long,
    //val photos: List<Bitmap>,
    val description: String,
    val surface: String,
    val room: String,
    val bathroom: String,
    val bedroom: String,
    val interest: List<String>?,
    val address: String,
    val apartment: String,
    val city: String,
    val county: String,
    val zipcode: String,
    val country: String,
)