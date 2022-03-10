package com.kardabel.realestatemanager.ui.edit

import android.graphics.Bitmap
import android.net.Uri

data class EditPropertyPhotoViewState(
    var photoDescription: String,
    val photoUri: String,
    val photoId: Int?,
    val propertyOwnerId: Long?,
    val photoTimestamp: String,
    val photoCreationDate: String,
)