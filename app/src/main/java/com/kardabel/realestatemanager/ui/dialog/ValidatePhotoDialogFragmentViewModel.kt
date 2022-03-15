package com.kardabel.realestatemanager.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.repository.CreatePhotoRepository
import com.kardabel.realestatemanager.repository.RegisteredPhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ValidatePhotoDialogFragmentViewModel @Inject constructor(
    private val createPhotoRepository: CreatePhotoRepository,
    private val registeredPhotoRepository: RegisteredPhotoRepository,
    private val propertiesDao: PropertiesDao,
    private val applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {

    fun deletePhotoFromRepository(photo: PhotoEntity) {
        createPhotoRepository.deleteAddedPhoto(photo)
    }

    fun deleteRegisteredPhotoFromRepository(photoId: Int) {
        registeredPhotoRepository.deleteRegisteredPhoto(photoId)
    }


    fun editPhotoText(description: String, photoUri: String) {
        createPhotoRepository.editPhotoText(description, photoUri)
        registeredPhotoRepository.editPhotoText(description, photoUri)

    }

    fun updateRegisteredPhoto(photo: PhotoEntity){
        viewModelScope.launch(applicationDispatchers.ioDispatcher) {
            propertiesDao.updatePhoto(photo)
        }
    }
}