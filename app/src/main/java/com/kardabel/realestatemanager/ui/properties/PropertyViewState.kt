package com.kardabel.realestatemanager.ui.properties

import android.graphics.Bitmap

data class PropertyViewState(
    val id: Long,
    val type: String?,
    val county: String?,
    val price: String?,
    //val photoBitmap: Bitmap
)