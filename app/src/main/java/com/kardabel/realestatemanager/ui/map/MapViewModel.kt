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
import com.kardabel.realestatemanager.model.SearchParams
import com.kardabel.realestatemanager.repository.CurrentPropertyIdRepository
import com.kardabel.realestatemanager.repository.CurrentSearchRepository
import com.kardabel.realestatemanager.repository.LocationRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import com.kardabel.realestatemanager.ui.properties.PropertyViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val context: Application,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    currentSearchRepository: CurrentSearchRepository,
    locationRepository: LocationRepository,
    propertiesRepository: PropertiesRepository,
    applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {

    val getMapInfo: LiveData<MapViewState> =
        combine(
            locationRepository.getUserLocation(),
            propertiesRepository.getPropertiesWithPhotosFlow(),
            currentSearchRepository.getSearchParamsParamsFlow()
        ) { location, properties, searchParams ->

            if(searchParams != null){
                MapViewState(
                    getFilteredProperties(properties, searchParams).map { property ->
                        getPoiLocation(property)
                    },
                    location
                )

            }else{
            MapViewState(
                properties.map { property ->
                    getPoiLocation(property)
                },
                location
            )}
        }.asLiveData(applicationDispatchers.ioDispatcher)

    private fun getFilteredProperties(
        properties: List<PropertyWithPhoto>,
        searchParams: SearchParams
    ): List<PropertyWithPhoto> {

        val filteredList = mutableListOf<PropertyWithPhoto>()

        for (property in properties) {
            if (applySearchParams(property, searchParams)) {
                filteredList.add(property)

            }
        }
        return filteredList

    }

    private fun applySearchParams(
        property: PropertyWithPhoto,
        searchParams: SearchParams
    ): Boolean {

        return (surfaceMatchParams(searchParams.surfaceRange, property.propertyEntity.surface)
                && priceMatchParams(searchParams.priceRange, property.propertyEntity.price)
                && roomMatchParams(searchParams.roomRange, property.propertyEntity.room)
                && searchParams.photo?.let { searchParams.photo == property.photo.size } != false
                && searchParams.propertyType?.let { searchParams.propertyType == property.propertyEntity.type } != false
                && matchInterest(searchParams.interest, property.propertyEntity.interest)
                && searchParams.county?.let { searchParams.county == property.propertyEntity.county } != false)

    }

    private fun surfaceMatchParams(surfaceRange: IntRange?, surfaceProperty: String): Boolean {

        var surfaceCanBeNull: String? = surfaceProperty

        if (surfaceCanBeNull == "") {
            surfaceCanBeNull = null
        }

        return surfaceRange?.let { surfaceCanBeNull?.let { surfaceRange.contains(it.toInt()) } }
            ?: true

    }

    private fun priceMatchParams(priceRange: IntRange?, priceProperty: String): Boolean {

        var priceCanBeNull: String? = priceProperty

        if (priceCanBeNull == "") {
            priceCanBeNull = null
        }

        return priceRange?.let { priceCanBeNull?.let { priceRange.contains(it.toInt()) } } ?: true

    }

    private fun roomMatchParams(roomRange: IntRange?, roomProperty: String): Boolean {

        var roomCanBeNull: String? = roomProperty

        if (roomCanBeNull == "") {
            roomCanBeNull = null
        }

        return roomRange?.let { roomCanBeNull?.let { roomRange.contains(it.toInt()) } } ?: true

    }

    private fun matchInterest(
        searchInterests: List<String>?,
        propertyInterests: List<String>?,
    ): Boolean {
        return if (propertyInterests != null && searchInterests != null) {
            propertyInterests.any { it in searchInterests }
        } else searchInterests == null
    }


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