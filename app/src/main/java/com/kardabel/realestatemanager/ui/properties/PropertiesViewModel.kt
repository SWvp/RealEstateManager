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
    applicationDispatchers: ApplicationDispatchers
): ViewModel() {

    private val getProperties: LiveData<List<PropertyEntity>> =
        propertiesRepository
            .getProperties()
            .asLiveData(applicationDispatchers.ioDispatcher)

    private val _properties = MediatorLiveData<List<PropertyViewState>>().apply {
        addSource(getProperties) {
            combine(it)
        }
    }

    val getPropertiesLiveData: LiveData<List<PropertyViewState>> = _properties

    private fun combine(it: List<PropertyEntity>?) {
        _properties.value = it?.let { it1 -> map(it1) }

    }

    private fun map(propertiesList: List<PropertyEntity>): List<PropertyViewState> {
        val propertyViewState: MutableList<PropertyViewState> = mutableListOf()
            for (it in propertiesList){
                propertyViewState.add(
                    PropertyViewState(
                        it.id,
                        it.type,
                        it.place,
                        it.price.toString()
                    )
                )
            }
        return propertyViewState

    }
}