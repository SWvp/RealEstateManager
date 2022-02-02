package com.kardabel.realestatemanager.ui.create

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class CreatePropertyViewModel @Inject constructor(
    private val propertiesRepository: PropertiesRepository,
    private val applicationDispatchers: ApplicationDispatchers,
    private val firebaseAuth: FirebaseAuth,
    private val photoRepository: PhotoRepository,
    @ApplicationContext context: Context,

    ) : ViewModel() {


    private val interests = mutableListOf<String>()
    private var photoList = mutableListOf<PhotoEntity>()
    var propertyId by Delegates.notNull<Int>()


    val getPhoto: LiveData<List<CreatePropertyPhotoViewState>> =
        photoRepository.getPhotoLiveData().map {
            it.map { photoEntity ->
                photoList = it as MutableList<PhotoEntity>
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


    //  private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
    //      val bytes = ByteArrayOutputStream()
    //      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    //      val path =
    //          MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
    //      return Uri.parse(path.toString())
    //  }


    fun addInterest(interest: String) {
        interests.add(interest)

    }

    fun createProperty(
        address: String?,
        apartmentNumber: String?,
        city: String?,
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

        val priceToFloat = price?.toFloat()
        val surfaceToDouble = surface?.toDouble()
        val roomToInt = room?.toInt()
        val bedroomToInt = bedroom?.toInt()
        val bathroomToInt = bathroom?.toInt()
        val uid = firebaseAuth.currentUser!!.uid
        val createDateToFormat = Utils.getTodayDate()


        val property = PropertyEntity(
            address = address,
            apartmentNumber = apartmentNumber,
            city = city,
            postalCode = postalCode,
            country = country,
            propertyDescription = propertyDescription,
            type = type,
            price = priceToFloat,
            surface = surfaceToDouble,
            room = roomToInt,
            bedroom = bedroomToInt,
            bathroom = bathroomToInt,
            uid = uid,
            createDate = createDateToFormat,
            saleStatus = true,
            purchaseDate = null,
            interest = interests,
        )

        propertyId = property.propertyId
        //createPhotoListForProperty(photoList,propertyId)
        insertProperty(property)
    }

// fun addPhoto(photo: Bitmap,
//                         photoDescription: String,) =
//     viewModelScope.launch(applicationDispatchers.ioDispatcher) {
//         photoRepository.addPhoto(
//             PhotoEntity(
//                 photo,
//                 photoDescription,
//                 null,
//             )
//         )
//     }

    fun addPhoto(photo: Bitmap, photoDescription: String) {
        photoRepository.addPhoto(
            PhotoEntity(
                photo,
                photoDescription,
                null,
            )
        )
    }

    private fun createPhotoListForProperty(
        photoList: MutableList<PhotoEntity> = mutableListOf()
    ) {
    }

    private fun insertProperty(property: PropertyEntity) =
        viewModelScope.launch(applicationDispatchers.ioDispatcher) {
            propertiesRepository.insertProperty(property)
        }

    private fun insertPhoto(photo: PhotoEntity) =
        viewModelScope.launch(applicationDispatchers.ioDispatcher) {
            propertiesRepository.insertPhoto(photo)
        }
}