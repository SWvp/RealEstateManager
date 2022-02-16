package com.kardabel.realestatemanager.model

import android.graphics.Bitmap
import android.net.Uri

data class PhotoWithId(
    val photoBitmap: Bitmap,
    var photoDescription: String,
    val photoUri: Uri,
    val photoId: Int,
)
