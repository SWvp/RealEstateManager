package com.kardabel.realestatemanager.ui.create

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kardabel.realestatemanager.*
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.repository.*
import com.kardabel.realestatemanager.usecase.GetCurrentUserIdUseCase
import com.kardabel.realestatemanager.usecase.GetCurrentUserNameUseCase
import com.kardabel.realestatemanager.utils.CreateActivityViewAction
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
class CreatePropertyViewModelTest {

    companion object {
        private const val ADDRESS = "address"
        private const val APARTMENT_NUMBER = "apartment_number"
        private const val COUNTY = "county"
        private const val CITY = "city"
        private const val ZIPCODE = "zipcode"
        private const val COUNTRY = "country"
        private const val PROPERTY_DESCRIPTION = "property_description"
        private const val TYPE = "type"
        private const val PRICE = "price"
        private const val SURFACE = "surface"
        private const val ROOM = "room"
        private const val BEDROOM = "bedroom"
        private const val BATHROOM = "bathroom"

        private const val EXPECTED_CURRENT_PROPERTY_ID = 42L
        private const val EXPECTED_CREATION_DATE = "first_createLocalDateTime"
        private const val EXPECTED_PHOTO_URI = "first_photoUri"
        private const val EXPECTED_PHOTO_DESCRIPTION = "first_photo_description"
        private const val EXPECTED_TIMESTAMP = "first_timestamp"
        private const val EXPECTED_PHOTO_ID = 666

        private const val EXPECTED_USER_ID = "userId"
        private const val EXPECTED_USER_NAME = "userName"
    }


    @get: Rule
    val testCoroutineRule = TestCoroutineRule()

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val propertiesRepository: PropertiesRepository = mockk()

    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase = mockk()

    private val getCurrentUserNameUseCase: GetCurrentUserNameUseCase = mockk()

    private val createPhotoRepository: CreatePhotoRepository = mockk()

    private val interestRepository: InterestRepository = mockk()

    private val sendPropertyToFirestoreRepository: SendPropertyToFirestoreRepository = mockk()

    private val sendPhotoToCloudStorageRepository: SendPhotoToCloudStorageRepository = mockk()

    private val clock = Clock.fixed(
        LocalDateTime
            .of(
                LocalDate.of(2021, 10, 20),
                LocalTime.of(10, 0)
            )
            .toInstant(ZoneOffset.UTC),
        ZoneOffset.UTC
    )

    private val context: Application = mockk()

    @Before
    fun setUp() {

        every { createPhotoRepository.getAddedPhotoLiveData() } returns MutableLiveData<List<PhotoEntity>>().apply {
            getPhotos()
        }

        every { interestRepository.getInterestLiveData()} returns MutableLiveData<List<String>>().apply {
            getInterest()
        }

        every { getCurrentUserIdUseCase.invoke()} returns EXPECTED_USER_ID
        every { getCurrentUserNameUseCase.invoke()} returns EXPECTED_USER_NAME

    }

 //  @Test
 //  fun `create property with success`() = runTest {
 //      // Given
 //      getViewModel().createProperty(
 //          address = ADDRESS,
 //          apartmentNumber = APARTMENT_NUMBER,
 //          county = COUNTY,
 //          city = CITY,
 //          zipcode = ZIPCODE,
 //          country = COUNTRY,
 //          propertyDescription = PROPERTY_DESCRIPTION,
 //          type = TYPE,
 //          price = PRICE,
 //          surface = SURFACE,
 //          room = ROOM,
 //          bedroom = BEDROOM,
 //          bathroom = BATHROOM,
 //      )

 //      // When
 //      getViewModel().actionSingleLiveEvent.observeForTesting { result ->
 //          // Then
 //          assertEquals(
 //              CreateActivityViewAction.FINISH_ACTIVITY,
 //              result
 //          )
 //      }

 //  }

 // @Test
 // fun `create interest unsuccessfully`() {
 //     // Given
 //     getViewModel().addInterest("pq")


 //     // When
 //     //getOrAwaitValue(getViewModel().actionSingleLiveEvent)
 //     val result = getViewModel().actionSingleLiveEvent.getValueForTesting()


 //     //getViewModel().actionSingleLiveEvent.observeForTesting { result ->
 //         // Then
 //         assertEquals(
 //             CreateActivityViewAction.INTEREST_FIELD_ERROR,
 //             result
 //         )
 //    // }

 // }


    private fun getViewModel() = CreatePropertyViewModel(
        propertiesRepository = propertiesRepository,
        getCurrentUserIdUseCase = getCurrentUserIdUseCase,
        getCurrentUserNameUseCase = getCurrentUserNameUseCase,
        createPhotoRepository = createPhotoRepository,
        interestRepository = interestRepository,
        sendPropertyToFirestoreRepository = sendPropertyToFirestoreRepository,
        sendPhotoToCloudStorageRepository = sendPhotoToCloudStorageRepository,
        clock = clock,
        context = context,
        applicationDispatchers = getApplicationDispatchersTest(testCoroutineRule)
    )

    // region IN
    private fun getPhotos(): List<PhotoEntity> {
        val photoList = mutableListOf<PhotoEntity>()
        photoList.add(
            PhotoEntity(
                photoUri = EXPECTED_PHOTO_URI,
                photoDescription = EXPECTED_PHOTO_DESCRIPTION,
                propertyOwnerId = EXPECTED_CURRENT_PROPERTY_ID,
                photoTimestamp = EXPECTED_TIMESTAMP,
                photoCreationDate = EXPECTED_CREATION_DATE,
                photoId = EXPECTED_PHOTO_ID,
            )
        )
        photoList.add(
            PhotoEntity(
                photoUri = EXPECTED_PHOTO_URI + 1,
                photoDescription = EXPECTED_PHOTO_DESCRIPTION + 1,
                propertyOwnerId = EXPECTED_CURRENT_PROPERTY_ID + 1,
                photoTimestamp = EXPECTED_TIMESTAMP + 1,
                photoCreationDate = EXPECTED_CREATION_DATE + 1,
                photoId = EXPECTED_PHOTO_ID + 1,
            )
        )
        return photoList

    }

    private fun getInterest(): List<String> {
        return listOf("restaurant", "shop",)

    }
}