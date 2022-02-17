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
    fun getProperties(): Flow<List<PropertyWithPhoto>>

    @Query("SELECT * FROM property WHERE propertyId=:id")
    fun getPropertyById(id : Long): Flow<PropertyWithPhoto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProperty(property: PropertyEntity): Long

    @Query("UPDATE property SET on_sale_status=:saleStatus WHERE propertyId =:id")
    suspend fun updatePropertySaleStatus(saleStatus: Boolean, id : Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photos: List<PhotoEntity>)

    @Update(entity = PropertyEntity::class)
    suspend fun updateProperty(property: PropertyUpdate)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePhoto(photo: PhotoEntity)

    @Query("DELETE FROM photo WHERE photoId = :id")
    suspend fun deletePhotoById(id: Int)
}