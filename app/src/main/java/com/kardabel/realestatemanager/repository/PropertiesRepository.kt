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

    fun getPropertiesWithPhotosFlow(): Flow<List<PropertyWithPhoto>> = propertiesDao.getPropertiesWithPhoto()

    fun getProperties(): List<PropertyEntity> = propertiesDao.getProperties()

    fun getPropertyById(id: Long): Flow<PropertyWithPhoto> = propertiesDao.getPropertyById(id)

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////  PROPERTIES ///////////////////////////////////////////////////

    @WorkerThread
    suspend fun insertProperty(property: PropertyEntity) = withContext(Dispatchers.IO) {
        propertiesDao.insertProperty(property)
    }

    suspend fun updateLightProperty(property: PropertyUpdate) {
        propertiesDao.updateLightProperty(property)
    }

    suspend fun updateProperty(property: PropertyEntity) {
        propertiesDao.updateProperty(property)
    }

    suspend fun updateSaleStatus(saleStatus: String, propertyId: Long) {
        propertiesDao.updatePropertySaleStatus(saleStatus, propertyId)
    }

    suspend fun updateSaleDate(saleDate: String, propertyId: Long) {
        propertiesDao.updatePropertySaleDate(saleDate, propertyId)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////  PHOTOS ///////////////////////////////////////////////////////

    suspend fun insertPhotos(photos: List<PhotoEntity>) {
        propertiesDao.insertPhotos(photos)
    }

    suspend fun insertPhoto(photo: PhotoEntity) {
        propertiesDao.insertPhoto(photo)
    }

    suspend fun deletePhotosById(photoId: List<Int>) {
        propertiesDao.deletePhotoById(photoId)
    }

    suspend fun deleteAllPropertyPhotos(propertyId: Long) {
        propertiesDao.deleteAllPropertyPhotos(propertyId)
    }

}