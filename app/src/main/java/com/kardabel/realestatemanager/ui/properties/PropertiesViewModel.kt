package com.kardabel.realestatemanager.ui.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import com.kardabel.realestatemanager.repository.CurrentPropertyIdRepository
import com.kardabel.realestatemanager.repository.CurrentSearchRepository
import com.kardabel.realestatemanager.repository.PriceConverterRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import com.kardabel.realestatemanager.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PropertiesViewModel @Inject constructor(
    private val propertiesRepository: PropertiesRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val priceConverterRepository: PriceConverterRepository,
    private val currentSearchRepository: CurrentSearchRepository,
    private val applicationDispatchers: ApplicationDispatchers
) : ViewModel() {

    private val propertiesLiveData: LiveData<List<PropertyWithPhoto>> =
        propertiesRepository
            .getProperties()
            .asLiveData(applicationDispatchers.ioDispatcher)

    private val currentCurrencyLiveData: LiveData<Boolean> =
        priceConverterRepository
            .getCurrentCurrencyLiveData

    private val propertiesMediatorLiveData = MediatorLiveData<List<PropertyViewState>>().apply {
        addSource(propertiesLiveData) { propertyWithPhoto ->
            combine(propertyWithPhoto, currentCurrencyLiveData.value)
        }
        addSource(currentCurrencyLiveData) { currencyStatus ->
            combine(propertiesLiveData.value, currencyStatus)
        }
    }

    val viewStateLiveData: LiveData<List<PropertyViewState>> = propertiesMediatorLiveData


    private fun combine(propertyWithPhoto: List<PropertyWithPhoto>?, currencyStatus: Boolean?) {
        propertyWithPhoto ?: return

        if (currencyStatus != null) {
            propertiesMediatorLiveData.postValue(propertyWithPhoto.map { propertyWithPhoto ->
                toViewStateWithCurrencyStatus(propertyWithPhoto, currencyStatus)
            })

        }

        propertiesMediatorLiveData.value = propertyWithPhoto.map {
            toViewState(it)
        }
    }

    private fun toViewState(property: PropertyWithPhoto) = PropertyViewState(
        propertyId = property.propertyEntity.propertyId,
        type = readableType(property.propertyEntity.type),
        county = property.propertyEntity.county,
        price = readablePrice(property.propertyEntity.price.toString()),
        saleStatus = saleStatusToString(property.propertyEntity.saleStatus),
        vendor = property.propertyEntity.vendor,
        photoBitmap = property.photo[0].photo
    )

    private fun toViewStateWithCurrencyStatus(
        property: PropertyWithPhoto,
        currencyStatus: Boolean
    ) =
        PropertyViewState(
            propertyId = property.propertyEntity.propertyId,
            type = readableType(property.propertyEntity.type),
            county = property.propertyEntity.county,
            price = currencyConverter(property.propertyEntity.price, currencyStatus),
            saleStatus = saleStatusToString(property.propertyEntity.saleStatus),
            vendor = property.propertyEntity.vendor,
            photoBitmap = property.photo[0].photo
        )

    private fun currencyConverter(
        price: Int?,
        currencyStatus: Boolean
    ): String {
        return if (price != null) {
            return if (currencyStatus) {
                val priceConverted: String = Utils.convertEuroToDollar(price).toString()
                "$$priceConverted"
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
        return if (price != "null") {
            "$$price"
        } else {
            "Price N/C"
        }
    }

    private fun saleStatusToString(saleStatus: Boolean): String {
        return when (saleStatus) {
            true -> "On sale"
            false -> "Sold !"

        }
    }

    fun onPropertyClicked(propertyId: Long) {
        currentPropertyIdRepository.setCurrentPropertyId(propertyId)
    }
}