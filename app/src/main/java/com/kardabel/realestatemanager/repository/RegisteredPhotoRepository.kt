package com.kardabel.realestatemanager.repository

import android.net.Uri
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
        for (photo in originalRegisteredPhotoList) {
            if (photo.photoId == photoId) {
                registeredPhotoList.remove(photo)
                break
            }
        }
        registeredPhotoLiveData.postValue(registeredPhotoList)
    }

    fun editPhotoText(description: String, photoUri: Uri) {
        for (photo in registeredPhotoList) {
            if (photo.photoUri == photoUri.toString()) {
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