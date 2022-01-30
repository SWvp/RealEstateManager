package com.kardabel.realestatemanager.ui.map

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val applicationDispatchers: ApplicationDispatchers
) : ViewModel() {

    val getMapInfo: LiveData<MapViewState> = locationRepository.fetchUpdates().asLiveData(applicationDispatchers.ioDispatcher)

}