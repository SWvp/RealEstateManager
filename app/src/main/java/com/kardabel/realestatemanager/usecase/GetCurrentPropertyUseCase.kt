package com.kardabel.realestatemanager.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.repository.CurrentPropertyIdRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import com.kardabel.realestatemanager.ui.details.DetailsPhotoViewState
import com.kardabel.realestatemanager.ui.details.DetailsViewState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentPropertyUseCase @Inject constructor(
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val propertiesRepository: PropertiesRepository,
    private val applicationDispatchers: ApplicationDispatchers,

    ) {

    val detailsLiveData: LiveData<DetailsViewState> =
        currentPropertyIdRepository.currentPropertyIdLiveData.switchMap { id ->
            propertiesRepository.getPropertyById(id).map {
                DetailsViewState(
                    propertyId = it.propertyEntity.propertyId,
                    photos = it.photo.map { photoEntity ->
                        DetailsPhotoViewState(
                            photoEntity.photo,
                            photoEntity.photoDescription,
                        )
                    },
                    description = it.propertyEntity.propertyDescription,
                    surface = it.propertyEntity.surface?.toString() + "m²",
                    room = it.propertyEntity.room?.toString(),
                    bathroom = it.propertyEntity.bathroom?.toString(),
                    bedroom = it.propertyEntity.bedroom?.toString(),
                    interest = it.propertyEntity.interest,
                    address = it.propertyEntity.address,
                    apartment = it.propertyEntity.apartmentNumber,
                    city = it.propertyEntity.city,
                    county = it.propertyEntity.county,
                    zipcode = it.propertyEntity.zipcode,
                    country = it.propertyEntity.country,
                    startSale = it.propertyEntity.createDateToFormat,
                    vendor = it.propertyEntity.vendor,
                    visibility = true,
                    staticMap = it.propertyEntity.staticMap
                )
            }.asLiveData(applicationDispatchers.ioDispatcher)
        }
}