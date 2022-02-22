package com.kardabel.realestatemanager.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.model.PhotoEntity
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

    fun editPhotoText(description: String, photoUri: Uri) {
        for(photo in newPhotoList){
            if(photo.photoUri == photoUri){
                photo.photoDescription = description
            }
        }
        photoToCreateLiveData.value = newPhotoList
    }

    fun emptyCreatePhotoList() {
        newPhotoList.clear()
        //photoToCreateLiveData.value = newPhotoList
    }
}