package com.kardabel.realestatemanager.model

import android.graphics.Bitmap
import android.net.Uri


data class Photo(
    val photo: Bitmap,
    val photoDescription: String,
    val photoUri: Uri,
)
