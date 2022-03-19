package com.kardabel.realestatemanager.ui.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
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

@ExperimentalCoroutinesApi
class DetailsFragmentViewModelTest {

    companion object {
        private const val EXPECTED_CURRENT_PROPERTY_ID = 42L
        private const val EXPECTED_TIMESTAMP = "1254845878"
        private const val EXPECTED_CREATION_DATE = "987654"
        private const val EXPECTED_PRICE = "400000"
        private const val EXPECTED_SURFACE = "85"
        private const val EXPECTED_ROOM = "4"
        private const val EXPECTED_BEDROOM = "3"
        private const val EXPECTED_BATHROOM = "1"
        private const val EXPECTED_IS_SOLD = "On Sale !"
        private const val EXPECTED_DESCRIPTION = "propertyDescription"
        private val EXPECTED_INTERESTS = null

        private const val EXPECTED_PHOTO_ID = 133
    }

    @get: Rule
    val testCoroutineRule = TestCoroutineRule()

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val currentPropertyIdRepository: CurrentPropertyIdRepository = mockk()

    private val propertiesRepository: PropertiesRepository = mockk()

    @Before
    fun setUp() {

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
        getViewModel().detailsLiveData.observeForTesting {
            // Then
            assertEquals(
                getDefaultDetailsViewState(),
                it
            )
        }
    }

    @Test
    fun `property with interests`() = runTest {
        // When
        every { propertiesRepository.getPropertyById(EXPECTED_CURRENT_PROPERTY_ID) } returns flowOf(
            getDefaultPropertyWithInterests()
        )
        getViewModel().detailsLiveData.observeForTesting {
            // Then
            assertEquals(
                getDetailsViewStateWithInterests(),
                it
            )
        }
    }


    private fun getViewModel() = DetailsFragmentViewModel(
        currentPropertyIdRepository = currentPropertyIdRepository,
        propertiesRepository = propertiesRepository,
        applicationDispatchers = getApplicationDispatchersTest(testCoroutineRule)
    )

    // region IN
    private fun getDefaultPropertyWithPhoto() = PropertyWithPhoto(
        propertyEntity = PropertyEntity(
            address = "address",
            apartmentNumber = "apartmentNumber",
            city = "city",
            zipcode = "zipcode",
            county = "county",
            country = "country",
            propertyDescription = EXPECTED_DESCRIPTION,
            type = "type",
            price = EXPECTED_PRICE,
            surface = EXPECTED_SURFACE,
            room = EXPECTED_ROOM,
            bedroom = EXPECTED_BEDROOM,
            bathroom = EXPECTED_BATHROOM,
            uid = "uid",
            vendor = "vendor",
            staticMap = "staticMap",
            propertyCreationDate = "createLocalDateTime",
            creationDateToFormat = "createDateToFormat",
            saleStatus = EXPECTED_IS_SOLD,
            purchaseDate = null,
            interest = EXPECTED_INTERESTS,
            propertyId = EXPECTED_CURRENT_PROPERTY_ID
        ),
        photo = listOf(
            PhotoEntity(
                photoUri = "photoUri",
                photoDescription = "photoDescription",
                propertyOwnerId = EXPECTED_CURRENT_PROPERTY_ID,
                photoTimestamp = EXPECTED_TIMESTAMP,
                photoCreationDate = EXPECTED_CREATION_DATE,
                photoId = EXPECTED_PHOTO_ID,
            )
        )
    )

    private fun getDefaultPropertyWithInterests() = PropertyWithPhoto(
        propertyEntity = PropertyEntity(
            address = "address",
            apartmentNumber = "apartmentNumber",
            city = "city",
            zipcode = "zipcode",
            county = "county",
            country = "country",
            propertyDescription = EXPECTED_DESCRIPTION,
            type = "type",
            price = EXPECTED_PRICE,
            surface = EXPECTED_SURFACE,
            room = EXPECTED_ROOM,
            bedroom = EXPECTED_BEDROOM,
            bathroom = EXPECTED_BATHROOM,
            uid = "uid",
            vendor = "vendor",
            staticMap = "staticMap",
            propertyCreationDate = "createLocalDateTime",
            creationDateToFormat = "createDateToFormat",
            saleStatus = EXPECTED_IS_SOLD,
            purchaseDate = null,
            interest = listOf("first interest", "second interest"),
            propertyId = EXPECTED_CURRENT_PROPERTY_ID
        ),
        photo = listOf(
            PhotoEntity(
                photoUri = "photoUri",
                photoDescription = "photoDescription",
                propertyOwnerId = EXPECTED_CURRENT_PROPERTY_ID,
                photoTimestamp = EXPECTED_TIMESTAMP,
                photoCreationDate = EXPECTED_CREATION_DATE,
                photoId = EXPECTED_PHOTO_ID,
            )
        )
    )
    // endregion IN

    // region OUT
    private fun getDefaultDetailsViewState() = DetailsViewState(
        propertyId = EXPECTED_CURRENT_PROPERTY_ID,
        photos = listOf(
            DetailsPhotoViewState(
                "photoUri",
                "photoDescription"

            )
        ),
        description = EXPECTED_DESCRIPTION,
        surface = EXPECTED_SURFACE + "m²",
        room = EXPECTED_ROOM,
        bathroom = EXPECTED_BATHROOM,
        bedroom = EXPECTED_BEDROOM,
        interest = EXPECTED_INTERESTS,
        address = "address",
        apartment = "apartmentNumber",
        city = "city",
        county = "county",
        zipcode = "zipcode",
        country = "country",
        startSale = "createDateToFormat",
        purchaseDate = "Ongoing sale !",
        vendor = "vendor",
        staticMap = "staticMap",
        visibility = true,
    )

    private fun getDetailsViewStateWithInterests() = DetailsViewState(
        propertyId = EXPECTED_CURRENT_PROPERTY_ID,
        photos = listOf(
            DetailsPhotoViewState(
                "photoUri",
                "photoDescription"

            )
        ),
        description = "propertyDescription",
        surface = EXPECTED_SURFACE + "m²",
        room = EXPECTED_ROOM,
        bathroom = EXPECTED_BATHROOM,
        bedroom = EXPECTED_BEDROOM,
        interest = listOf("first interest", "second interest"),
        address = "address",
        apartment = "apartmentNumber",
        city = "city",
        county = "county",
        zipcode = "zipcode",
        country = "country",
        startSale = "createDateToFormat",
        purchaseDate = "Ongoing sale !",
        vendor = "vendor",
        staticMap = "staticMap",
        visibility = true,
    )
    // endregion OUT
}