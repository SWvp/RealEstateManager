package com.kardabel.realestatemanager.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PhotoWithId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor() {

    private var photoList = mutableListOf<Photo>()
    private var photoEntityList = mutableListOf<PhotoEntity>()

    private val photoToCreateLiveData = MutableLiveData<List<Photo>>()
    private val photoToEditLiveData = MutableLiveData<List<PhotoEntity>>()

    fun getAddedPhotoLiveData(): LiveData<List<Photo>> = photoToCreateLiveData
    fun getOldPhotoLiveData(): LiveData<List<PhotoEntity>> = photoToEditLiveData

    // Retrieve by create activity
    fun addPhoto(photo: Photo) {
        photoList.add(photo)
        photoToCreateLiveData.value = photoList
    }

    // Retrieve by edit activity
    fun addPhotoEntity(photoList: List<PhotoEntity>) {
        for(photo in photoList){
            photoEntityList.add(photo)
        }
        photoToEditLiveData.postValue(photoEntityList)
    }

    fun deletePhoto(photoToDelete: Photo) {
        photoList.remove(photoToDelete)
        photoToCreateLiveData.value = photoList
    }

    fun deletePhotoEntity(photoId: Int) {
        for(photo in photoEntityList){
            if(photo.photoId == photoId){
                photoEntityList.remove(photo)
                break
            }
        }
        photoToCreateLiveData.value = photoList
        photoToEditLiveData.value = photoEntityList
    }

    fun editPhotoText(description: String, photoToEdit: Bitmap) {
        for(photo in photoList){
            if(photo.photoBitmap == photoToEdit){
                photo.photoDescription = description
            }
        }
        photoToCreateLiveData.value = photoList
    }

    fun emptyPhotoList() {
        photoList.clear()
        photoEntityList.clear()
    }
}