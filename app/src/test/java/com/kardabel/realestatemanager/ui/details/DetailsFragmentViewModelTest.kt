package com.kardabel.realestatemanager.ui.details

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.TestCoroutineRule
import com.kardabel.realestatemanager.getApplicationDispatchersTest
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import com.kardabel.realestatemanager.observeForTesting
import com.kardabel.realestatemanager.repository.CurrentPropertyIdRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class DetailsFragmentViewModelTest {

    companion object {
        private const val EXPECTED_ADDRESS = "first_address"
        private const val EXPECTED_APARTMENT_NUMBER = "first_apartment_number"
        private const val EXPECTED_CITY = "first_city"
        private const val EXPECTED_ZIPCODE = "first_zipcode"
        private const val EXPECTED_COUNTY = "first_county"
        private const val EXPECTED_COUNTRY = "first_country"
        private const val EXPECTED_DESCRIPTION = "first_propertyDescription"
        private const val EXPECTED_TYPE = "first_type"
        private const val EXPECTED_PRICE_DOLLARS = "1000000"
        private const val EXPECTED_SURFACE = "100"
        private const val EXPECTED_ROOM = "5"
        private const val EXPECTED_BEDROOM = "first_bedroom"
        private const val EXPECTED_BATHROOM = "first_bathroom"
        private const val EXPECTED_UID = "first_uid"
        private const val EXPECTED_VENDOR = "first_vendor"
        private const val EXPECTED_STATIC_MAP = "first_staticMap"
        private const val EXPECTED_CREATION_DATE = "first_createLocalDateTime"
        private const val EXPECTED_CREATION_DATE_FORMAT = "first_createDateToFormat"
        private const val EXPECTED_SALE_STATUS = "On Sale !"
        private val EXPECTED_PURCHASE_DATE = null
        private const val EXPECTED_PURCHASE_AVAILABLE = "1234"
        private val EXPECTED_EMPTY_INTERESTS = ArrayList<String>()
        private val EXPECTED_INTERESTS = listOf("first interest", "second interest")
        private const val EXPECTED_CURRENT_PROPERTY_ID = 42L

        private const val EXPECTED_PHOTO_URI = "first_photoUri"
        private const val EXPECTED_PHOTO_DESCRIPTION = "first_photo_description"
        private const val EXPECTED_TIMESTAMP = "first_timestamp"
        private const val EXPECTED_PHOTO_ID = 666
    }

    @get: Rule
    val testCoroutineRule = TestCoroutineRule()

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val currentPropertyIdRepository: CurrentPropertyIdRepository = mockk()

    private val propertiesRepository: PropertiesRepository = mockk()

    private val context = Mockito.mock(Application::class.java)

    @Before
    fun setUp() {

        // STRINGS RETURNS
        Mockito.doReturn("Ongoing sale !").`when`(context).getString(R.string.ongoing_sale)
        Mockito.doReturn("m²").`when`(context).getString(R.string.meter)

        // REPOSITORY RETURNS
        every { currentPropertyIdRepository.currentPropertyIdLiveData } returns MutableLiveData<Long>().apply {
            value = EXPECTED_CURRENT_PROPERTY_ID
        }

        every { currentPropertyIdRepository.isFromSearchLiveData } returns MutableLiveData<Boolean>().apply {
            value = true
        }

        every { propertiesRepository.getPropertyById(EXPECTED_CURRENT_PROPERTY_ID) } returns flowOf(
            getDefaultPropertyWithPhoto()
        )
    }

    @Test
    fun `nominal case`() = runTest {
        // When
        getViewModel().detailsLiveData.observeForTesting { result ->
            // Then
            assertEquals(
                getDefaultDetailsViewState(),
                result
            )
        }
    }

    @Test
    fun `property with interests should display interest`() = runTest {
        // When
        every { propertiesRepository.getPropertyById(EXPECTED_CURRENT_PROPERTY_ID) } returns flowOf(
            getDefaultPropertyWithInterests()
        )
        getViewModel().detailsLiveData.observeForTesting { result ->
            // Then
            assertEquals(
                getDetailsViewStateWithInterests(),
                result
            )
        }
    }

    @Test
    fun `property sold should display purchase date`() = runTest {
        // When
        every { propertiesRepository.getPropertyById(EXPECTED_CURRENT_PROPERTY_ID) } returns flowOf(
            getSoldProperty()
        )
        getViewModel().detailsLiveData.observeForTesting { result ->
            // Then
            assertEquals(
                getSoldPropertyViewState(),
                result
            )
        }
    }

    @Test
    fun `no property surface should not show suffix`() = runTest {
        // When
        every { propertiesRepository.getPropertyById(EXPECTED_CURRENT_PROPERTY_ID) } returns flowOf(
            getNoSurfaceProperty()
        )
        getViewModel().detailsLiveData.observeForTesting { result ->
            // Then
            assertEquals(
                getNoSurfacePropertyViewState(),
                result
            )
        }
    }

    @Test
    fun `property with minimum field required should display only required field`() = runTest {
        // When
        every { propertiesRepository.getPropertyById(EXPECTED_CURRENT_PROPERTY_ID) } returns flowOf(
            getMinimumProperty()
        )
        getViewModel().detailsLiveData.observeForTesting { result ->
            // Then
            assertEquals(
                getMinimumPropertyViewState(),
                result
            )
        }
    }

    @Test
    fun `property with two photos display two photos`() = runTest {
        // When
        every { propertiesRepository.getPropertyById(EXPECTED_CURRENT_PROPERTY_ID) } returns flowOf(
            getPropertyWithTwoPhotos()
        )
        getViewModel().detailsLiveData.observeForTesting { result ->
            // Then
            assertEquals(
                getPropertyWithTwoPhotosViewState(),
                result
            )
        }
    }

    private fun getViewModel() = DetailsFragmentViewModel(
        currentPropertyIdRepository = currentPropertyIdRepository,
        propertiesRepository = propertiesRepository,
        context = context,
        applicationDispatchers = getApplicationDispatchersTest(testCoroutineRule)
    )

    // region IN
    private fun getDefaultPropertyWithPhoto() = PropertyWithPhoto(
        propertyEntity = PropertyEntity(
            address = EXPECTED_ADDRESS,
            apartmentNumber = EXPECTED_APARTMENT_NUMBER,
            city = EXPECTED_CITY,
            zipcode = EXPECTED_ZIPCODE,
            county = EXPECTED_COUNTY,
            country = EXPECTED_COUNTRY,
            propertyDescription = EXPECTED_DESCRIPTION,
            type = EXPECTED_TYPE,
            price = EXPECTED_PRICE_DOLLARS,
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
            interest = EXPECTED_EMPTY_INTERESTS,
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

    private fun getDefaultPropertyWithInterests() = PropertyWithPhoto(
        propertyEntity = PropertyEntity(
            address = EXPECTED_ADDRESS,
            apartmentNumber = EXPECTED_APARTMENT_NUMBER,
            city = EXPECTED_CITY,
            zipcode = EXPECTED_ZIPCODE,
            county = EXPECTED_COUNTY,
            country = EXPECTED_COUNTRY,
            propertyDescription = EXPECTED_DESCRIPTION,
            type = EXPECTED_TYPE,
            price = EXPECTED_PRICE_DOLLARS,
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

    private fun getSoldProperty() = PropertyWithPhoto(
        propertyEntity = PropertyEntity(
            address = EXPECTED_ADDRESS,
            apartmentNumber = EXPECTED_APARTMENT_NUMBER,
            city = EXPECTED_CITY,
            zipcode = EXPECTED_ZIPCODE,
            county = EXPECTED_COUNTY,
            country = EXPECTED_COUNTRY,
            propertyDescription = EXPECTED_DESCRIPTION,
            type = EXPECTED_TYPE,
            price = EXPECTED_PRICE_DOLLARS,
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
            purchaseDate = EXPECTED_PURCHASE_AVAILABLE,
            interest = EXPECTED_EMPTY_INTERESTS,
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

    private fun getNoSurfaceProperty() = PropertyWithPhoto(
        propertyEntity = PropertyEntity(
            address = EXPECTED_ADDRESS,
            apartmentNumber = EXPECTED_APARTMENT_NUMBER,
            city = EXPECTED_CITY,
            zipcode = EXPECTED_ZIPCODE,
            county = EXPECTED_COUNTY,
            country = EXPECTED_COUNTRY,
            propertyDescription = EXPECTED_DESCRIPTION,
            type = EXPECTED_TYPE,
            price = EXPECTED_PRICE_DOLLARS,
            surface = "",
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
            interest = EXPECTED_EMPTY_INTERESTS,
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

    private fun getMinimumProperty() = PropertyWithPhoto(
        propertyEntity = PropertyEntity(
            address = EXPECTED_ADDRESS,
            apartmentNumber = "",
            city = EXPECTED_CITY,
            zipcode = EXPECTED_ZIPCODE,
            county = "",
            country = "",
            propertyDescription = "",
            type = "",
            price = "",
            surface = "",
            room = "",
            bedroom = "",
            bathroom = "",
            uid = EXPECTED_UID,
            vendor = EXPECTED_VENDOR,
            staticMap = EXPECTED_STATIC_MAP,
            propertyCreationDate = EXPECTED_CREATION_DATE,
            creationDateToFormat = EXPECTED_CREATION_DATE_FORMAT,
            saleStatus = EXPECTED_SALE_STATUS,
            purchaseDate = EXPECTED_PURCHASE_DATE,
            interest = EXPECTED_EMPTY_INTERESTS,
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

    private fun getPropertyWithTwoPhotos() = PropertyWithPhoto(
        propertyEntity = PropertyEntity(
            address = EXPECTED_ADDRESS,
            apartmentNumber = "",
            city = EXPECTED_CITY,
            zipcode = EXPECTED_ZIPCODE,
            county = "",
            country = "",
            propertyDescription = "",
            type = "",
            price = "",
            surface = "",
            room = "",
            bedroom = "",
            bathroom = "",
            uid = EXPECTED_UID,
            vendor = EXPECTED_VENDOR,
            staticMap = EXPECTED_STATIC_MAP,
            propertyCreationDate = EXPECTED_CREATION_DATE,
            creationDateToFormat = EXPECTED_CREATION_DATE_FORMAT,
            saleStatus = EXPECTED_SALE_STATUS,
            purchaseDate = EXPECTED_PURCHASE_DATE,
            interest = EXPECTED_EMPTY_INTERESTS,
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
            ),
            PhotoEntity(
                photoUri = EXPECTED_PHOTO_URI,
                photoDescription = EXPECTED_PHOTO_DESCRIPTION,
                propertyOwnerId = EXPECTED_CURRENT_PROPERTY_ID,
                photoTimestamp = EXPECTED_TIMESTAMP,
                photoCreationDate = EXPECTED_CREATION_DATE,
                photoId = EXPECTED_PHOTO_ID,
            ),
        )
    )

    // endregion IN

    // region OUT
    private fun getDefaultDetailsViewState() = DetailsViewState(
        propertyId = EXPECTED_CURRENT_PROPERTY_ID,
        photos = listOf(
            DetailsPhotoViewState(
                EXPECTED_PHOTO_URI,
                EXPECTED_PHOTO_DESCRIPTION

            )
        ),
        description = EXPECTED_DESCRIPTION,
        surface = EXPECTED_SURFACE + "m²",
        room = EXPECTED_ROOM,
        bathroom = EXPECTED_BATHROOM,
        bedroom = EXPECTED_BEDROOM,
        interest = null,
        address = EXPECTED_ADDRESS,
        apartment = EXPECTED_APARTMENT_NUMBER,
        city = EXPECTED_CITY,
        county = EXPECTED_COUNTY,
        zipcode = EXPECTED_ZIPCODE,
        country = EXPECTED_COUNTRY,
        startSale = EXPECTED_CREATION_DATE_FORMAT,
        purchaseDate = "Ongoing sale !",
        vendor = EXPECTED_VENDOR,
        staticMap = EXPECTED_STATIC_MAP,
        visibility = true,
    )

    private fun getDetailsViewStateWithInterests() = DetailsViewState(
        propertyId = EXPECTED_CURRENT_PROPERTY_ID,
        photos = listOf(
            DetailsPhotoViewState(
                EXPECTED_PHOTO_URI,
                EXPECTED_PHOTO_DESCRIPTION

            )
        ),
        description = EXPECTED_DESCRIPTION,
        surface = EXPECTED_SURFACE + "m²",
        room = EXPECTED_ROOM,
        bathroom = EXPECTED_BATHROOM,
        bedroom = EXPECTED_BEDROOM,
        interest = EXPECTED_INTERESTS,
        address = EXPECTED_ADDRESS,
        apartment = EXPECTED_APARTMENT_NUMBER,
        city = EXPECTED_CITY,
        county = EXPECTED_COUNTY,
        zipcode = EXPECTED_ZIPCODE,
        country = EXPECTED_COUNTRY,
        startSale = EXPECTED_CREATION_DATE_FORMAT,
        purchaseDate = "Ongoing sale !",
        vendor = EXPECTED_VENDOR,
        staticMap = EXPECTED_STATIC_MAP,
        visibility = true,
    )

    private fun getSoldPropertyViewState() = DetailsViewState(
        propertyId = EXPECTED_CURRENT_PROPERTY_ID,
        photos = listOf(
            DetailsPhotoViewState(
                EXPECTED_PHOTO_URI,
                EXPECTED_PHOTO_DESCRIPTION

            )
        ),
        description = EXPECTED_DESCRIPTION,
        surface = EXPECTED_SURFACE + "m²",
        room = EXPECTED_ROOM,
        bathroom = EXPECTED_BATHROOM,
        bedroom = EXPECTED_BEDROOM,
        interest = null,
        address = EXPECTED_ADDRESS,
        apartment = EXPECTED_APARTMENT_NUMBER,
        city = EXPECTED_CITY,
        county = EXPECTED_COUNTY,
        zipcode = EXPECTED_ZIPCODE,
        country = EXPECTED_COUNTRY,
        startSale = EXPECTED_CREATION_DATE_FORMAT,
        purchaseDate = EXPECTED_PURCHASE_AVAILABLE,
        vendor = EXPECTED_VENDOR,
        staticMap = EXPECTED_STATIC_MAP,
        visibility = true,
    )

    private fun getNoSurfacePropertyViewState() = DetailsViewState(
        propertyId = EXPECTED_CURRENT_PROPERTY_ID,
        photos = listOf(
            DetailsPhotoViewState(
                EXPECTED_PHOTO_URI,
                EXPECTED_PHOTO_DESCRIPTION

            )
        ),
        description = EXPECTED_DESCRIPTION,
        surface = "",
        room = EXPECTED_ROOM,
        bathroom = EXPECTED_BATHROOM,
        bedroom = EXPECTED_BEDROOM,
        interest = null,
        address = EXPECTED_ADDRESS,
        apartment = EXPECTED_APARTMENT_NUMBER,
        city = EXPECTED_CITY,
        county = EXPECTED_COUNTY,
        zipcode = EXPECTED_ZIPCODE,
        country = EXPECTED_COUNTRY,
        startSale = EXPECTED_CREATION_DATE_FORMAT,
        purchaseDate = "Ongoing sale !",
        vendor = EXPECTED_VENDOR,
        staticMap = EXPECTED_STATIC_MAP,
        visibility = true,
    )

    private fun getMinimumPropertyViewState() = DetailsViewState(
        propertyId = EXPECTED_CURRENT_PROPERTY_ID,
        photos = listOf(
            DetailsPhotoViewState(
                EXPECTED_PHOTO_URI,
                EXPECTED_PHOTO_DESCRIPTION

            )
        ),
        description = "",
        surface = "",
        room = "",
        bathroom = "",
        bedroom = "",
        interest = null,
        address = EXPECTED_ADDRESS,
        apartment = "",
        city = EXPECTED_CITY,
        county = "",
        zipcode = EXPECTED_ZIPCODE,
        country = "",
        startSale = EXPECTED_CREATION_DATE_FORMAT,
        purchaseDate = "Ongoing sale !",
        vendor = EXPECTED_VENDOR,
        staticMap = EXPECTED_STATIC_MAP,
        visibility = true,
    )

    private fun getPropertyWithTwoPhotosViewState() = DetailsViewState(
        propertyId = EXPECTED_CURRENT_PROPERTY_ID,
        photos = listOf(
            DetailsPhotoViewState(
                EXPECTED_PHOTO_URI,
                EXPECTED_PHOTO_DESCRIPTION

            ),
            DetailsPhotoViewState(
                EXPECTED_PHOTO_URI,
                EXPECTED_PHOTO_DESCRIPTION

            ),
        ),
        description = "",
        surface = "",
        room = "",
        bathroom = "",
        bedroom = "",
        interest = null,
        address = EXPECTED_ADDRESS,
        apartment = "",
        city = EXPECTED_CITY,
        county = "",
        zipcode = EXPECTED_ZIPCODE,
        country = "",
        startSale = EXPECTED_CREATION_DATE_FORMAT,
        purchaseDate = "Ongoing sale !",
        vendor = EXPECTED_VENDOR,
        staticMap = EXPECTED_STATIC_MAP,
        visibility = true,
    )
    // endregion OUT
}