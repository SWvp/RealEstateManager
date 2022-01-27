package com.kardabel.realestatemanager.database

import androidx.room.*
import com.kardabel.realestatemanager.model.PropertyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertiesDao {

    @Query("SELECT * FROM property")
    fun getProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM property WHERE id=:id")
    fun getPropertyById(id : Int): Flow<PropertyEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProperty(note: PropertyEntity)

    @Query("DELETE FROM property WHERE id = :id")
    suspend fun deletePropertyById(id: Int)
}