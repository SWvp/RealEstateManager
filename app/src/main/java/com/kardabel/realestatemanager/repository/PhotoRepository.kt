package com.kardabel.realestatemanager.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kardabel.realestatemanager.model.Photo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor() {

    private var photoList = mutableListOf<Photo>()
    private val photoToCreateLiveData = MutableLiveData<List<Photo>>()

    fun getPhotoLiveData(): LiveData<List<Photo>> = photoToCreateLiveData

    fun addPhoto(photo: Photo) {
        photoList.add(photo)
        photoToCreateLiveData.postValue(photoList)
    }

    fun deletePhoto(photoToDelete: Photo) {
        photoList.remove(photoToDelete)
        photoToCreateLiveData.postValue(photoList)
    }

    fun editPhotoText(description: String, photoToEdit: Bitmap) {
        for(photo in photoList){
            if(photo.photoBitmap == photoToEdit){
                photo.photoDescription = description
            }
        }
        photoToCreateLiveData.postValue(photoList)
    }

    fun emptyPhotoList() {
        photoList.clear()
    }
}