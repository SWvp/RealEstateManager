package com.kardabel.realestatemanager.ui.map

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.maps.model.LatLng
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.model.Poi
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import com.kardabel.realestatemanager.repository.CurrentPropertyIdRepository
import com.kardabel.realestatemanager.repository.LocationRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val context: Application,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    locationRepository: LocationRepository,
    propertiesRepository: PropertiesRepository,
    applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {

    val getMapInfo: LiveData<MapViewState> =
        combine(
            locationRepository.userLocation(),
            propertiesRepository.getPropertiesWithPhotosFlow()
        ) { location, properties ->
            MapViewState(
                properties.map { property ->
                    getPoiLocation(property)
                },
                location
            )
        }.asLiveData(applicationDispatchers.ioDispatcher)

    private fun getPoiLocation(property: PropertyWithPhoto): Poi {

        val address = property.propertyEntity.address + ", " + property.propertyEntity.city
        val geocode = Geocoder(context, Locale.getDefault())
        val list = geocode.getFromLocationName(address, 1)

        var propertyLatLng: LatLng? = null

        if (list.isNotEmpty()) {
            propertyLatLng = LatLng(
                list[0].latitude,
                list[0].longitude
            )
        }

        return Poi(
            property.propertyEntity.propertyId,
            propertyLatLng
        )
    }

    fun onPropertyClicked(propertyId: Long) {
        currentPropertyIdRepository.setCurrentPropertyId(propertyId)
    }
}