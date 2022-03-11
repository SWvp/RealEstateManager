package com.kardabel.realestatemanager.ui.edit

data class EditPropertyPhotoViewState(
    var photoDescription: String,
    val photoUri: String,
    val photoId: Int?,
    val propertyOwnerId: Long?,
    val photoTimestamp: String,
    val photoCreationDate: String,
)