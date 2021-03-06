package com.kardabel.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kardabel.realestatemanager.model.PhotoEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisteredPhotoRepository @Inject constructor() {

    private var registeredPhotoList = mutableListOf<PhotoEntity>()
    private var originalRegisteredPhotoList = mutableListOf<PhotoEntity>()

    private val registeredPhotoLiveData = MutableLiveData<List<PhotoEntity>>()

    fun getRegisteredPhotoLiveData(): LiveData<List<PhotoEntity>> = registeredPhotoLiveData

    // Used by edit activity to send photo registered
    fun sendRegisteredPhotoToRepository(photoList: List<PhotoEntity>) {

        if (originalRegisteredPhotoList.isEmpty()) {
            for (photo in photoList) {
                originalRegisteredPhotoList.add(photo)
            }
            registeredPhotoList = originalRegisteredPhotoList
            registeredPhotoLiveData.postValue(originalRegisteredPhotoList)
        }
    }

    fun deleteRegisteredPhoto(photoId: Int) {
        val photoListToRemove = mutableListOf<PhotoEntity>()
        for (photo in originalRegisteredPhotoList) {
            if (photo.photoId == photoId) {
                photoListToRemove.add(photo)
            }
        }
        registeredPhotoList.removeAll(photoListToRemove)
        registeredPhotoLiveData.postValue(registeredPhotoList)
    }

    fun editPhotoText(description: String, photoUriString: String) {
        for (photo in registeredPhotoList) {
            if (photo.photoUri == photoUriString) {
                photo.photoDescription = description
            }
        }
        registeredPhotoLiveData.value = registeredPhotoList
    }

    fun emptyRegisteredPhotoList() {
        registeredPhotoList.clear()
        registeredPhotoLiveData.postValue(registeredPhotoList)
    }
}