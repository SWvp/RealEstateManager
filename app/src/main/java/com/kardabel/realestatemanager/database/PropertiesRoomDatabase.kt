package com.kardabel.realestatemanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.utils.Converters

@Database(entities = [PropertyEntity::class, PhotoEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PropertiesRoomDatabase: RoomDatabase() {

    abstract fun propertiesDao(): PropertiesDao
}