package com.kardabel.realestatemanager.ui.create

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPhotoFragmentViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val propertiesDao: PropertiesDao,
    private val applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {

    fun deletePhotoFromRepository(photo: Photo) {
        photoRepository.deletePhoto(photo)
    }

    fun deletePhotoFromDataBase(photoId: Int) {
        viewModelScope.launch(applicationDispatchers.ioDispatcher) {
            propertiesDao.deletePhotoById(photoId)
        }
    }

    fun editPhotoText(description: String, photo: Bitmap) {
        photoRepository.editPhotoText(description, photo)
    }
}