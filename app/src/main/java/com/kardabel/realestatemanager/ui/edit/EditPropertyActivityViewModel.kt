package com.kardabel.realestatemanager.ui.edit

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.BuildConfig
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.model.Photo
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyUpdate
import com.kardabel.realestatemanager.repository.CurrentPropertyIdRepository
import com.kardabel.realestatemanager.repository.InterestRepository
import com.kardabel.realestatemanager.repository.PhotoRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
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
    private val photoRepository: PhotoRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val interestRepository: InterestRepository,
    private val context: Application,
) : ViewModel() {

    val actionSingleLiveEvent = SingleLiveEvent<CreateActivityViewAction>()

    private val interests = mutableListOf<String>()
    private var photoMutableList = mutableListOf<Photo>()
    private var oldPhotoMutableList = mutableListOf<Photo>()

    var propertyId by Delegates.notNull<Long>()


    val getPhoto: LiveData<List<EditPropertyPhotoViewState>> =
        photoRepository.getPhotoLiveData().map { photoList ->
            photoMutableList = photoList as MutableList<Photo>
            photoList.map { photo ->
                EditPropertyPhotoViewState(
                    photo.photo,
                    photo.photoDescription,
                )
            }
        }

    val getInterest: LiveData<List<String>> = interestRepository.getInterestLiveData()

    val getDetailsLiveData: LiveData<EditPropertyViewState> =
        currentPropertyIdRepository.currentPropertyIdLiveData.switchMap { id ->
            propertiesRepository.getPropertyById(id).map {

                emptyInterestRepository()

                propertyId = it.propertyEntity.propertyId

                addOldInterests(it.propertyEntity.interest)

                retrieveOldPhotos(it.photo)

                EditPropertyViewState(
                    propertyId = it.propertyEntity.propertyId,
                    type = it.propertyEntity.type,
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

    // Create a photo list with old photo
    private fun retrieveOldPhotos(photo: List<PhotoEntity>) {
        photo.map { photoEntity ->
            oldPhotoMutableList.add(
                Photo(
                    photoEntity.photo,
                    photoEntity.photoDescription,
                    Uri.parse(photoEntity.photoUri)

                )
            )
        }
        sendPhotoToPhotoRepository(oldPhotoMutableList)
    }

    private fun sendPhotoToPhotoRepository(oldPhotoList: MutableList<Photo>) {
        for (oldPhoto in oldPhotoList) {
            photoRepository.addPhoto(oldPhoto)
        }
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
        if (photoMutableList.isNotEmpty() || oldPhotoMutableList.isNotEmpty()) {
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
                }
                actionSingleLiveEvent.setValue(CreateActivityViewAction.FINISH_ACTIVITY)

            }
        } else {
            actionSingleLiveEvent.setValue(CreateActivityViewAction.FIELDS_ERROR)
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

    // Compare old photo list to new, if differences appear, create new photo in database
    private suspend fun createPhotoEntity() {
        val photoListWithPropertyId = mutableListOf<PhotoEntity>()
        if (photoMutableList != oldPhotoMutableList) {

            val newPhoto = photoMutableList.filterNot { oldPhotoMutableList.contains(it) }

            for (photo in newPhoto) {
                val photoEntity = PhotoEntity(
                    photo.photo,
                    photo.photoUri.toString(),
                    photo.photoDescription,
                    propertyId,
                )
                photoListWithPropertyId.add(photoEntity)
            }
            updatePhotosDataBase(photoListWithPropertyId)
        }
        emptyPhotoRepository()

        withContext(applicationDispatchers.mainDispatcher) {
            currentPropertyIdRepository.setCurrentPropertyId(propertyId)
        }
    }


    private suspend fun updatePhotosDataBase(photoEntities: MutableList<PhotoEntity>) {
        for (photo in photoEntities) {
            insertNewPhoto(photo)
        }
    }

    private suspend fun updateProperty(property: PropertyUpdate) =
        propertiesRepository.updateProperty(property)


    private suspend fun insertNewPhoto(photo: PhotoEntity) =
        propertiesRepository.insertNewPhoto(photo)

    // Clear the photoRepo for the next use
    fun emptyPhotoRepository() {
        photoRepository.emptyPhotoList()
    }

    fun emptyInterestRepository() {
        interestRepository.emptyInterestList()
    }


}