package com.kardabel.realestatemanager.ui.create

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditPhotoFragmentViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
) : ViewModel() {

    fun deletePhoto(photo: Photo) {
        photoRepository.deletePhoto(photo)
    }

    fun editPhotoText(description: String, photo: Bitmap) {
        photoRepository.editPhotoText(description, photo)
    }
}