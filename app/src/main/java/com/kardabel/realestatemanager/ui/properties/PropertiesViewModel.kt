package com.kardabel.realestatemanager.ui.properties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.data.CurrentSearchRepo
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import com.kardabel.realestatemanager.repository.PropertiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PropertiesViewModel @Inject constructor(
    private val propertiesRepository: PropertiesRepository,
    private val currentSearchRepo: CurrentSearchRepo,
    private val applicationDispatchers: ApplicationDispatchers
) : ViewModel() {

    val viewStateLiveData: LiveData<List<PropertyViewState>> =
        combine(propertiesRepository.getProperties(), currentSearchRepo.mySearchParamsFlow) { properties, searchParams ->
            properties.map { propertyWithPhoto ->
                toViewState(propertyWithPhoto)
            }
        }.asLiveData(applicationDispatchers.ioDispatcher)

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