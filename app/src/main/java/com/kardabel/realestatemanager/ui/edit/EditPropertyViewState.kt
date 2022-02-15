package com.kardabel.realestatemanager.ui.edit

import com.kardabel.realestatemanager.ui.details.DetailsPhotoViewState


data class EditPropertyViewState(
    val propertyId: Long,
    val type: String?,
    val description: String?,
    val surface: String?,
    val room: String?,
    val bathroom: String?,
    val bedroom: String?,
    //val interest: List<String>?,
    val address: String,
    val apartment: String?,
    val city: String,
    val county: String?,
    val zipcode: String,
    val country: String?,
    val startSale: String,
    val createLocalDateTime: String,
    val vendor: String,
    val staticMap: String,
    val visibility: Boolean,
    val price: String?,
    val uid: String?,
)