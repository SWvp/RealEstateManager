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

    val detailsLiveData: LiveData<DetailsViewState> =
        currentPropertyIdRepository.currentPropertyIdLiveData.switchMap { id ->
            propertiesRepository.getPropertyById(id).map {
                DetailsViewState(
                    propertyId = it.propertyEntity.propertyId,
                    //photos = it.photo.,
                    description = nullBecomeReadable(it.propertyEntity.propertyDescription),
                    surface = nullBecomeReadable(it.propertyEntity.surface.toString()),
                    room = nullBecomeReadable(it.propertyEntity.room.toString()),
                    bathroom = nullBecomeReadable(it.propertyEntity.bathroom.toString()),
                    bedroom = nullBecomeReadable(it.propertyEntity.bedroom.toString()),
                    poi = it.propertyEntity.interest,
                    address = nullBecomeReadable(it.propertyEntity.address),
                    apartment = nullBecomeReadable(it.propertyEntity.apartmentNumber),
                    city = nullBecomeReadable(it.propertyEntity.city),
                    county = nullBecomeReadable(it.propertyEntity.county),
                    zipcode = nullBecomeReadable(it.propertyEntity.zipcode),
                    country = nullBecomeReadable(it.propertyEntity.country),
                )
            }.asLiveData(applicationDispatchers.ioDispatcher)
        }


    private fun nullBecomeReadable(value: String?): String {
        var item: String? = null
        if(value == null){
            item = ""
        }
        else{
            item = value
        }
        return item
    }

}