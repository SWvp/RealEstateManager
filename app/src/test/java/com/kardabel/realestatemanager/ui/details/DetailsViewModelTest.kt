package com.kardabel.realestatemanager.ui.details

import android.graphics.Bitmap
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
class DetailsViewModelTest {

    companion object {
        private const val EXPECTED_CURRENT_PROPERTY_ID = 42L
        private const val EXPECTED_PRICE = 400_000
        private const val EXPECTED_SURFACE = 85
        private const val EXPECTED_ROOM = 4
        private const val EXPECTED_BEDROOM = 3
        private const val EXPECTED_BATHROOM = 1
        private const val EXPECTED_IS_SOLD = false
        private val EXPECTED_INTERESTS = emptyList<String>()

        private const val EXPECTED_PHOTO_ID = 666
        private val EXPECTED_PHOTO_BITMAP = mockk<Bitmap>()
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

    private fun getViewModel() = DetailsViewModel(
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
            propertyDescription = "propertyDescription",
            type = "type",
            price = EXPECTED_PRICE,
            surface = EXPECTED_SURFACE,
            room = EXPECTED_ROOM,
            bedroom = EXPECTED_BEDROOM,
            bathroom = EXPECTED_BATHROOM,
            uid = "uid",
            vendor = "vendor",
            staticMap = "staticMap",
            createLocalDateTime = "createLocalDateTime",
            createDateToFormat = "createDateToFormat",
            saleStatus = EXPECTED_IS_SOLD,
            purchaseDate = "purchaseDate",
            interest = EXPECTED_INTERESTS,
            propertyId = EXPECTED_CURRENT_PROPERTY_ID
        ),
        photo = listOf(
            PhotoEntity(
                photo = EXPECTED_PHOTO_BITMAP,
                photoDescription = "photoDescription",
                propertyOwnerId = EXPECTED_CURRENT_PROPERTY_ID,
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
                EXPECTED_PHOTO_BITMAP,
                "photoDescription"
            )
        ),
        description = "propertyDescription",
        surface = "85m²",
        room = EXPECTED_ROOM.toString(),
        bathroom = EXPECTED_BATHROOM.toString(),
        bedroom = EXPECTED_BEDROOM.toString(),
        interest = emptyList(),
        address = "address",
        apartment = "apartmentNumber",
        city = "city",
        county = "county",
        zipcode = "zipcode",
        country = "country",
        startSale = "createDateToFormat",
        vendor = "vendor",
        staticMap = "staticMap",
        visibility = true,
    )
    // endregion OUT
}