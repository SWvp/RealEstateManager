package com.kardabel.realestatemanager.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.database.PropertiesRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): PropertiesRoomDatabase = Room.databaseBuilder(
        appContext,
        PropertiesRoomDatabase::class.java,
        "properties_database"
    ).build()

    @Provides
    @Singleton
    fun provideProperties(database: PropertiesRoomDatabase): PropertiesDao = database.propertiesDao()

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}