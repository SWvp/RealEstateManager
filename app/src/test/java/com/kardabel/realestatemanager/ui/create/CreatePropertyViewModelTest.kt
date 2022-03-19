package com.kardabel.realestatemanager.ui.create

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.kardabel.realestatemanager.TestCoroutineRule
import com.kardabel.realestatemanager.repository.SendPhotoToCloudStorageRepository
import com.kardabel.realestatemanager.repository.SendPropertyToFirestoreRepository
import com.kardabel.realestatemanager.repository.CreatePhotoRepository
import com.kardabel.realestatemanager.repository.InterestRepository
import com.kardabel.realestatemanager.repository.PropertiesRepository
import com.kardabel.realestatemanager.ui.details.DetailsFragmentViewModelTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import java.time.*

@ExperimentalCoroutinesApi
class CreatePropertyViewModelTest {

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

        private const val EXPECTED_PHOTO_ID = 666
    }


    @get: Rule
    val testCoroutineRule = TestCoroutineRule()

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val propertiesRepository: PropertiesRepository = mockk()

    //private val firebaseAuth: FirebaseAuth = mockk()

    private val createPhotoRepository: CreatePhotoRepository = mockk()

    private val interestRepository: InterestRepository = mockk()

    private val sendPropertyToFirestoreRepository: SendPropertyToFirestoreRepository = mockk()

    private val mSendPhotoToCloudStorageRepository: SendPhotoToCloudStorageRepository = mockk()

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



    }


}