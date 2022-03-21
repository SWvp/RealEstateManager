package com.kardabel.realestatemanager.ui.properties

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.TestCoroutineRule
import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.getApplicationDispatchersTest
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.model.PropertyWithPhoto
import com.kardabel.realestatemanager.model.SearchParams
import com.kardabel.realestatemanager.observeForTesting
import com.kardabel.realestatemanager.repository.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

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
        private const val EXPECTED_PRICE_DOLLARS = "1000000"
        private const val EXPECTED_PRICE_EUROS = "812000"
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
        private val EXPECTED_FIRST_INTERESTS = listOf("first interest")
        private val EXPECTED_SECOND_INTERESTS = listOf("second interest")
        private val EXPECTED_THIRD_INTERESTS = listOf("third interest")
        private const val EXPECTED_CURRENT_PROPERTY_ID = 42L

        private const val EXPECTED_PHOTO_URI = "first_photoUri"
        private const val EXPECTED_PHOTO_DESCRIPTION = "first_photo_description"
        private const val EXPECTED_TIMESTAMP = "first_timestamp"
        private const val EXPECTED_PHOTO_ID = 666

        private const val EXPECTED_DEFAULT_COLOR = -1

    }

    @get: Rule
    val testCoroutineRule = TestCoroutineRule()

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val propertiesRepository: PropertiesRepository = mockk()

    private val currentPropertyIdRepository: CurrentPropertyIdRepository = mockk()

    private val currentPropertySaleStatus: CurrentPropertySaleStatus = mockk()

    private val priceConverterRepository: PriceConverterRepository = mockk()

    private val currentSearchRepository: CurrentSearchRepository = mockk()

    private val propertiesDao: PropertiesDao = mockk()

    private val context = Mockito.mock(Application::class.java)


    @Before
    fun setUp() {


        // STRINGS RETURNS
        Mockito.doReturn("Price N/C").`when`(context).getString(R.string.price_nc)
        Mockito.doReturn("$").`when`(context).getString(R.string.dollar)
        Mockito.doReturn("€").`when`(context).getString(R.string.euro)

        // REPOSITORY RETURNS
        every { propertiesRepository.getPropertiesWithPhotosFlow() } returns flowOf(
            getDefaultPropertiesWithPhoto()
        )

        every { currentPropertyIdRepository.currentPropertyIdLiveData } returns MutableLiveData<Long>().apply {
            value = EXPECTED_CURRENT_PROPERTY_ID
        }

        every { priceConverterRepository.getCurrentCurrencyLiveData } returns MutableLiveData<Boolean>().apply {
            value = null
        }

        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            null
        )

    }

    @Test
    fun `nominal case`() = runTest {
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getDefaultPropertiesViewState(),
                result
            )
        }
    }

    @Test
    fun `photo is not available yet on first property`() = runTest {
        // Given
        every { propertiesRepository.getPropertiesWithPhotosFlow() } returns flowOf(
            getPropertiesWithoutPhoto()
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getPropertiesWithoutPhotosViewState(),
                result
            )
        }
    }

    @Test
    fun `filter properties by price match one property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = IntRange(0, 1000000),
                surfaceRange = null,
                roomRange = null,
                photo = null,
                propertyType = null,
                interest = null,
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getFilteredPropertiesViewState(),
                result
            )
        }
    }

    @Test
    fun `filter properties by price match no property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = IntRange(2000000, 2000001),
                surfaceRange = null,
                roomRange = null,
                photo = null,
                propertyType = null,
                interest = null,
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                ArrayList<PropertyViewState>(),
                result
            )
        }
    }

    @Test
    fun `filter properties by surface match one property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = IntRange(0, 100),
                roomRange = null,
                photo = null,
                propertyType = null,
                interest = null,
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getFilteredPropertiesViewState(),
                result
            )
        }
    }

    @Test
    fun `filter properties by surface match no property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = IntRange(200, 201),
                roomRange = null,
                photo = null,
                propertyType = null,
                interest = null,
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                ArrayList<PropertyViewState>(),
                result
            )
        }
    }

    @Test
    fun `filter properties by room match one property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = null,
                roomRange = IntRange(0, 5),
                photo = null,
                propertyType = null,
                interest = null,
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getFilteredPropertiesViewState(),
                result
            )
        }
    }

    @Test
    fun `filter properties by room match no property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = null,
                roomRange = IntRange(10, 11),
                photo = null,
                propertyType = null,
                interest = null,
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                ArrayList<PropertyViewState>(),
                result
            )
        }
    }

    @Test
    fun `filter properties by photos match one property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = null,
                roomRange = null,
                photo = 2,
                propertyType = null,
                interest = null,
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getFilteredPropertiesViewState(),
                result
            )
        }
    }

    @Test
    fun `filter properties by photos match no property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = null,
                roomRange = null,
                photo = 10,
                propertyType = null,
                interest = null,
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                ArrayList<PropertyViewState>(),
                result
            )
        }
    }

    @Test
    fun `filter properties by property type match one property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = null,
                roomRange = null,
                photo = null,
                propertyType = EXPECTED_TYPE,
                interest = null,
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getFilteredPropertiesViewState(),
                result
            )
        }
    }

    @Test
    fun `filter properties by property type match no property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = null,
                roomRange = null,
                photo = null,
                propertyType = "fourth_type",
                interest = null,
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                ArrayList<PropertyViewState>(),
                result
            )
        }
    }

    @Test
    fun `filter properties by interest with one interest match one property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = null,
                roomRange = null,
                photo = null,
                propertyType = null,
                interest = EXPECTED_FIRST_INTERESTS,
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getFilteredPropertiesViewState(),
                result
            )
        }
    }

    @Test
    fun `filter properties by interest with two interests match two property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = null,
                roomRange = null,
                photo = null,
                propertyType = null,
                interest = listOf("first interest", "second interest"),
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getPropertiesInterestsFilterViewState(),
                result
            )
        }
    }

    @Test
    fun `filter properties by interest match no property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = null,
                roomRange = null,
                photo = null,
                propertyType = null,
                interest = listOf("fourth interest"),
                county = null,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                ArrayList<PropertyViewState>(),
                result
            )
        }
    }

    @Test
    fun `filter properties by county match one property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = null,
                roomRange = null,
                photo = null,
                propertyType = null,
                interest = null,
                county = EXPECTED_COUNTY,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getFilteredPropertiesViewState(),
                result
            )
        }
    }

    @Test
    fun `filter properties by county match no property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = null,
                surfaceRange = null,
                roomRange = null,
                photo = null,
                propertyType = null,
                interest = null,
                county = "awesome county",

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                ArrayList<PropertyViewState>(),
                result
            )
        }
    }

    @Test
    fun `filter properties by all parameters match one property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = IntRange(0, 1000000),
                surfaceRange = IntRange(0, 100),
                roomRange = IntRange(0, 5),
                photo = 2,
                propertyType = EXPECTED_TYPE,
                interest = EXPECTED_FIRST_INTERESTS,
                county = EXPECTED_COUNTY,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getFilteredPropertiesViewState(),
                result
            )
        }
    }

    @Test
    fun `filter properties by all parameters with one wrong match no property`() = runTest {
        // Given
        every { currentSearchRepository.getSearchParamsParamsFlow() } returns flowOf(
            SearchParams(
                priceRange = IntRange(0, 1000000),
                surfaceRange = IntRange(0, 100),
                roomRange = IntRange(100, 101),
                photo = 2,
                propertyType = EXPECTED_TYPE,
                interest = EXPECTED_FIRST_INTERESTS,
                county = EXPECTED_COUNTY,

                )
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                ArrayList<PropertyViewState>(),
                result
            )
        }
    }

    @Test
    fun `when price is not indicated, display nc`() = runTest {
        // Given
        every { propertiesRepository.getPropertiesWithPhotosFlow() } returns flowOf(
            getPropertiesWithoutPrice()
        )
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getPropertiesWithoutPriceViewState(),
                result
            )
        }
    }

    @Test
    fun `when click on currency item, display euro price`() = runTest {
        // Given
        every { priceConverterRepository.getCurrentCurrencyLiveData } returns MutableLiveData<Boolean>().apply {
            value = false
        }
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getPropertiesEuroPriceViewState(),
                result
            )
        }
    }

    @Test
    fun `when click on currency item, display dollar price`() = runTest {
        // Given
        every { priceConverterRepository.getCurrentCurrencyLiveData } returns MutableLiveData<Boolean>().apply {
            value = true
        }
        // When
        getViewModel().getPropertiesLiveData.observeForTesting { result ->
            // Then
            Assert.assertEquals(
                getDefaultPropertiesViewState(),
                result
            )
        }
    }

    private fun getViewModel() = PropertiesViewModel(
        currentPropertyIdRepository = currentPropertyIdRepository,
        currentPropertySaleStatus = currentPropertySaleStatus,
        propertiesDao = propertiesDao,
        propertiesRepository = propertiesRepository,
        priceConverterRepository = priceConverterRepository,
        currentSearchRepository = currentSearchRepository,
        context = context,
        applicationDispatchers = getApplicationDispatchersTest(testCoroutineRule)
    )

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
                    interest = EXPECTED_FIRST_INTERESTS,
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
                    price = EXPECTED_PRICE_DOLLARS + 1,
                    surface = EXPECTED_SURFACE + 1,
                    room = EXPECTED_ROOM + 1,
                    bedroom = EXPECTED_BEDROOM + 1,
                    bathroom = EXPECTED_BATHROOM + 1,
                    uid = EXPECTED_UID + 1,
                    vendor = EXPECTED_VENDOR + 1,
                    staticMap = EXPECTED_STATIC_MAP + 1,
                    propertyCreationDate = EXPECTED_CREATION_DATE + 1,
                    creationDateToFormat = EXPECTED_CREATION_DATE_FORMAT + 1,
                    saleStatus = EXPECTED_SALE_STATUS,
                    purchaseDate = EXPECTED_PURCHASE_DATE,
                    interest = EXPECTED_SECOND_INTERESTS,
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
                    price = EXPECTED_PRICE_DOLLARS + 2,
                    surface = EXPECTED_SURFACE + 2,
                    room = EXPECTED_ROOM + 2,
                    bedroom = EXPECTED_BEDROOM + 2,
                    bathroom = EXPECTED_BATHROOM + 2,
                    uid = EXPECTED_UID + 2,
                    vendor = EXPECTED_VENDOR + 2,
                    staticMap = EXPECTED_STATIC_MAP + 2,
                    propertyCreationDate = EXPECTED_CREATION_DATE + 2,
                    creationDateToFormat = EXPECTED_CREATION_DATE_FORMAT + 2,
                    saleStatus = EXPECTED_SALE_STATUS,
                    purchaseDate = EXPECTED_PURCHASE_DATE,
                    interest = EXPECTED_THIRD_INTERESTS,
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

    private fun getPropertiesWithoutPhoto(): List<PropertyWithPhoto> {
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
                    interest = EXPECTED_FIRST_INTERESTS,
                    propertyId = EXPECTED_CURRENT_PROPERTY_ID
                ),
                photo = ArrayList<PhotoEntity>()
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
                    price = EXPECTED_PRICE_DOLLARS + 1,
                    surface = EXPECTED_SURFACE + 1,
                    room = EXPECTED_ROOM + 1,
                    bedroom = EXPECTED_BEDROOM + 1,
                    bathroom = EXPECTED_BATHROOM + 1,
                    uid = EXPECTED_UID + 1,
                    vendor = EXPECTED_VENDOR + 1,
                    staticMap = EXPECTED_STATIC_MAP + 1,
                    propertyCreationDate = EXPECTED_CREATION_DATE + 1,
                    creationDateToFormat = EXPECTED_CREATION_DATE_FORMAT + 1,
                    saleStatus = EXPECTED_SALE_STATUS,
                    purchaseDate = EXPECTED_PURCHASE_DATE,
                    interest = EXPECTED_SECOND_INTERESTS,
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
                    price = EXPECTED_PRICE_DOLLARS + 2,
                    surface = EXPECTED_SURFACE + 2,
                    room = EXPECTED_ROOM + 2,
                    bedroom = EXPECTED_BEDROOM + 2,
                    bathroom = EXPECTED_BATHROOM + 2,
                    uid = EXPECTED_UID + 2,
                    vendor = EXPECTED_VENDOR + 2,
                    staticMap = EXPECTED_STATIC_MAP + 2,
                    propertyCreationDate = EXPECTED_CREATION_DATE + 2,
                    creationDateToFormat = EXPECTED_CREATION_DATE_FORMAT + 2,
                    saleStatus = EXPECTED_SALE_STATUS,
                    purchaseDate = EXPECTED_PURCHASE_DATE,
                    interest = EXPECTED_THIRD_INTERESTS,
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

    private fun getPropertiesWithoutPrice(): List<PropertyWithPhoto> {
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
                    price = "",
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
                    interest = EXPECTED_FIRST_INTERESTS,
                    propertyId = EXPECTED_CURRENT_PROPERTY_ID
                ),
                photo = ArrayList<PhotoEntity>()
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
                    price = EXPECTED_PRICE_DOLLARS + 1,
                    surface = EXPECTED_SURFACE + 1,
                    room = EXPECTED_ROOM + 1,
                    bedroom = EXPECTED_BEDROOM + 1,
                    bathroom = EXPECTED_BATHROOM + 1,
                    uid = EXPECTED_UID + 1,
                    vendor = EXPECTED_VENDOR + 1,
                    staticMap = EXPECTED_STATIC_MAP + 1,
                    propertyCreationDate = EXPECTED_CREATION_DATE + 1,
                    creationDateToFormat = EXPECTED_CREATION_DATE_FORMAT + 1,
                    saleStatus = EXPECTED_SALE_STATUS,
                    purchaseDate = EXPECTED_PURCHASE_DATE,
                    interest = EXPECTED_SECOND_INTERESTS,
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
                    price = EXPECTED_PRICE_DOLLARS + 2,
                    surface = EXPECTED_SURFACE + 2,
                    room = EXPECTED_ROOM + 2,
                    bedroom = EXPECTED_BEDROOM + 2,
                    bathroom = EXPECTED_BATHROOM + 2,
                    uid = EXPECTED_UID + 2,
                    vendor = EXPECTED_VENDOR + 2,
                    staticMap = EXPECTED_STATIC_MAP + 2,
                    propertyCreationDate = EXPECTED_CREATION_DATE + 2,
                    creationDateToFormat = EXPECTED_CREATION_DATE_FORMAT + 2,
                    saleStatus = EXPECTED_SALE_STATUS,
                    purchaseDate = EXPECTED_PURCHASE_DATE,
                    interest = EXPECTED_THIRD_INTERESTS,
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

    // endregion IN

    // region OUT

    private fun getDefaultPropertiesViewState(): List<PropertyViewState> {

        val propertiesList = mutableListOf<PropertyViewState>()

        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID,
                type = EXPECTED_TYPE,
                county = EXPECTED_COUNTY,
                price = "$$EXPECTED_PRICE_DOLLARS",
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR,
                photoUri = EXPECTED_PHOTO_URI,
            )
        )
        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID + 1,
                type = EXPECTED_TYPE + 1,
                county = EXPECTED_COUNTY + 1,
                price = "$$EXPECTED_PRICE_DOLLARS" + 1,
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR + 1,
                photoUri = EXPECTED_PHOTO_URI + 1,
            )
        )
        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID + 2,
                type = EXPECTED_TYPE + 2,
                county = EXPECTED_COUNTY + 2,
                price = "$$EXPECTED_PRICE_DOLLARS" + 2,
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR + 2,
                photoUri = EXPECTED_PHOTO_URI + 2,
            )
        )

        return propertiesList
    }

    private fun getPropertiesWithoutPhotosViewState(): List<PropertyViewState> {

        val propertiesList = mutableListOf<PropertyViewState>()

        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID,
                type = EXPECTED_TYPE,
                county = EXPECTED_COUNTY,
                price = "$$EXPECTED_PRICE_DOLLARS",
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR,
                photoUri = null,
            )
        )
        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID + 1,
                type = EXPECTED_TYPE + 1,
                county = EXPECTED_COUNTY + 1,
                price = "$$EXPECTED_PRICE_DOLLARS" + 1,
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR + 1,
                photoUri = EXPECTED_PHOTO_URI + 1,
            )
        )
        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID + 2,
                type = EXPECTED_TYPE + 2,
                county = EXPECTED_COUNTY + 2,
                price = "$$EXPECTED_PRICE_DOLLARS" + 2,
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR + 2,
                photoUri = EXPECTED_PHOTO_URI + 2,
            )
        )

        return propertiesList
    }

    private fun getPropertiesWithoutPriceViewState(): List<PropertyViewState> {

        val propertiesList = mutableListOf<PropertyViewState>()

        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID,
                type = EXPECTED_TYPE,
                county = EXPECTED_COUNTY,
                price = "Price N/C",
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR,
                photoUri = null,
            )
        )
        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID + 1,
                type = EXPECTED_TYPE + 1,
                county = EXPECTED_COUNTY + 1,
                price = "$$EXPECTED_PRICE_DOLLARS" + 1,
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR + 1,
                photoUri = EXPECTED_PHOTO_URI + 1,
            )
        )
        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID + 2,
                type = EXPECTED_TYPE + 2,
                county = EXPECTED_COUNTY + 2,
                price = "$$EXPECTED_PRICE_DOLLARS" + 2,
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR + 2,
                photoUri = EXPECTED_PHOTO_URI + 2,
            )
        )

        return propertiesList
    }

    private fun getFilteredPropertiesViewState(): List<PropertyViewState> {

        val propertiesList = mutableListOf<PropertyViewState>()

        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID,
                type = EXPECTED_TYPE,
                county = EXPECTED_COUNTY,
                price = "$$EXPECTED_PRICE_DOLLARS",
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR,
                photoUri = EXPECTED_PHOTO_URI,
            )
        )

        return propertiesList
    }

    private fun getPropertiesInterestsFilterViewState(): List<PropertyViewState> {

        val propertiesList = mutableListOf<PropertyViewState>()

        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID,
                type = EXPECTED_TYPE,
                county = EXPECTED_COUNTY,
                price = "$$EXPECTED_PRICE_DOLLARS",
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR,
                photoUri = EXPECTED_PHOTO_URI,
            )
        )
        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID + 1,
                type = EXPECTED_TYPE + 1,
                county = EXPECTED_COUNTY + 1,
                price = "$$EXPECTED_PRICE_DOLLARS" + 1,
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR + 1,
                photoUri = EXPECTED_PHOTO_URI + 1,
            )
        )

        return propertiesList
    }

    private fun getPropertiesEuroPriceViewState(): List<PropertyViewState> {

        val propertiesList = mutableListOf<PropertyViewState>()

        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID,
                type = EXPECTED_TYPE,
                county = EXPECTED_COUNTY,
                price = "€$EXPECTED_PRICE_EUROS",
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR,
                photoUri = EXPECTED_PHOTO_URI,
            )
        )
        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID + 1,
                type = EXPECTED_TYPE + 1,
                county = EXPECTED_COUNTY + 1,
                price = "€$EXPECTED_PRICE_EUROS" + 1,
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR + 1,
                photoUri = EXPECTED_PHOTO_URI + 1,
            )
        )
        propertiesList.add(
            PropertyViewState(
                propertyId = EXPECTED_CURRENT_PROPERTY_ID + 2,
                type = EXPECTED_TYPE + 2,
                county = EXPECTED_COUNTY + 2,
                price = "€$EXPECTED_PRICE_EUROS" + 2,
                saleStatus = EXPECTED_SALE_STATUS,
                saleColor = EXPECTED_DEFAULT_COLOR,
                vendor = EXPECTED_VENDOR + 2,
                photoUri = EXPECTED_PHOTO_URI + 2,
            )
        )

        return propertiesList
    }

    // endregion OUT


}