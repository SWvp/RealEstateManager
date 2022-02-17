package com.kardabel.realestatemanager.ui.properties

import android.graphics.Bitmap
import androidx.annotation.ColorInt

data class PropertyViewState(
    val propertyId: Long,
    val type: String,
    val county: String,
    val price: String?,
    val saleStatus: String,
    @ColorInt
    val saleColor: Int,
    val vendor: String,
    val photoBitmap: Bitmap
)