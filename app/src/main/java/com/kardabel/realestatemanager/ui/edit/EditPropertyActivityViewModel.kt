package com.kardabel.realestatemanager.ui.edit

import android.app.Application
import androidx.lifecycle.*
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.BuildConfig
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.repository.SendPhotoToCloudStorageRepository
import com.kardabel.realestatemanager.repository.SendPropertyToFirestoreRepository
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyUpdate
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import com.kardabel.realestatemanager.repository.*
import com.kardabel.realestatemanager.utils.ActivityViewAction
import com.kardabel.realestatemanager.utils.SingleLiveEvent
import com.kardabel.realestatemanager.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class EditPropertyActivityViewModel @Inject constructor(
    private val propertiesRepository: PropertiesRepository,
    private val applicationDispatchers: ApplicationDispatchers,
    private val createPhotoRepository: CreatePhotoRepository,
    private val registeredPhotoRepository: RegisteredPhotoRepository,
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val sendPropertyToFirestoreRepository: SendPropertyToFirestoreRepository,
    private val sendPhotoToCloudStorageRepository: SendPhotoToCloudStorageRepository,
    private val interestRepository: InterestRepository,
    private val context: Application,
) : ViewModel() {

    val actionSingleLiveEvent = SingleLiveEvent<ActivityViewAction>()

    private var addedPhotoMutableList = mutableListOf<PhotoEntity>()
    private var updatedRegisteredPhotoMutableList = mutableListOf<PhotoEntity>()
    private var registeredPhotoList = mutableListOf<PhotoEntity>()
    private var photoFullList = mutableListOf<PhotoEntity>()


    private val interestList = mutableListOf<String>()

    private var uid by Delegates.notNull<String>()
    private var vendor by Delegates.notNull<String>()
    private var propertyId by Delegates.notNull<Long>()
    private var propertyCreationDate by Delegates.notNull<String>()
    private var dateToFormat by Delegates.notNull<String>()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////  RETRIEVE AND DISPLAY PHOTOS  //////////////////////////////////

    private val getRegisteredPhoto: LiveData<List<PhotoEntity>> =
        registeredPhotoRepository.getRegisteredPhotoLiveData()
    private val getAddedPhoto: LiveData<List<PhotoEntity>> =
        createPhotoRepository.getAddedPhotoLiveData()

    private val getAllPhotoMediatorLiveData =
        MediatorLiveData<List<EditPropertyPhotoViewState>>().apply {

            addSource(getRegisteredPhoto) { oldPhoto ->
                combine(oldPhoto, getAddedPhoto.value)
            }

            addSource(getAddedPhoto) { addedPhoto ->
                combine(getRegisteredPhoto.value, addedPhoto)
            }
        }

    private fun combine(registeredPhoto: List<PhotoEntity>?, addedPhoto: List<PhotoEntity>?) {
        registeredPhoto ?: return

        if (addedPhoto == null) {
            getAllPhotoMediatorLiveData.value = registeredPhoto.map { photo ->
                EditPropertyPhotoViewState(
                    photoDescription = photo.photoDescription,
                    photoUri = photo.photoUri,
                    photoId = photo.photoId,
                    propertyOwnerId = photo.propertyOwnerId,
                    photoTimestamp = photo.photoTimestamp,
                    photoCreationDate = photo.photoCreationDate
                )
            }
            updatedRegisteredPhotoMutableList = registeredPhoto as MutableList<PhotoEntity>

        } else {
            getAllPhotoMediatorLiveData.value = toViewState(registeredPhoto, addedPhoto)
        }
    }

    private fun toViewState(
        registeredPhoto: List<PhotoEntity>,
        addedPhoto: List<PhotoEntity>
    ): List<EditPropertyPhotoViewState> {

        val photoList = mutableListOf<EditPropertyPhotoViewState>()

        for (photo in registeredPhoto) {
            photoList.add(
                EditPropertyPhotoViewState(
                    photoDescription = photo.photoDescription,
                    photoUri = photo.photoUri,
                    photoId = photo.photoId,
                    propertyOwnerId = photo.propertyOwnerId,
                    photoTimestamp = photo.photoTimestamp,
                    photoCreationDate = photo.photoCreationDate
                )
            )
        }
        for (photo in addedPhoto) {
            photoList.add(
                EditPropertyPhotoViewState(
                    photoDescription = photo.photoDescription,
                    photoUri = photo.photoUri,
                    photoId = null,
                    propertyOwnerId = null,
                    photoTimestamp = photo.photoTimestamp,
                    photoCreationDate = photo.photoCreationDate
                )
            )
        }

        updatedRegisteredPhotoMutableList = registeredPhoto as MutableList<PhotoEntity>

        addedPhotoMutableList = addedPhoto as MutableList<PhotoEntity>


        return photoList
    }

    // Expose photo list to view
    val getPhoto: LiveData<List<EditPropertyPhotoViewState>> = getAllPhotoMediatorLiveData

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////  RETRIEVE AND DISPLAY PROPERTY  ///////////////////////////////

    val getDetailsLiveData: LiveData<EditPropertyViewState> =
        currentPropertyIdRepository.currentPropertyIdLiveData.switchMap { id ->
            propertiesRepository.getPropertyById(id).map { property ->

                uid = property.propertyEntity.uid
                vendor = property.propertyEntity.vendor
                propertyId = property.propertyEntity.propertyId
                propertyCreationDate = property.propertyEntity.propertyCreationDate
                dateToFormat = property.propertyEntity.creationDateToFormat

                sendRegisteredInterestsToRepository(property.propertyEntity.interest)
                sendRegisteredPhotosToRepository(property.photo)

                toViewState(property)

            }.asLiveData(applicationDispatchers.ioDispatcher)
        }

    private fun toViewState(property: PropertyWithPhoto) = EditPropertyViewState(
        propertyId = property.propertyEntity.propertyId,
        type = readableType(property.propertyEntity.type),
        description = property.propertyEntity.propertyDescription,
        surface = property.propertyEntity.surface,
        room = property.propertyEntity.room,
        bathroom = property.propertyEntity.bathroom,
        bedroom = property.propertyEntity.bedroom,
        address = property.propertyEntity.address,
        apartment = property.propertyEntity.apartmentNumber,
        city = property.propertyEntity.city,
        county = property.propertyEntity.county,
        zipcode = property.propertyEntity.zipcode,
        country = property.propertyEntity.country,
        startSale = property.propertyEntity.creationDateToFormat,
        createLocalDateTime = property.propertyEntity.propertyCreationDate,
        vendor = property.propertyEntity.vendor,
        visibility = true,
        staticMap = property.propertyEntity.staticMap,
        price = property.propertyEntity.price,
        uid = property.propertyEntity.uid,
    )

    private fun readableType(type: String): String {
        return if (type == "null") {
            ""
        } else type
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////  POPULATE CACHE REPOSITORY ONCE WITH REGISTERED DATA  ///////////

    // Create a photo list with old photo
    private fun sendRegisteredPhotosToRepository(photoList: List<PhotoEntity>) {
        if (registeredPhotoList.isEmpty()) {
            registeredPhotoRepository.sendRegisteredPhotoToRepository(photoList)
            registeredPhotoList = photoList as MutableList<PhotoEntity>
            updatedRegisteredPhotoMutableList = photoList
        }
    }

    // Create an interest list with old interests
    private fun sendRegisteredInterestsToRepository(oldInterests: List<String>?) {
        if (oldInterests != null) {
            if (interestList.isEmpty()) {
                for (interest in oldInterests) {
                    interestRepository.addInterest(interest)
                    interestList.add(interest)
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////  MANAGE INTERESTS /////////////////////////////////////////////

    // Expose interest list to view
    val getInterest: LiveData<List<String>> = interestRepository.getInterestLiveData()

    fun addInterest(interest: String) {
        if (interest.length > 2) {
            interestRepository.addInterest(interest)
            interestList.add(interest)
        }
    }

    fun removeInterest(interest: String) {
        interestRepository.remove(interest)
        interestList.remove(interest)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////  CREATE PROPERTY //////////////////////////////////////////////

    fun editProperty(
        address: String,
        apartmentNumber: String,
        county: String,
        city: String,
        zipcode: String,
        country: String,
        propertyDescription: String,
        type: String?,
        price: String,
        surface: String,
        room: String,
        bedroom: String,
        bathroom: String,
    ) {

        // Must contain at least one photo and an address (street, zip, city)
        if (addedPhotoMutableList.isNotEmpty() || updatedRegisteredPhotoMutableList.isNotEmpty()) {
            if (address != "" && city != "" && zipcode != "") {

                val timestamp = System.currentTimeMillis().toString()
                val property = PropertyUpdate(
                    uid = uid,
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
                    saleStatus = "On Sale !",
                    purchaseDate = null,
                    interest = interestCanBeNull(interestList),
                    propertyId = propertyId,
                    staticMap = staticMapUrl(address, zipcode, city),
                    updateTimestamp = timestamp,
                )

                viewModelScope.launch(applicationDispatchers.ioDispatcher) {

                    updatePropertyOnRoom(property)
                    updatePropertyOnFirestore(property)

                    checkForRegisteredPhoto()
                    createPhotoEntity()
                    updatePhotoOnCloudStorage()

                    emptyAllPhotoRepository()
                    emptyInterestRepository()

                    withContext(applicationDispatchers.mainDispatcher) {
                        actionSingleLiveEvent.postValue(ActivityViewAction.FINISH_ACTIVITY)

                    }
                }
            }
        } else {
            actionSingleLiveEvent.setValue(ActivityViewAction.FIELDS_ERROR)
        }
    }

    // Allow interest list to be null -> avoid to display "" interest
    private fun interestCanBeNull(interests: MutableList<String>): List<String>? {
        return interests.ifEmpty {
            null
        }
    }

    // Recreate an url to retrieve a miniature of the map with property marker
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////  MANAGE AND BUILD PHOTO LIST  /////////////////////////////////

    // If new photo, create photo and send to room DB
    private suspend fun createPhotoEntity() {

        val photoListWithPropertyId = mutableListOf<PhotoEntity>()

        if (addedPhotoMutableList.isNotEmpty()) {

            for (photo in addedPhotoMutableList) {
                val photoEntity = PhotoEntity(
                    photoUri = photo.photoUri,
                    photoDescription = photo.photoDescription,
                    propertyOwnerId = propertyId,
                    photoTimestamp = System.nanoTime().toString(),
                    photoCreationDate = propertyCreationDate
                )
                photoListWithPropertyId.add(photoEntity)
            }

            insertNewPhotoOnRoom(photoListWithPropertyId)

            photoFullList.addAll(photoListWithPropertyId)
        }
    }

    // Check if registered photo list has been modified, if so, update room DB
    private suspend fun checkForRegisteredPhoto() {

        val photoToDeleteId = mutableListOf<Int>()

        if (updatedRegisteredPhotoMutableList.size < registeredPhotoList.size) {
            for (photo in registeredPhotoList) {
                if (!updatedRegisteredPhotoMutableList.contains(photo)) {
                    photoToDeleteId.add(photo.photoId)
                }
            }
        }
        deletePhotoOnRoom(photoToDeleteId)
        photoFullList.addAll(updatedRegisteredPhotoMutableList)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////  UPDATE PROPERTY ON DATABASES /////////////////////////////////

    private suspend fun updatePropertyOnRoom(property: PropertyUpdate) =
        propertiesRepository.updateLightProperty(property)

    private suspend fun updatePropertyOnFirestore(property: PropertyUpdate) {
        sendPropertyToFirestoreRepository.updatePropertyDocumentFromEditView(
            property,
            propertyCreationDate,
            dateToFormat,
            vendor
        )
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////  UPDATE PHOTOS ON DATABASES ///////////////////////////////////

    private suspend fun insertNewPhotoOnRoom(photos: List<PhotoEntity>) =
        propertiesRepository.insertPhotos(photos)

    private suspend fun deletePhotoOnRoom(photoId: List<Int>) =
        propertiesRepository.deletePhotosById(photoId)

    private suspend fun updatePhotoOnCloudStorage() {
        sendPhotoToCloudStorageRepository.updatePhotoOnCloudStorage(photoFullList, propertyCreationDate, uid)

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////  CLEAR CACHES /////////////////////////////////////////////////

    // Clear the photoRepoS for the next use
    fun emptyAllPhotoRepository() {
        registeredPhotoRepository.emptyRegisteredPhotoList()
        createPhotoRepository.emptyCreatePhotoList()
    }

    // Clear the interestRepo for the next use
    fun emptyInterestRepository() {
        interestRepository.emptyInterestList()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////  WHEN PROPERTY IS SOLD ////////////////////////////////////////

    // When user chose to sold the property,
    // retrieve sale date, update all databases and close editPropertyActivity
    fun propertySold() {
        viewModelScope.launch(applicationDispatchers.ioDispatcher) {
            val saleDate = Utils.todayDate()
            propertiesRepository.updateSaleStatus("Sold", propertyId)
            propertiesRepository.updateSaleDate(saleDate, propertyId)
            sendPropertyToFirestoreRepository.updatePropertyWhenSold(uid, propertyCreationDate, saleDate)
            withContext(applicationDispatchers.mainDispatcher) {
                actionSingleLiveEvent.postValue(ActivityViewAction.FINISH_ACTIVITY)

            }
        }
    }
}