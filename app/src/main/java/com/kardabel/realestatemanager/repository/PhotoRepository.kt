package com.kardabel.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kardabel.realestatemanager.model.PhotoEntity
import javax.inject.Inject

class PhotoRepository @Inject constructor() {

    private var photo = mutableListOf<PhotoEntity>()


    // fun getPhoto(): Flow<List<PhotoEntity>> = flow{
    //     val latestPhotoList = photo
    //     emit(latestPhotoList)
    // }

// fun addPhoto(photoEntity: PhotoEntity){
//     photo.add(photoEntity)
// }

    private val photoToCreateLiveData = MutableLiveData<List<PhotoEntity>>()

    fun addPhoto(photoEntity: PhotoEntity) {
        photo.add(photoEntity)
        photoToCreateLiveData.value = photo
    }

    fun getPhotoLiveData(): LiveData<List<PhotoEntity>> = photoToCreateLiveData
}