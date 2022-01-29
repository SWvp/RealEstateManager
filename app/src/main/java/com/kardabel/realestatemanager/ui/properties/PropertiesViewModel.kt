package com.kardabel.realestatemanager.ui.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.repository.PropertiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PropertiesViewModel @Inject constructor(
    propertiesRepository: PropertiesRepository,
    applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {

    private val propertiesLiveData: LiveData<List<PropertyEntity>> =
        propertiesRepository
            .getProperties()
            .asLiveData(applicationDispatchers.ioDispatcher)

    private val propertiesMediatorLiveData = MediatorLiveData<List<PropertyViewState>>().apply {
        addSource(propertiesLiveData) {
            combine(it)
        }
    }

    val viewStateLiveData: LiveData<List<PropertyViewState>> = propertiesMediatorLiveData

    private fun combine(propertyEntities: List<PropertyEntity>?) {
        propertyEntities ?: return

        propertiesMediatorLiveData.value = propertyEntities.map {
            toViewState(it)
        }
    }

    private fun toViewState(propertyEntity: PropertyEntity) = PropertyViewState(
        id = propertyEntity.id,
        type = propertyEntity.type,
        place = propertyEntity.place,
        price = propertyEntity.price.toString()
    )

    fun onPropertyClicked(propertyViewState: PropertyViewState) {
        TODO("Not yet implemented")
    }
}