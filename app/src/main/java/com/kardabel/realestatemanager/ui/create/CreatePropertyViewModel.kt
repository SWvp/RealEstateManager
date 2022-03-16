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
import com.kardabel.realestatemanager.firestore.SendPhotoToCloudStorage
import com.kardabel.realestatemanager.firestore.SendPropertyToFirestoreRepository
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
    private val sendPropertyToFirestoreRepository: SendPropertyToFirestoreRepository,
    private val sendPhotoToCloudStorage: SendPhotoToCloudStorage,
    private val clock: Clock,
    private val context: Application,

    ) : ViewModel() {

    val actionSingleLiveEvent = SingleLiveEvent<ActivityViewAction>()

    private var photoMutableList = mutableListOf<PhotoEntity>()

    val getPhoto: LiveData<List<CreatePropertyPhotoViewState>> =
        createPhotoRepository.getAddedPhotoLiveData().map { photoList ->
            photoList.map { photo ->
                photoMutableList = photoList as MutableList<PhotoEntity>
                CreatePropertyPhotoViewState(
                    photoDescription = photo.photoDescription,
                    photoUri = photo.photoUri,
                    photoTimestamp = photo.photoTimestamp
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
            val uid = firebaseAuth.currentUser!!.uid
            val vendor = firebaseAuth.currentUser!!.displayName.toString()
            val createDateToFormat = Utils.todayDate()
            val propertyCreationDate = LocalDateTime.now(clock).toString()

            val property = PropertyEntity(
                address = address,
                apartmentNumber = apartmentNumber,
                city = city,
                zipcode = zipcode,
                county = county,
                country = country,
                propertyDescription = propertyDescription,
                type = type,
                price = price,
                surface = surface,
                room = room,
                bedroom = bedroom,
                bathroom = bathroom,
                uid = uid,
                vendor = vendor,
                propertyCreationDate = propertyCreationDate,
                creationDateToFormat = createDateToFormat,
                saleStatus = "On Sale !",
                purchaseDate = null,
                interest = interestCanBeNull(interestRepository.getInterest()),
                staticMap = staticMapUrl(address, zipcode, city),
                updateTimestamp = System.currentTimeMillis().toString(),
            )

            // Get the property id to update photoEntity
            viewModelScope.launch(applicationDispatchers.ioDispatcher) {
                val newPropertyId = insertProperty(property)
                createPhotoEntityWithPropertyId(newPropertyId, propertyCreationDate, uid)
                createPropertyOnFirestore(property)

                emptyPhotoRepository()
                emptyInterestRepository()
            }
        } else {
            actionSingleLiveEvent.setValue(ActivityViewAction.FIELDS_ERROR)
        }
    }

    private fun interestCanBeNull(interests: List<String>): List<String>? {
        return interests.ifEmpty {
            null
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
        createLocalDateTime: String,
        uid: String
    ) {

        val photoListWithPropertyId = mutableListOf<PhotoEntity>()

        for (photo in photoMutableList) {
            val photoEntity = PhotoEntity(
                photoUri = photo.photoUri,
                photoDescription = photo.photoDescription,
                propertyOwnerId = newPropertyId,
                photoTimestamp = System.nanoTime().toString(),
                photoCreationDate = createLocalDateTime,
            )
            photoListWithPropertyId.add(photoEntity)
        }

        sendPhotosToLocalDataBase(photoListWithPropertyId)
        sendPhotoToCloudStorage(photoListWithPropertyId, uid)

        withContext(applicationDispatchers.mainDispatcher) {
            actionSingleLiveEvent.postValue(ActivityViewAction.FINISH_ACTIVITY)

        }
    }

    private suspend fun createPropertyOnFirestore(property: PropertyEntity) {
        sendPropertyToFirestoreRepository.createPropertyDocument(property)

    }

    private suspend fun sendPhotoToCloudStorage(
        photos: List<PhotoEntity>,
        uid: String
    ) {
        sendPhotoToCloudStorage.createPhotoDocument(photos, uid)

    }

    private suspend fun insertProperty(property: PropertyEntity): Long {
        return propertiesRepository.insertProperty(property)
    }

    private suspend fun sendPhotosToLocalDataBase(photos: List<PhotoEntity>) =
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