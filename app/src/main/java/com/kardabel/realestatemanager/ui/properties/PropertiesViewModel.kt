package com.kardabel.realestatemanager.ui.properties

import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import com.kardabel.realestatemanager.model.SearchParams
import com.kardabel.realestatemanager.repository.CurrentPropertyIdRepository
import com.kardabel.realestatemanager.repository.CurrentSearchRepository
import com.kardabel.realestatemanager.repository.PriceConverterRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import com.kardabel.realestatemanager.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PropertiesViewModel @Inject constructor(
    propertiesRepository: PropertiesRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    priceConverterRepository: PriceConverterRepository,
    currentSearchRepository: CurrentSearchRepository,
    applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {

    private val propertiesLiveData: LiveData<List<PropertyWithPhoto>> =
        propertiesRepository
            .getProperties()
            .asLiveData(applicationDispatchers.ioDispatcher)

    private val searchParamsLiveData: LiveData<SearchParams?> =
        currentSearchRepository
            .searchParamsParamsFlow()
            .asLiveData(applicationDispatchers.ioDispatcher)

    private val currentCurrencyLiveData: LiveData<Boolean> =
        priceConverterRepository
            .getCurrentCurrencyLiveData

    // Due to the fact we have both liveData and flow, i must combine with a mediator
    private val propertiesMediatorLiveData = MediatorLiveData<List<PropertyViewState>>().apply {
        addSource(propertiesLiveData) { propertyWithPhoto ->
            combine(propertyWithPhoto, currentCurrencyLiveData.value, searchParamsLiveData.value)
        }
        addSource(currentCurrencyLiveData) { currencyStatus ->
            combine(propertiesLiveData.value, currencyStatus, searchParamsLiveData.value)
        }
        addSource(searchParamsLiveData) { searchParams ->
            combine(propertiesLiveData.value, currentCurrencyLiveData.value, searchParams)

        }
    }

    // Exposed LiveData for the beautiful view
    val viewStateLiveData: LiveData<List<PropertyViewState>> = propertiesMediatorLiveData


    private fun combine(
        propertyWithPhoto: List<PropertyWithPhoto>?,
        currencyStatus: Boolean?,
        searchParams: SearchParams?,
    ) {
        propertyWithPhoto ?: return

        if (searchParams == null && currencyStatus == null) {
            propertiesMediatorLiveData.value = propertyWithPhoto.map {
                toViewState(it)
            }

        } else if (searchParams != null) {
            val filteredList = mutableListOf<PropertyViewState>()
            for (property in propertyWithPhoto) {
                if (applySearchParams(property, searchParams)) {
                    filteredList.add(toViewState(property))

                }
            }

            propertiesMediatorLiveData.value = filteredList

        } else if (currencyStatus != null) {
            propertiesMediatorLiveData.postValue(propertyWithPhoto.map { properties ->
                toViewStateWithCurrencyStatus(properties, currencyStatus)
            })
        }
    }

    private fun toViewState(property: PropertyWithPhoto) = PropertyViewState(
        propertyId = property.propertyEntity.propertyId,
        type = readableType(property.propertyEntity.type),
        county = property.propertyEntity.county,
        price = readablePrice(property.propertyEntity.price.toString()),
        saleStatus = property.propertyEntity.saleStatus,
        saleColor = colorToApply(property.propertyEntity.saleStatus),
        vendor = property.propertyEntity.vendor,
        photoUri = Uri.parse(property.photo[0].photoUri)
    )

    private fun toViewStateWithCurrencyStatus(
        property: PropertyWithPhoto,
        currencyStatus: Boolean
    ) =
        PropertyViewState(
            propertyId = property.propertyEntity.propertyId,
            type = readableType(property.propertyEntity.type),
            county = property.propertyEntity.county,
            price = currencyConverter(property.propertyEntity.price?.toInt(), currencyStatus),
            saleStatus = property.propertyEntity.saleStatus,
            saleColor = colorToApply(property.propertyEntity.saleStatus),
            vendor = property.propertyEntity.vendor,
            photoUri = Uri.parse(property.photo[0].photoUri)
        )

    private fun applySearchParams(
        property: PropertyWithPhoto,
        searchParams: SearchParams
    ): Boolean {

        return (searchParams.priceRange?.let {
            property.propertyEntity.price?.let { it1 ->
                searchParams.priceRange.contains(it1.toInt())
            }
        } ?: true
                && searchParams.surfaceRange?.let { property.propertyEntity.surface?.let { it1 ->
            searchParams.surfaceRange.contains(
                it1.toInt())
        } } ?: true
                && searchParams.roomRange?.let { property.propertyEntity.room?.let { it1 ->
            searchParams.roomRange.contains(
                it1.toInt())
        } } ?: true
                && searchParams.photo?.let { searchParams.photo == property.photo.size } ?: true
                && searchParams.county?.let { searchParams.county == property.propertyEntity.county } ?: true
                && matchInterest(searchParams.interest, property.propertyEntity.interest)
                && searchParams.propertyType?.let { searchParams.propertyType == property.propertyEntity.type } ?: true
                )

    }

    private fun currencyConverter(
        price: Int?,
        currencyStatus: Boolean
    ): String {
        return if (price != null) {
            return if (currencyStatus) {
                "$$price"
            } else {
                val priceConverted: String = Utils.convertDollarToEuro(price).toString()
                "€$priceConverted"
            }
        } else {
            "Price N/C"
        }
    }

    private fun readableType(type: String): String {
        return if (type != "null") {
            type
        } else {
            ""
        }
    }

    private fun readablePrice(price: String?): String {
        return if (price != "") {
            "$$price"
        } else {
            "Price N/C"
        }
    }

    private fun colorToApply(saleStatus: String): Int {
        return when (saleStatus) {
            "On Sale !" -> Color.WHITE
            else -> {
                Color.RED
            }
        }
    }

    private fun matchInterest(
        searchInterests: List<String>?,
        propertyInterests: List<String>?,
    ): Boolean {
        return if (propertyInterests != null && searchInterests != null) {
            propertyInterests.any { it in searchInterests }
        } else searchInterests == null
    }

    fun onPropertyClicked(propertyId: Long) {
        currentPropertyIdRepository.setCurrentPropertyId(propertyId)
    }
}