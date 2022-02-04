package com.kardabel.realestatemanager.ui.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import com.kardabel.realestatemanager.repository.PropertiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PropertiesViewModel @Inject constructor(
    private val propertiesRepository: PropertiesRepository,
    private val applicationDispatchers: ApplicationDispatchers
) : ViewModel() {

    private val propertiesLiveData: LiveData<List<PropertyWithPhoto>> =
        propertiesRepository
            .getProperties()
            .asLiveData(applicationDispatchers.ioDispatcher)

    private val propertiesMediatorLiveData = MediatorLiveData<List<PropertyViewState>>().apply {
        addSource(propertiesLiveData) {
            combine(it)
        }
    }

    val viewStateLiveData: LiveData<List<PropertyViewState>> = propertiesMediatorLiveData

    private fun combine(propertyEntities: List<PropertyWithPhoto>) {
        propertyEntities ?: return

        propertiesMediatorLiveData.value = propertyEntities.map {
            toViewState(it)
        }
    }

    private fun toViewState(property: PropertyWithPhoto) = PropertyViewState(
        propertyId = property.propertyEntity.propertyId,
        type = property.propertyEntity.type,
        county = property.propertyEntity.address,
        price = property.propertyEntity.price.toString(),
        //photoBitmap = property.photo[0].photo
    )

    fun onPropertyClicked(propertyViewState: PropertyViewState) {
        TODO("Not yet implemented")
    }
}