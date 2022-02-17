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
   // private var registeredPhotoList = mutableListOf<PhotoEntity>()

    private val photoToCreateLiveData = MutableLiveData<List<Photo>>()
   // private val registeredPhotoLiveData = MutableLiveData<List<PhotoEntity>>()

    fun getAddedPhotoLiveData(): LiveData<List<Photo>> = photoToCreateLiveData
   // fun getRegisteredPhotoLiveData(): LiveData<List<PhotoEntity>> = registeredPhotoLiveData

    // Used by create and edit activity
    fun addPhoto(photo: Photo) {
        newPhotoList.add(photo)
        photoToCreateLiveData.value = newPhotoList
    }

// // Used by edit activity
// fun retrieveRegisteredPhoto(photoList: List<PhotoEntity>) {
//     for(photo in photoList){
//         registeredPhotoList.add(photo)
//     }
//     registeredPhotoLiveData.postValue(registeredPhotoList)
// }

    fun deleteAddedPhoto(photoToDelete: Photo) {
        newPhotoList.remove(photoToDelete)
        photoToCreateLiveData.value = newPhotoList
    }

 // fun deleteRegisteredPhoto(photoId: Int) {
 //     for(photo in registeredPhotoList){
 //         if(photo.photoId == photoId){
 //             registeredPhotoList.remove(photo)
 //             break
 //         }
 //     }
 //    // photoToCreateLiveData.value = newPhotoList
 //     registeredPhotoLiveData.value = registeredPhotoList
 // }

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
        //registeredPhotoList.clear()
    }
}