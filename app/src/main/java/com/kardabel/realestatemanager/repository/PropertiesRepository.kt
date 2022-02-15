package com.kardabel.realestatemanager.repository

import androidx.annotation.WorkerThread
import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.model.PropertyUpdate
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class PropertiesRepository @Inject constructor(
    private val propertiesDao: PropertiesDao
) {

    fun getProperties(): Flow<List<PropertyWithPhoto>> = propertiesDao.getProperties()

    fun getPropertyById(id: Long): Flow<PropertyWithPhoto> = propertiesDao.getPropertyById(id)

    @WorkerThread
    suspend fun insertProperty(property: PropertyEntity)= withContext(Dispatchers.IO)  {
        propertiesDao.insertProperty(property)
    }

    suspend fun insertPhoto(photo: PhotoEntity) {
        propertiesDao.insertPhoto(photo)
    }

    suspend fun updateProperty(property: PropertyUpdate){
        propertiesDao.updateProperty(property)
    }

    suspend fun insertNewPhoto(photo: PhotoEntity){
        propertiesDao.insertPhoto(photo)
    }

}