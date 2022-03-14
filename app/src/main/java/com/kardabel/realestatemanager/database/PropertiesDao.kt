package com.kardabel.realestatemanager.database

import android.database.Cursor
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

    @Transaction
    @Query("SELECT * FROM property WHERE propertyId=:id")
    fun getPropertyById(id : Long): Flow<PropertyWithPhoto>

    @Transaction
    @Query("SELECT * FROM property WHERE propertyId=:id")
    suspend fun getPropertyByIdNoFlow(id : Long): PropertyWithPhoto

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProperty(property: PropertyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(property: List<PropertyEntity>)

    @Query("UPDATE property SET on_sale_status=:saleStatus WHERE propertyId =:id")
    suspend fun updatePropertySaleStatus(saleStatus: String, id : Long)

    @Query("UPDATE property SET purchase_date=:saleDate WHERE propertyId =:id")
    suspend fun updatePropertySaleDate(saleDate: String, id : Long)

    @Update(entity = PropertyEntity::class)
    suspend fun updateLightProperty(property: PropertyUpdate)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProperty(property: PropertyEntity)

    @Query("SELECT * FROM photo")
    fun getPhotos(): List<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photos: PhotoEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePhoto(photo: PhotoEntity)

    @Query("DELETE FROM photo WHERE photoId = :id")
    suspend fun deletePhotoById(id: Int)

    @Query("DELETE FROM photo WHERE photoId IN (:photoId)")
    suspend fun deletePhotoById(photoId: List<Int>)

    @Query("DELETE FROM photo WHERE property_owner_id = :propertyId")
    suspend fun deleteAllPropertyPhotos(propertyId: Long)



    @Query("SELECT * FROM property WHERE propertyId = :id")
    fun getPropertiesWithCursor(vararg id: Long): Cursor
}