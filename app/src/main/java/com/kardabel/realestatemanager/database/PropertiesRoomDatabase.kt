package com.kardabel.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.utils.Converters
import kotlinx.coroutines.CoroutineScope

@Database(entities = [PropertyEntity::class, PhotoEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PropertiesRoomDatabase: RoomDatabase() {
    abstract fun propertiesDao(): PropertiesDao

    companion object {
        @Volatile
        private var INSTANCE: PropertiesRoomDatabase? = null


        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): PropertiesRoomDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PropertiesRoomDatabase::class.java,
                    "real_estate_database"
                )
                    .fallbackToDestructiveMigration() // Wipes and rebuilds instead of migrating if no Migration object.
                    //.addCallback(RealEstateDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}