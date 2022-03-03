package com.kardabel.realestatemanager.ui.create

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.BuildConfig
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.firestore.SendPhotoToStorage
import com.kardabel.realestatemanager.firestore.SendPropertyToFirestore
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.repository.CreatePhotoRepository
import com.kardabel.realestatemanager.repository.InterestRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import com.kardabel.realestatemanager.utils.ActivityViewAction
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
    private val createPhotoRepository: CreatePhotoRepository,
    private val interestRepository: InterestRepository,
    private val sendPropertyToFirestore: SendPropertyToFirestore,
    private val sendPhotoToStorage: SendPhotoToStorage,
    private val clock: Clock,
    private val context: Application,

    ) : ViewModel() {

    val actionSingleLiveEvent = SingleLiveEvent<ActivityViewAction>()

    private var photoMutableList = mutableListOf<Photo>()

    val getPhoto: LiveData<List<CreatePropertyPhotoViewState>> =
        createPhotoRepository.getAddedPhotoLiveData().map { photoList ->
            photoList.map { photo ->
                photoMutableList = photoList as MutableList<Photo>
                CreatePropertyPhotoViewState(
                    //photoBitmap = photo.photoBitmap,
                    photoDescription = photo.photoDescription,
                    photoUri = photo.photoUri,
                )
            }
        }

    val getInterest: LiveData<List<String>> = interestRepository.getInterestLiveData()

    // Poi are stored here
    fun addInterest(interest: String) {
        if (interest.length > 2) {
            interestRepository.addInterest(interest)
        } else {
            actionSingleLiveEvent.setValue(ActivityViewAction.INTEREST_FIELD_ERROR)
        }
    }

    fun removeInterest(interest: String) {
        interestRepository.remove(interest)
    }

    // When property is ready
    fun createProperty(
        address: String,
        apartmentNumber: String,
        county: String,
        city: String,
        zipcode: String,
        country: String,
        propertyDescription: String,
        type: String,
        price: String,
        surface: String,
        room: String,
        bedroom: String,
        bathroom: String,
    ) {

        // Must contain at least one photo and an address (street, zip, city)
        if (photoMutableList.isNotEmpty() && address.isNotEmpty() && city.isNotEmpty() && zipcode.isNotEmpty()) {

            // Get value to entity format, string is for the view, we don't trust anything else
            val priceToInt = price.toIntOrNull()
            val surfaceToInt = surface.toIntOrNull()
            val roomToInt = room.toIntOrNull()
            val bedroomToInt = bedroom.toIntOrNull()
            val bathroomToInt = bathroom.toIntOrNull()
            val uid = firebaseAuth.currentUser!!.uid
            val vendor = firebaseAuth.currentUser!!.displayName.toString()
            val createDateToFormat = Utils.getTodayDate()
            val createlocalDateTime = LocalDateTime.now(clock).toString()

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
                createLocalDateTime = createlocalDateTime,
                createDateToFormat = createDateToFormat,
                saleStatus = true,
                purchaseDate = null,
                interest = interestCanBeNull(interestRepository.getInterest()),
                staticMap = staticMapUrl(address, zipcode, city)
            )


            // Get the property id to update photoEntity
            viewModelScope.launch(applicationDispatchers.ioDispatcher) {
                val newPropertyId = insertProperty(property)
                createPhotoEntityWithPropertyId(newPropertyId, createlocalDateTime)
                createPropertyOnFirestore(property)
            }
        } else {
            actionSingleLiveEvent.setValue(ActivityViewAction.FIELDS_ERROR)
        }
    }

    private fun interestCanBeNull(interests: MutableList<String>): List<String>? {
        return if (interests.size == 0) {
            null
        } else {
            interests
        }
    }

    // Create an url to retrieve a miniature of the map with property marker
    private fun staticMapUrl(address: String, zipcode: String, city: String): String {

        val key: String = BuildConfig.GOOGLE_PLACES_KEY

        val addressWithComas = address.replace(" ", context.getString(R.string.coma))
        val cityWithoutSpace = city.replace(" ", "")

        return context.getString(R.string.url_map_static) +
                addressWithComas +
                context.getString(R.string.coma) +
                zipcode +
                context.getString(R.string.coma) +
                cityWithoutSpace +
                context.getString(R.string.zoom_size) +
                context.getString(R.string.marker) +
                addressWithComas +
                context.getString(R.string.key) +
                key
    }

    private suspend fun createPhotoEntityWithPropertyId(
        newPropertyId: Long,
        createLocalDateTime: String
    ) {

        val photoListWithPropertyId = mutableListOf<PhotoEntity>()

        for (photo in photoMutableList) {
            val photoEntity = PhotoEntity(
                photo.photoUri.toString(),
                photo.photoDescription,
                newPropertyId,
            )
            photoListWithPropertyId.add(photoEntity)
        }

        sendPhotosToLocalDataBase(photoListWithPropertyId)
        sendPhotoToCloudStorage(photoListWithPropertyId, createLocalDateTime)

        withContext(applicationDispatchers.mainDispatcher) {
            actionSingleLiveEvent.postValue(ActivityViewAction.FINISH_ACTIVITY)

        }

        emptyPhotoRepository()
        emptyInterestRepository()
    }

    private fun createPropertyOnFirestore(property: PropertyEntity) {
        sendPropertyToFirestore.createPropertyDocument(property)

    }

    private fun sendPhotoToCloudStorage(
        photos: MutableList<PhotoEntity>,
        createLocalDateTime: String
    ) {
        sendPhotoToStorage.createPhotoDocument(photos, createLocalDateTime)


    }

    private suspend fun sendPhotosToLocalDataBase(photoEntities: MutableList<PhotoEntity>) {
        insertPhotoDao(photoEntities)

    }

    private suspend fun insertProperty(property: PropertyEntity): Long {
        return propertiesRepository.insertProperty(property)
    }

    private suspend fun insertPhotoDao(photos: List<PhotoEntity>) =
        propertiesRepository.insertPhotos(photos)

    // Clear the photoRepo for the next use
    fun emptyPhotoRepository() {
        createPhotoRepository.emptyCreatePhotoList()
    }

    // Clear the interestRepo for the next use
    private fun emptyInterestRepository() {
        interestRepository.emptyInterestList()
    }
}