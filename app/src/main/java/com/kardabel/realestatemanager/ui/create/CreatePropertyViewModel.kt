package com.kardabel.realestatemanager.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.repository.PropertiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePropertyViewModel @Inject constructor(
    private val propertiesRepository: PropertiesRepository,
    private val applicationDispatchers: ApplicationDispatchers
): ViewModel() {


    fun createProperty(
        address: String,
        description: String,
        type: String,
        place: String,
        price: String,
    ) {

        val property = price.toFloatOrNull()?.let {
            PropertyEntity(
                address = address,
                description = description,
                type = type,
                place = place,
                price = it
            )
        }
    }

    private fun insertProperty(propertyEntity: PropertyEntity) =
        viewModelScope.launch(applicationDispatchers.ioDispatcher) {
            propertiesRepository.insertProperty(propertyEntity)
        }


}