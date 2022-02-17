package com.kardabel.realestatemanager.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddedPhotoConfirmationDialogFragmentViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val applicationDispatchers: ApplicationDispatchers

) : ViewModel() {

    fun addPhoto(photo: Photo) = photoRepository.addPhoto(photo)

}