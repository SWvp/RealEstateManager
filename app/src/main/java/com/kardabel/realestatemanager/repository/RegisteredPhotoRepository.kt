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

    private val registeredPhotoLiveData = MutableLiveData<List<PhotoEntity>>()

    fun getRegisteredPhotoLiveData(): LiveData<List<PhotoEntity>> = registeredPhotoLiveData

    // Used by edit activity to send photo registered
    fun retrieveRegisteredPhoto(photoList: List<PhotoEntity>) {
        for(photo in photoList){
            registeredPhotoList.add(photo)
        }
        registeredPhotoLiveData.postValue(registeredPhotoList)
    }

    fun deleteRegisteredPhoto(photoId: Int) {
        for(photo in registeredPhotoList){
            if(photo.photoId == photoId){
                registeredPhotoList.remove(photo)
                break
            }
        }
        // photoToCreateLiveData.value = newPhotoList
        registeredPhotoLiveData.value = registeredPhotoList
    }

    fun editPhotoText(description: String, photoUri: Uri) {
        for(photo in registeredPhotoList){
            if(photo.photoUri == photoUri.toString()){
                photo.photoDescription = description
            }
        }
        registeredPhotoLiveData.value = registeredPhotoList
    }

    fun emptyRegisteredPhotoList() {
        registeredPhotoList.clear()
    }
}