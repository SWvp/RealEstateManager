package com.kardabel.realestatemanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kardabel.realestatemanager.model.PropertyEntity

@Database(entities = [PropertyEntity::class], version = 1, exportSchema = false)
abstract class PropertiesRoomDatabase: RoomDatabase() {

    abstract fun propertiesDao(): PropertiesDao
}