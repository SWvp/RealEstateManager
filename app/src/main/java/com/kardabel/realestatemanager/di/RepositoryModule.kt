package com.kardabel.realestatemanager.di

import android.content.Context
import com.kardabel.realestatemanager.repository.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideLocationRepository(@ApplicationContext context: Context): LocationRepository{
        return LocationRepository(context)
    }
}