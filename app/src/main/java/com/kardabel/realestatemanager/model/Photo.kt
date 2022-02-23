package com.kardabel.realestatemanager.model

import android.graphics.Bitmap
import android.net.Uri


data class Photo(
    //val photoBitmap: Bitmap,
    var photoDescription: String,
    val photoUri: Uri,
)
