package com.kardabel.realestatemanager.ui.properties

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.kardabel.realestatemanager.TestCoroutineRule
import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import com.kardabel.realestatemanager.model.SearchParams
import com.kardabel.realestatemanager.repository.CurrentPropertyIdRepository
import com.kardabel.realestatemanager.repository.CurrentSearchRepository
import com.kardabel.realestatemanager.repository.PriceConverterRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
class PropertyViewModelTest {

    companion object {

        private const val EXPECTED_ADDRESS = "first_address"
        private const val EXPECTED_APARTMENT_NUMBER = "first_apartment_number"
        private const val EXPECTED_CITY = "first_city"
        private const val EXPECTED_ZIPCODE = "first_zipcode"
        private const val EXPECTED_COUNTY = "first_county"
        private const val EXPECTED_COUNTRY = "first_country"
        private const val EXPECTED_DESCRIPTION = "first_propertyDescription"
        private const val EXPECTED_TYPE = "first_type"
        private const val EXPECTED_PRICE = "first_price"
        private const val EXPECTED_SURFACE = "first_surface"
        private const val EXPECTED_ROOM = "first_room"
        private const val EXPECTED_BEDROOM = "first_bedroom"
        private const val EXPECTED_BATHROOM = "first_bathroom"
        private const val EXPECTED_UID = "first_uid"
        private const val EXPECTED_VENDOR = "first_vendor"
        private const val EXPECTED_STATIC_MAP = "first_staticMap"
        private const val EXPECTED_CREATION_DATE = "first_createLocalDateTime"
        private const val EXPECTED_CREATION_DATE_FORMAT = "first_createDateToFormat"
        private const val EXPECTED_SALE_STATUS = "On Sale !"
        private val EXPECTED_PURCHASE_DATE = null
        private val EXPECTED_INTERESTS = null
        private const val EXPECTED_CURRENT_PROPERTY_ID = 42L

        private const val EXPECTED_PHOTO_URI = "first_photoUri"
        private const val EXPECTED_PHOTO_DESCRIPTION = "first_photo_description"
        private const val EXPECTED_TIMESTAMP = "first_timestamp"
        private const val EXPECTED_PHOTO_ID = 666


        private const val PRICE_MIN_EXPECTED = 100000
        private const val PRICE_MAX_EXPECTED = 10000000

    }

    @get: Rule
    val testCoroutineRule = TestCoroutineRule()

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val propertiesRepository: PropertiesRepository = mockk()

    private val currentPropertyIdRepository: CurrentPropertyIdRepository = mockk()

    private val priceConverterRepository: PriceConverterRepository = mockk()

    private val currentSearchRepository: CurrentSearchRepository = mockk()

    private val propertiesDao: PropertiesDao = mockk()

    @Before
    fun setUp() {

        every { propertiesRepository.getProperties() } returns flowOf(
            getDefaultPropertiesWithPhoto()
        )

        every { currentPropertyIdRepository.currentPropertyIdLiveData } returns MutableLiveData<Long>().apply {
            value = EXPECTED_CURRENT_PROPERTY_ID
        }

        every { priceConverterRepository.getCurrentCurrencyLiveData } returns MutableLiveData<Boolean>().apply {
            value = true
        }

        every { currentSearchRepository.searchParamsParamsFlow() } returns flowOf {
            getDefaultSearchParams()
        }


    }


