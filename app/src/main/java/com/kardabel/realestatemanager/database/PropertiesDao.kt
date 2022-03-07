package com.kardabel.realestatemanager.database

import androidx.room.*
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.model.PropertyUpdate
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertiesDao {

    @Transaction
    @Query("SELECT * FROM property")
    fun getPropertiesWithPhoto(): Flow<List<PropertyWithPhoto>>

    @Query("SELECT * FROM property")
    fun getProperties(): List<PropertyEntity>

    @Query("SELECT * FROM property WHERE propertyId=:id")
    fun getPropertyById(id : Long): Flow<PropertyWithPhoto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProperty(property: PropertyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(property: List<PropertyEntity>)

    @Query("UPDATE property SET on_sale_status=:saleStatus WHERE propertyId =:id")
    suspend fun updatePropertySaleStatus(saleStatus: Boolean, id : Long)

    @Update(entity = PropertyEntity::class)
    suspend fun updateProperty(property: PropertyUpdate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePhoto(photo: PhotoEntity)

    @Query("DELETE FROM photo WHERE photoId = :id")
    suspend fun deletePhotoById(id: Int)

    @Query("DELETE FROM photo WHERE photoId IN (:photoId)")
    suspend fun deletePhotoById(photoId: List<Int>)
}