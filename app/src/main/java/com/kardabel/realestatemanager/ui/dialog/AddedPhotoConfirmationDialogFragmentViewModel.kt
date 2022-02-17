package com.kardabel.realestatemanager.ui.dialog

import androidx.lifecycle.ViewModel
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.repository.CreatePhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddedPhotoConfirmationDialogFragmentViewModel @Inject constructor(
    private val createPhotoRepository: CreatePhotoRepository,
    private val applicationDispatchers: ApplicationDispatchers

) : ViewModel() {

    fun addPhoto(photo: Photo) = createPhotoRepository.addPhoto(photo)

}