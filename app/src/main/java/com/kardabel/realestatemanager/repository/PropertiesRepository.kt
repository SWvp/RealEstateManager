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

    fun getProperties(): Flow<List<PropertyWithPhoto>> = propertiesDao.getPropertiesWithPhoto()

    fun getPropertyById(id: Long): Flow<PropertyWithPhoto> = propertiesDao.getPropertyById(id)

    @WorkerThread
    suspend fun insertProperty(property: PropertyEntity) = withContext(Dispatchers.IO) {
        propertiesDao.insertProperty(property)
    }

    suspend fun updateProperty(property: PropertyUpdate) {
        propertiesDao.updateProperty(property)
    }

    suspend fun insertPhotos(photos: List<PhotoEntity>) {
        propertiesDao.insertPhotos(photos)
    }

    suspend fun deletePhotos(photoId: List<Int>) {
        propertiesDao.deletePhotoById(photoId)
    }

    suspend fun updateSaleStatus(saleStatus: Boolean, propertyId: Long) {
        propertiesDao.updatePropertySaleStatus(saleStatus, propertyId)
    }
}