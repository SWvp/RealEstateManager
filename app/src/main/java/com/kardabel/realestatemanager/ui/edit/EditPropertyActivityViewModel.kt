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
import com.kardabel.realestatemanager.utils.ActivityViewAction
import com.kardabel.realestatemanager.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class EditPropertyActivityViewModel @Inject constructor(
    private val propertiesRepository: PropertiesRepository,
    private val applicationDispatchers: ApplicationDispatchers,
    private val createPhotoRepository: CreatePhotoRepository,
    private val registeredPhotoRepository: RegisteredPhotoRepository,
    currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val interestRepository: InterestRepository,
    private val context: Application,
) : ViewModel() {

    val actionSingleLiveEvent = SingleLiveEvent<ActivityViewAction>()

    private var addedPhotoMutableList = mutableListOf<Photo>()
    private var updatedRegisteredPhotoMutableList = mutableListOf<PhotoEntity>()
    private var registeredPhotoMutableList = mutableListOf<PhotoEntity>()

    var propertyId by Delegates.notNull<Long>()

    // Expose interest list to view
    val getInterest: LiveData<List<String>> = interestRepository.getInterestLiveData()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////  MANAGE PHOTOS  /////////////////////////////////////////////

    private val getRegisteredPhoto: LiveData<List<PhotoEntity>> =
        registeredPhotoRepository.getRegisteredPhotoLiveData()
    private val getAddedPhoto: LiveData<List<Photo>> =
        createPhotoRepository.getAddedPhotoLiveData()

    private val getAllPhotoMediatorLiveData =
        MediatorLiveData<List<EditPropertyPhotoViewState>>().apply {

            addSource(getRegisteredPhoto) { oldPhoto ->
                updatedRegisteredPhotoMutableList = oldPhoto as MutableList<PhotoEntity>
                combine(oldPhoto, getAddedPhoto.value)
            }

            addSource(getAddedPhoto) { addedPhoto ->
                addedPhotoMutableList = addedPhoto as MutableList<Photo>
                combine(getRegisteredPhoto.value, addedPhoto)
            }
        }

    private fun combine(oldPhoto: List<PhotoEntity>?, addedPhoto: List<Photo>?) {
        oldPhoto ?: return

        if (addedPhoto == null) {
            // updatedRegisteredPhotoMutableList = oldPhoto as MutableList<PhotoEntity>
            getAllPhotoMediatorLiveData.value = oldPhoto.map { photo ->
                EditPropertyPhotoViewState(
                    //photoBitmap = photo.photo,
                    photoDescription = photo.photoDescription,
                    photoUri = photo.photoString,
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
                    //photoBitmap = photo.photo,
                    photoDescription = photo.photoDescription,
                    photoUri = photo.photoString,
                    photoId = photo.photoId,
                    propertyOwnerId = photo.propertyOwnerId
                )
            )
        }
        for (photo in addedPhoto) {
            photoList.add(
                EditPropertyPhotoViewState(
                    //photoBitmap = photo.photoBitmap,
                    photoDescription = photo.photoDescription,
                    photoUri = photo.photoUri.toString(),
                    photoId = null,
                    propertyOwnerId = null
                )
            )
        }
        return photoList
    }

    // Expose photo list to view
    val getPhoto: LiveData<List<EditPropertyPhotoViewState>> = getAllPhotoMediatorLiveData

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////  RETRIEVE AND DISPLAY PROPERTY  ///////////////////////////////

    val getDetailsLiveData: LiveData<EditPropertyViewState> =
        currentPropertyIdRepository.currentPropertyIdLiveData.switchMap { id ->
            propertiesRepository.getPropertyById(id).map {

                emptyInterestRepository()
                updatedRegisteredPhotoMutableList.clear()

                propertyId = it.propertyEntity.propertyId

                sendRegisteredInterestsToRepository(it.propertyEntity.interest)

                sendRegisteredPhotosToRepository(it.photo)

                EditPropertyViewState(
                    propertyId = it.propertyEntity.propertyId,
                    type = readableType(it.propertyEntity.type),
                    description = it.propertyEntity.propertyDescription,
                    surface = it.propertyEntity.surface?.toString(),
                    room = it.propertyEntity.room?.toString(),
                    bathroom = it.propertyEntity.bathroom?.toString(),
                    bedroom = it.propertyEntity.bedroom?.toString(),
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
    private fun sendRegisteredPhotosToRepository(photoList: List<PhotoEntity>) {
        registeredPhotoRepository.sendRegisteredPhotoToRepository(photoList)
        registeredPhotoMutableList = photoList as MutableList<PhotoEntity>

    }

    private fun sendRegisteredInterestsToRepository(oldInterests: List<String>?) {
        if (oldInterests != null) {
            for (interest in oldInterests) {
                interestRepository.addInterest(interest)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////  EDIT PROPERTY ////////////////////////////////////////////////


    fun addInterest(interest: String) {
        if (interest.length > 2) {
            interestRepository.addInterest(interest)
        }
    }

    fun removeInterest(interest: String) {
        interestRepository.remove(interest)
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
        if (addedPhotoMutableList.isNotEmpty() || updatedRegisteredPhotoMutableList.isNotEmpty()) {
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
                    async {
                        updateProperty(property)
                    }
                    async {
                        checkForRegisteredPhoto()
                    }
                    async {
                        createPhotoEntity()
                    }
                }

                actionSingleLiveEvent.setValue(ActivityViewAction.FINISH_ACTIVITY)

            }
        } else {
            actionSingleLiveEvent.setValue(ActivityViewAction.FIELDS_ERROR)
        }

        emptyAllPhotoRepository()
        emptyInterestRepository()
    }

    // Allow interest list to be null -> avoid to display "" interest
    private fun interestCanBeNull(interests: MutableList<String>): List<String>? {
        return if (interests.size == 0) {
            null
        } else {
            interests
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

    // If new photo, create photo and send to room DB
    private suspend fun createPhotoEntity() {

        val photoListWithPropertyId = mutableListOf<PhotoEntity>()

        if(addedPhotoMutableList.isNotEmpty()){

            for (photo in addedPhotoMutableList) {
                val photoEntity = PhotoEntity(
                    //photo.photoBitmap,
                    photo.photoUri.toString(),
                    photo.photoDescription,
                    propertyId,
                )
                photoListWithPropertyId.add(photoEntity)
            }
            insertNewPhoto(photoListWithPropertyId)
        }
    }

    // Check if registered photo list has been modified, if so, update room DB
    private suspend fun checkForRegisteredPhoto() {

        val photoToDeleteId = mutableListOf<Int>()

        if (updatedRegisteredPhotoMutableList.size < registeredPhotoMutableList.size) {
            for (photo in registeredPhotoMutableList) {
                if (!updatedRegisteredPhotoMutableList.contains(photo)) {
                    photoToDeleteId.add(photo.photoId)
                }
            }
        }
        deletePhoto(photoToDeleteId)
    }

    private suspend fun updateProperty(property: PropertyUpdate) =
        propertiesRepository.updateProperty(property)

    private suspend fun insertNewPhoto(photos: List<PhotoEntity>) =
        propertiesRepository.insertPhotos(photos)

    private suspend fun deletePhoto(photoId: List<Int>) =
        propertiesRepository.deletePhotos(photoId)

    // Clear the photoRepoS for the next use
    fun emptyAllPhotoRepository() {
        registeredPhotoRepository.emptyRegisteredPhotoList()
        createPhotoRepository.emptyCreatePhotoList()
    }

    // Clear the interestRepo for the next use
    fun emptyInterestRepository() {
        interestRepository.emptyInterestList()
    }

    fun propertySold() {
        viewModelScope.launch(applicationDispatchers.ioDispatcher) {
            propertiesRepository.updateSaleStatus(false, propertyId)
        }
    }
}