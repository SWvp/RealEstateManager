package com.kardabel.realestatemanager.ui.edit

import android.app.Application
import androidx.lifecycle.*
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.BuildConfig
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyUpdate
import com.kardabel.realestatemanager.repository.*
import com.kardabel.realestatemanager.ui.create.CreateActivityViewAction
import com.kardabel.realestatemanager.utils.SingleLiveEvent
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
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val interestRepository: InterestRepository,
    private val context: Application,
) : ViewModel() {

    val actionSingleLiveEvent = SingleLiveEvent<CreateActivityViewAction>()

    //private val interests = mutableListOf<String>()
    private var addedPhotoMutableList = mutableListOf<Photo>()
    private var registeredPhotoMutableList = mutableListOf<PhotoEntity>()

    var propertyId by Delegates.notNull<Long>()


    private val getOldPhoto: LiveData<List<PhotoEntity>> =
        registeredPhotoRepository.getRegisteredPhotoLiveData()
    private val getAddedPhoto: LiveData<List<Photo>> =
        createPhotoRepository.getAddedPhotoLiveData()

    private val getAllPhotoMediatorLiveData =
        MediatorLiveData<List<EditPropertyPhotoViewState>>().apply {

            addSource(getOldPhoto) { oldPhoto ->
                registeredPhotoMutableList = oldPhoto as MutableList<PhotoEntity>
                combine(oldPhoto, getAddedPhoto.value)
            }

            addSource(getAddedPhoto) { addedPhoto ->
                addedPhotoMutableList = addedPhoto as MutableList<Photo>
                combine(getOldPhoto.value, addedPhoto)
            }
        }

    private fun combine(oldPhoto: List<PhotoEntity>?, addedPhoto: List<Photo>?) {
        oldPhoto ?: return

        if (addedPhoto == null) {
            registeredPhotoMutableList = oldPhoto as MutableList<PhotoEntity>
            getAllPhotoMediatorLiveData.value = oldPhoto.map { photo ->
                EditPropertyPhotoViewState(
                    photoBitmap = photo.photo,
                    photoDescription = photo.photoDescription,
                    photoUri = photo.photoUri,
                    photoId = photo.photoId,
                    propertyOwnerId = photo.propertyOwnerId
                )
            }
        } else {
            getAllPhotoMediatorLiveData.value = toViewState(oldPhoto, addedPhoto)
        }
    }

    private fun toViewState(
        oldPhoto: List<PhotoEntity>,
        addedPhoto: List<Photo>
    ): List<EditPropertyPhotoViewState> {

        val photoList = mutableListOf<EditPropertyPhotoViewState>()

        for (photo in oldPhoto) {
            photoList.add(
                EditPropertyPhotoViewState(
                    photoBitmap = photo.photo,
                    photoDescription = photo.photoDescription,
                    photoUri = photo.photoUri,
                    photoId = photo.photoId,
                    propertyOwnerId = photo.propertyOwnerId
                )
            )
        }
        for (photo in addedPhoto) {
            photoList.add(
                EditPropertyPhotoViewState(
                    photoBitmap = photo.photoBitmap,
                    photoDescription = photo.photoDescription,
                    photoUri = photo.photoUri.toString(),
                    photoId = null,
                    propertyOwnerId = null
                )
            )
        }
        return photoList
    }

    // Expose photo to view
    val getPhoto: LiveData<List<EditPropertyPhotoViewState>> = getAllPhotoMediatorLiveData

    val getInterest: LiveData<List<String>> = interestRepository.getInterestLiveData()

    val getDetailsLiveData: LiveData<EditPropertyViewState> =
        currentPropertyIdRepository.currentPropertyIdLiveData.switchMap { id ->
            propertiesRepository.getPropertyById(id).map {

                emptyInterestRepository()
                registeredPhotoMutableList.clear()

                propertyId = it.propertyEntity.propertyId

                addOldInterests(it.propertyEntity.interest)

                retrieveOldPhotos(it.photo)

                EditPropertyViewState(
                    propertyId = it.propertyEntity.propertyId,
                    type = readableType(it.propertyEntity.type),
                    description = it.propertyEntity.propertyDescription,
                    surface = it.propertyEntity.surface?.toString(),
                    room = it.propertyEntity.room?.toString(),
                    bathroom = it.propertyEntity.bathroom?.toString(),
                    bedroom = it.propertyEntity.bedroom?.toString(),
                    //interest = it.propertyEntity.interest,
                    address = it.propertyEntity.address,
                    apartment = it.propertyEntity.apartmentNumber,
                    city = it.propertyEntity.city,
                    county = it.propertyEntity.county,
                    zipcode = it.propertyEntity.zipcode,
                    country = it.propertyEntity.country,
                    startSale = it.propertyEntity.createDateToFormat,
                    createLocalDateTime = it.propertyEntity.createLocalDateTime,
                    vendor = it.propertyEntity.vendor,
                    visibility = true,
                    staticMap = it.propertyEntity.staticMap,
                    price = it.propertyEntity.price?.toString(),
                    uid = it.propertyEntity.uid,
                )
            }.asLiveData(applicationDispatchers.ioDispatcher)
        }

    private fun readableType(type: String): String {
        return if (type == "null") {
            ""
        } else type
    }

    // Create a photo list with old photo
    private fun retrieveOldPhotos(photoList: List<PhotoEntity>) {
        registeredPhotoRepository.retrieveRegisteredPhoto(photoList)
        registeredPhotoMutableList = photoList as MutableList<PhotoEntity>

    }

    private fun addOldInterests(oldInterests: List<String>?) {
        if (oldInterests != null) {
            for (interest in oldInterests) {
                interestRepository.addInterest(interest)
            }
        }
    }

    fun addInterest(interest: String) {
        interestRepository.addInterest(interest)
        //interests.add(interest)
    }

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
        if (addedPhotoMutableList.isNotEmpty() || registeredPhotoMutableList.isNotEmpty()) {
            if (address != null && city != null && zipcode != null) {

                // Get value to entity format, string is for the view
                val priceToInt = price?.toIntOrNull()
                val surfaceToInt = surface?.toIntOrNull()
                val roomToInt = room?.toIntOrNull()
                val bedroomToInt = bedroom?.toIntOrNull()
                val bathroomToInt = bathroom?.toIntOrNull()

                val property = PropertyUpdate(
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
                    saleStatus = true,
                    purchaseDate = null,
                    interest = interestCanBeNull(interestRepository.getInterest()),
                    propertyId = propertyId,
                    staticMap = staticMapUrl(address, zipcode, city),
                )

                // Get the property id to update photoEntity
                viewModelScope.launch(applicationDispatchers.ioDispatcher) {
                    updateProperty(property)
                    createPhotoEntity()
                    //updatePhotos(registeredPhotoMutableList)
                }
                actionSingleLiveEvent.setValue(CreateActivityViewAction.FINISH_ACTIVITY)

            }
        } else {
            actionSingleLiveEvent.setValue(CreateActivityViewAction.FIELDS_ERROR)
        }
    }

    // Fun to allow interest list to be null -> avoid to display "" interest
    private fun interestCanBeNull(interests: MutableList<String>): List<String>? {
        return if (interests.size == 0) {
            null
        } else {
            interests
        }
    }

    // ReCreate an url to retrieve a miniature of the map with property marker
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

    // Compare old photo list to new, if differences appear, create new photo in database
    private suspend fun createPhotoEntity() {
        val photoListWithPropertyId = mutableListOf<PhotoEntity>()
        for (photo in addedPhotoMutableList) {
            val photoEntity = PhotoEntity(
                photo.photoBitmap,
                photo.photoUri.toString(),
                photo.photoDescription,
                propertyId,
            )
            photoListWithPropertyId.add(photoEntity)
        }
        insertNewPhoto(photoListWithPropertyId)
        emptyAllPhotoRepository()

        withContext(applicationDispatchers.mainDispatcher) {
            currentPropertyIdRepository.setCurrentPropertyId(propertyId)
        }
    }

    private suspend fun updateProperty(property: PropertyUpdate) =
        propertiesRepository.updateProperty(property)

    private suspend fun insertNewPhoto(photos: List<PhotoEntity>) =
        propertiesRepository.insertPhoto(photos)

    // Clear the photoRepo for the next use
    fun emptyAllPhotoRepository() {
        registeredPhotoRepository.emptyRegisteredPhotoList()
        createPhotoRepository.emptyCreatePhotoList()
    }

    private fun emptyInterestRepository() {
        interestRepository.emptyInterestList()
    }

    fun propertySold() {
        viewModelScope.launch(applicationDispatchers.ioDispatcher) {
            propertiesRepository.updateSaleStatus(false, propertyId)
        }
        actionSingleLiveEvent.setValue(CreateActivityViewAction.FINISH_ACTIVITY)
    }
}