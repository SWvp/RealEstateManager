package com.kardabel.realestatemanager.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.BuildConfig
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.repository.PhotoRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import com.kardabel.realestatemanager.utils.SingleLiveEvent
import com.kardabel.realestatemanager.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CreatePropertyViewModel @Inject constructor(
    private val propertiesRepository: PropertiesRepository,
    private val applicationDispatchers: ApplicationDispatchers,
    private val firebaseAuth: FirebaseAuth,
    private val photoRepository: PhotoRepository,
    private val clock: Clock,
    // @ApplicationContext context: Context,

) : ViewModel() {

    private val actionSingleLiveEvent: SingleLiveEvent<CreateActivityViewAction> = SingleLiveEvent()

    private val interests = mutableListOf<String>()
    private var photoMutableList = mutableListOf<Photo>()


    val getPhoto: LiveData<List<CreatePropertyPhotoViewState>> =
        photoRepository.getPhotoLiveData().map {
            it.map { photoEntity ->
                photoMutableList = it as MutableList<Photo>
                CreatePropertyPhotoViewState(
                    photoEntity.photo,
                    photoEntity.photoDescription,
                )
            }
        }

    // Poi are stored here
    fun addInterest(interest: String) {
        interests.add(interest)

    }

    // When property is ready
    fun createProperty(
        address: String?,
        apartmentNumber: String?,
        county: String?,
        city: String?,
        zipcode: String?,
        country: String?,
        propertyDescription: String?,
        type: String?,
        price: String?,
        surface: String?,
        room: String?,
        bedroom: String?,
        bathroom: String?,
    ) {

        // Must contain at least one photo and an address (street, zip, city)
        if (photoMutableList.isNotEmpty() && address != null && city != null && zipcode != null) {

            // Get value to entity format, string is for the view, we don't trust anything else
            val priceToInt = price?.toIntOrNull()
            val surfaceToInt = surface?.toIntOrNull()
            val roomToInt = room?.toIntOrNull()
            val bedroomToInt = bedroom?.toIntOrNull()
            val bathroomToInt = bathroom?.toIntOrNull()
            val uid = firebaseAuth.currentUser!!.uid
            val vendor = firebaseAuth.currentUser!!.displayName.toString()
            val createDateToFormat = Utils.getTodayDate()
            val localDateTime = LocalDateTime.now(clock).toString()

            val property = PropertyEntity(
                address = address,
                apartmentNumber = apartmentNumber,
                city = city,
                zipcode = zipcode,
                county = county,
                country = country,
                propertyDescription = propertyDescription,
                type = type,
                price = priceToInt,
                surface = surfaceToInt,
                room = roomToInt,
                bedroom = bedroomToInt,
                bathroom = bathroomToInt,
                uid = uid,
                vendor = vendor,
                createLocalDateTime = localDateTime,
                createDateToFormat = createDateToFormat,
                saleStatus = true,
                purchaseDate = null,
                interest = interests,
                staticMap = staticMapUrl(address, zipcode, city)
            )


            // Get the property id to update photoEntity
            viewModelScope.launch(applicationDispatchers.ioDispatcher) {
                val newPropertyId = insertProperty(property)
                createPhotoEntityWithPropertyId(newPropertyId)
            }
        } else {
            actionSingleLiveEvent.setValue(CreateActivityViewAction.PHOTO_ERROR)
        }
    }

    private fun staticMapUrl(address: String, zipcode: String, city: String): String {
        var staticMap: String? = null
        val key: String = BuildConfig.GOOGLE_PLACES_KEY


        return staticMap!!

    }

    private suspend fun createPhotoEntityWithPropertyId(newPropertyId: Long) {
        val photoListWithPropertyId = mutableListOf<PhotoEntity>()
        for (photo in photoMutableList) {
            val photoEntity = PhotoEntity(
                photo.photo,
                photo.photoDescription,
                newPropertyId,
            )
            photoListWithPropertyId.add(photoEntity)
        }
        sendPhotosToDataBase(photoListWithPropertyId)

        withContext(applicationDispatchers.mainDispatcher) {
            actionSingleLiveEvent.setValue(CreateActivityViewAction.FINISH_ACTIVITY)
        }
    }

    private suspend fun sendPhotosToDataBase(photoEntities: MutableList<PhotoEntity>) {
        for (photoEntity in photoEntities) {
            insertPhoto(photoEntity)
        }
    }

    private suspend fun insertProperty(property: PropertyEntity): Long {
        return propertiesRepository.insertProperty(property)
    }

    private suspend fun insertPhoto(photo: PhotoEntity) = propertiesRepository.insertPhoto(photo)
}