package com.kardabel.realestatemanager.repository

import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PropertiesRepository @Inject constructor(
    private val propertiesDao: PropertiesDao
) {

    fun getProperties(): Flow<List<PropertyWithPhoto>> = propertiesDao.getProperties()

    fun getPropertyById(id: Int): Flow<PropertyWithPhoto> = propertiesDao.getPropertyById(id)

    suspend fun insertProperty(property: PropertyEntity) {
        propertiesDao.insertProperty(property)
    }

    suspend fun deletePropertyById(id: Int) {
        propertiesDao.deletePropertyById(id)
    }

}