package com.kardabel.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kardabel.realestatemanager.model.PhotoEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreatePhotoRepository @Inject constructor() {

    private var newPhotoList = mutableListOf<PhotoEntity>()

    private val photoToCreateLiveData = MutableLiveData<List<PhotoEntity>>()

    fun getAddedPhotoLiveData(): LiveData<List<PhotoEntity>> = photoToCreateLiveData

    fun addPhoto(photo: PhotoEntity) {
        newPhotoList.add(photo)
        photoToCreateLiveData.value = newPhotoList
    }

    fun deleteAddedPhoto(photoToDelete: PhotoEntity) {
        newPhotoList.remove(photoToDelete)
        photoToCreateLiveData.value = newPhotoList
    }

    fun editPhotoText(description: String, photoUriString: String) {
        for(photo in newPhotoList){
            if(photo.photoUri == photoUriString){
                photo.photoDescription = description
            }
        }
        photoToCreateLiveData.value = newPhotoList
    }

    fun emptyCreatePhotoList() {
        newPhotoList.clear()
    }
}