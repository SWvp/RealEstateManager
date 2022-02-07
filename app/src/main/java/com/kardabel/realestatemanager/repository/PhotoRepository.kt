package com.kardabel.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.model.PhotoEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor() {

    private var photoList = mutableListOf<Photo>()
    private val photoToCreateLiveData = MutableLiveData<List<Photo>>()

    fun addPhoto(photo: Photo) {
        photoList.add(photo)
        photoToCreateLiveData.postValue(photoList)
    }

    fun getPhotoLiveData(): LiveData<List<Photo>> = photoToCreateLiveData
}