    // region IN
    private fun getDefaultPropertiesWithPhoto(): List<PropertyWithPhoto> {
        val propertiesList = mutableListOf<PropertyWithPhoto>()
        propertiesList.add(
            PropertyWithPhoto(
                propertyEntity = PropertyEntity(
                    address = EXPECTED_ADDRESS,
                    apartmentNumber = EXPECTED_APARTMENT_NUMBER,
                    city = EXPECTED_CITY,
                    zipcode = EXPECTED_ZIPCODE,
                    county = EXPECTED_COUNTY,
                    country = EXPECTED_COUNTRY,
                    propertyDescription = EXPECTED_DESCRIPTION,
                    type = EXPECTED_TYPE,
                    price = EXPECTED_PRICE,
                    surface = EXPECTED_SURFACE,
                    room = EXPECTED_ROOM,
                    bedroom = EXPECTED_BEDROOM,
                    bathroom = EXPECTED_BATHROOM,
                    uid = EXPECTED_UID,
                    vendor = EXPECTED_VENDOR,
                    staticMap = EXPECTED_STATIC_MAP,
                    propertyCreationDate = EXPECTED_CREATION_DATE,
                    creationDateToFormat = EXPECTED_CREATION_DATE_FORMAT,
                    saleStatus = EXPECTED_SALE_STATUS,
                    purchaseDate = EXPECTED_PURCHASE_DATE,
                    interest = EXPECTED_INTERESTS,
                    propertyId = EXPECTED_CURRENT_PROPERTY_ID
                ),
                photo = listOf(
                    PhotoEntity(
                        photoUri = EXPECTED_PHOTO_URI,
                        photoDescription = EXPECTED_PHOTO_DESCRIPTION,
                        propertyOwnerId = EXPECTED_CURRENT_PROPERTY_ID,
                        photoTimestamp = EXPECTED_TIMESTAMP,
                        photoCreationDate = EXPECTED_CREATION_DATE,
                        photoId = EXPECTED_PHOTO_ID,
                    )
                )
            )
        )
        propertiesList.add(
            PropertyWithPhoto(
                propertyEntity = PropertyEntity(
                    address = EXPECTED_ADDRESS + 1,
                    apartmentNumber = EXPECTED_APARTMENT_NUMBER + 1,
                    city = EXPECTED_CITY + 1,
                    zipcode = EXPECTED_ZIPCODE + 1,
                    county = EXPECTED_COUNTY + 1,
                    country = EXPECTED_COUNTRY + 1,
                    propertyDescription = EXPECTED_DESCRIPTION + 1,
                    type = EXPECTED_TYPE + 1,
                    price = EXPECTED_PRICE + 1,
                    surface = EXPECTED_SURFACE + 1,
                    room = EXPECTED_ROOM + 1,
                    bedroom = EXPECTED_BEDROOM + 1,
                    bathroom = EXPECTED_BATHROOM + 1,
                    uid = EXPECTED_UID + 1,
                    vendor = EXPECTED_VENDOR + 1,
                    staticMap = EXPECTED_STATIC_MAP + 1,
                    propertyCreationDate = EXPECTED_CREATION_DATE + 1,
                    creationDateToFormat = EXPECTED_CREATION_DATE_FORMAT + 1,
                    saleStatus = EXPECTED_SALE_STATUS + 1,
                    purchaseDate = EXPECTED_PURCHASE_DATE,
                    interest = EXPECTED_INTERESTS,
                    propertyId = EXPECTED_CURRENT_PROPERTY_ID + 1
                ),
                photo = listOf(
                    PhotoEntity(
                        photoUri = EXPECTED_PHOTO_URI + 1,
                        photoDescription = EXPECTED_PHOTO_DESCRIPTION + 1,
                        propertyOwnerId = EXPECTED_CURRENT_PROPERTY_ID + 1,
                        photoTimestamp = EXPECTED_TIMESTAMP + 1,
                        photoCreationDate = EXPECTED_CREATION_DATE + 1,
                        photoId = EXPECTED_PHOTO_ID + 1,
                    )
                )
            )
        )
        propertiesList.add(
            PropertyWithPhoto(
                propertyEntity = PropertyEntity(
                    address = EXPECTED_ADDRESS + 2,
                    apartmentNumber = EXPECTED_APARTMENT_NUMBER + 2,
                    city = EXPECTED_CITY + 2,
                    zipcode = EXPECTED_ZIPCODE + 2,
                    county = EXPECTED_COUNTY + 2,
                    country = EXPECTED_COUNTRY + 2,
                    propertyDescription = EXPECTED_DESCRIPTION + 2,
                    type = EXPECTED_TYPE + 2,
                    price = EXPECTED_PRICE + 2,
                    surface = EXPECTED_SURFACE + 2,
                    room = EXPECTED_ROOM + 2,
                    bedroom = EXPECTED_BEDROOM + 2,
                    bathroom = EXPECTED_BATHROOM + 2,
                    uid = EXPECTED_UID + 2,
                    vendor = EXPECTED_VENDOR + 2,
                    staticMap = EXPECTED_STATIC_MAP + 2,
                    propertyCreationDate = EXPECTED_CREATION_DATE + 2,
                    creationDateToFormat = EXPECTED_CREATION_DATE_FORMAT + 2,
                    saleStatus = EXPECTED_SALE_STATUS + 2,
                    purchaseDate = EXPECTED_PURCHASE_DATE,
                    interest = EXPECTED_INTERESTS,
                    propertyId = EXPECTED_CURRENT_PROPERTY_ID + 2
                ),
                photo = listOf(
                    PhotoEntity(
                        photoUri = EXPECTED_PHOTO_URI + 2,
                        photoDescription = EXPECTED_PHOTO_DESCRIPTION + 2,
                        propertyOwnerId = EXPECTED_CURRENT_PROPERTY_ID + 2,
                        photoTimestamp = EXPECTED_TIMESTAMP + 2,
                        photoCreationDate = EXPECTED_CREATION_DATE + 2,
                        photoId = EXPECTED_PHOTO_ID + 2,
                    )
                )
            )
        )

        return propertiesList

    }


    private fun getDefaultSearchParams() = SearchParams(

        priceRange = IntRange(
            PRICE_MIN_EXPECTED,
            PRICE_MAX_EXPECTED,
        ),
        surfaceRange = null,
        roomRange = null,

        photo = null,

        propertyType = null,

        interest = null,

        county = null,

        )


}