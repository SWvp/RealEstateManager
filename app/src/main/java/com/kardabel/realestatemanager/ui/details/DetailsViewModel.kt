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
class DetailsViewModel @Inject constructor(
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val propertiesRepository: PropertiesRepository,
    private val applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {

    //private var isTablet: Boolean = false

   // val navigationSingleLiveEvent : SingleLiveEvent<NavigateViewAction> = SingleLiveEvent()


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

 // init {
 //     navigationSingleLiveEvent.addSource(currentPropertyIdRepository.currentPropertyIdLiveData) {
 //         if (!isTablet) {
 //             navigationSingleLiveEvent.setValue(NavigateViewAction.IsLandscapeMode)
 //         }
 //     }
 // }

 // fun onConfigurationChanged(isTablet: Boolean) {
 //     this.isTablet = isTablet
 // }
}