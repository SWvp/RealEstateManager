package com.kardabel.realestatemanager.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.repository.CurrentPropertyIdRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DetailsFragmentViewModel @Inject constructor(
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val propertiesRepository: PropertiesRepository,
    private val applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {

    val detailsLiveData: LiveData<DetailsViewState> =
        currentPropertyIdRepository.currentPropertyIdLiveData.switchMap { id ->
            propertiesRepository.getPropertyById(id).map {
                DetailsViewState(
                    propertyId = it.propertyEntity.propertyId,
                    photos = it.photo.map { photoEntity ->
                        DetailsPhotoViewState(
                            photoEntity.photoUri,
                            photoEntity.photoDescription,
                        )
                    },
                    description = it.propertyEntity.propertyDescription,
                    surface = readableSurface(it.propertyEntity.surface),
                    room = it.propertyEntity.room,
                    bathroom = it.propertyEntity.bathroom,
                    bedroom = it.propertyEntity.bedroom,
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

    val isFromSearchLiveData = currentPropertyIdRepository.isFromSearchLiveData

    private fun readableSurface(value: String?): String {
        return if (value != "") {
            value + "m²"
        } else {
            value
        }
    }
}