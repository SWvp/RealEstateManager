package com.kardabel.realestatemanager.ui.create

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kardabel.realestatemanager.ApplicationDispatchers
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

    // TODO Faire un Repo partagé entre ce VM et le VM de la DialogFragment
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

    // When user create a photo with a description, create this as a photo, not entity
    fun addPhoto(photo: Bitmap, photoDescription: String) {
        photoRepository.addPhoto(
            Photo(
                photo,
                photoDescription,

                )
        )
    }

    // When property is ready
    fun createProperty(
        address: String?,
        apartmentNumber: String?,
        city: String?,
        county: String?,
        postalCode: String?,
        country: String?,
        propertyDescription: String?,
        type: String?,
        price: String?,
        surface: String?,
        room: String?,
        bedroom: String?,
        bathroom: String?,
    ) {

        // Must contain at least one photo
        if (photoMutableList.isNotEmpty()) {

            // Get value to entity format, string is for the view, we don't trust anything else
            val priceToFloat = price?.toFloatOrNull()
            val surfaceToDouble = surface?.toDoubleOrNull()
            val roomToInt = room?.toIntOrNull()
            val bedroomToInt = bedroom?.toIntOrNull()
            val bathroomToInt = bathroom?.toIntOrNull()
            val uid = firebaseAuth.currentUser!!.uid
            val createDateToFormat = Utils.getTodayDate()
            val localDateTime = LocalDateTime.now(clock).toString()

            val property = PropertyEntity(
                address = address,
                apartmentNumber = apartmentNumber,
                city = city,
                zipcode = postalCode,
                county = county,
                country = country,
                propertyDescription = propertyDescription,
                type = type,
                price = priceToFloat,
                surface = surfaceToDouble,
                room = roomToInt,
                bedroom = bedroomToInt,
                bathroom = bathroomToInt,
                uid = uid,
                createLocalDateTime = localDateTime,
                createDateToFormat = createDateToFormat,
                saleStatus = true,
                purchaseDate = null,
                interest = interests,
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