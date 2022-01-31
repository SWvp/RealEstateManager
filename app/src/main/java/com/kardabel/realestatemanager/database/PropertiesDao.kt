package com.kardabel.realestatemanager.database

import androidx.room.*
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertiesDao {

    @Transaction
    @Query("SELECT * FROM property")
    fun getProperties(): Flow<List<PropertyWithPhoto>>

    @Query("SELECT * FROM property WHERE propertyId=:id")
    fun getPropertyById(id : Int): Flow<PropertyWithPhoto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProperty(property: PropertyEntity)

    @Query("DELETE FROM property WHERE user_id = :id")
    suspend fun deletePropertyById(id: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Query("DELETE FROM photo WHERE photoId = :id")
    suspend fun deletePhotoById(id: Int)
}