package com.kardabel.realestatemanager.ui.properties

import android.graphics.Bitmap

data class PropertyViewState(
    val propertyId: Long,
    val type: String?,
    val county: String?,
    val price: String?,
    val saleStatus: String,
    val vendor: String,
    val photoBitmap: Bitmap
)