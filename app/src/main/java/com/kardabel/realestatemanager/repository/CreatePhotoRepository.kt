package com.kardabel.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kardabel.realestatemanager.model.Photo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreatePhotoRepository @Inject constructor() {

    private var newPhotoList = mutableListOf<Photo>()

    private val photoToCreateLiveData = MutableLiveData<List<Photo>>()

    fun getAddedPhotoLiveData(): LiveData<List<Photo>> = photoToCreateLiveData

    fun addPhoto(photo: Photo) {
        newPhotoList.add(photo)
        photoToCreateLiveData.value = newPhotoList
    }

    fun deleteAddedPhoto(photoToDelete: Photo) {
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