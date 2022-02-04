package com.kardabel.realestatemanager.ui.create

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.repository.PhotoRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import com.kardabel.realestatemanager.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class CreatePropertyViewModel @Inject constructor(
    private val propertiesRepository: PropertiesRepository,
    private val applicationDispatchers: ApplicationDispatchers,
    private val firebaseAuth: FirebaseAuth,
    private val photoRepository: PhotoRepository,
    private val clock: Clock,
    @ApplicationContext context: Context,

    ) : ViewModel() {

    private val propertyIdResponseLiveData: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }


    private val interests = mutableListOf<String>()
    private var photoEntities = mutableListOf<PhotoEntity>()
    var propertyId by Delegates.notNull<Long>()


    val getPhoto: LiveData<List<CreatePropertyPhotoViewState>> =
        photoRepository.getPhotoLiveData().map {
            it.map { photoEntity ->
                photoEntities = it as MutableList<PhotoEntity>
                CreatePropertyPhotoViewState(
                    photoEntity.photo,
                    photoEntity.photoDescription,
                )
            }
        }

    // val getPhoto: LiveData<List<CreatePropertyPhotoViewState>> = photoRepository.getPhoto().map {
    //     it.map { photoEntity ->
    //         photoList = it as MutableList<PhotoEntity>
    //         CreatePropertyPhotoViewState(
    //             photoEntity.photo,
    //             photoEntity.photoDescription,
    //         )
    //     }
    // }.asLiveData(applicationDispatchers.ioDispatcher)


    fun addInterest(interest: String) {
        interests.add(interest)

    }
    fun addPhoto(photo: Bitmap, photoDescription: String) {
        photoRepository.addPhoto(
            PhotoEntity(
                photo,
                photoDescription,
                null,
            )
        )
    }


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

        //createPhotoEntityWithPropertyId(propertyId)
        insertProperty(property)
        createPhotoEntityWithPropertyId()

    }

    private fun thisCantShowNull(it: Any?): Any {
        var item: Any? = null
        if(it == null){
            item = ""
        }
        return item!!
    }

    private fun createPhotoEntityWithPropertyId() {
        val photoListWithPropertyId = mutableListOf<PhotoEntity>()
        for (photoEntity in photoEntities){
            photoEntity.propertyOwnerId = propertyIdResponseLiveData.value
            photoListWithPropertyId.add(photoEntity)
        }
        sendPhotoToDataBase(photoListWithPropertyId)
    }

    private fun sendPhotoToDataBase(photoEntities: MutableList<PhotoEntity>) {
        for(photoEntity in photoEntities){
            insertPhoto(photoEntity)
        }
    }

    private fun insertProperty(property: PropertyEntity) =
        viewModelScope.launch(applicationDispatchers.ioDispatcher) {
            propertyIdResponseLiveData.postValue(propertiesRepository.insertProperty(property))
        }

    private fun insertPhoto(photo: PhotoEntity) =
        viewModelScope.launch(applicationDispatchers.ioDispatcher) {
            propertiesRepository.insertPhoto(photo)
        }
}