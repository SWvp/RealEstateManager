package com.kardabel.realestatemanager.ui.map

import androidx.lifecycle.*
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.model.UserLocation
import com.kardabel.realestatemanager.repository.LocationRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine

import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val propertiesRepository: PropertiesRepository,
    private val applicationDispatchers: ApplicationDispatchers
) : ViewModel() {

    val getMapInfo: LiveData<MapViewState> =
        combine(locationRepository.userLocation(), propertiesRepository.getProperties()){ location, properties ->
            location.map { userLocation ->
                toViewState(userLocation)

            }
        }.asLiveData(applicationDispatchers.ioDispatcher)


    private fun toViewState(userLocation: UserLocation) = MapViewState(


    )

}