package com.kardabel.realestatemanager.ui.main

import android.Manifest.permission
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.repository.*
import com.kardabel.realestatemanager.usecase.MergePropertiesDataBaseUseCase
import com.kardabel.realestatemanager.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val context: Application,
    private val priceConverterRepository: PriceConverterRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val currentPropertySaleStatus: CurrentPropertySaleStatus,
    private val currentSearchRepository: CurrentSearchRepository,
    private val createPhotoRepository: CreatePhotoRepository,
    private val interestRepository: InterestRepository,
    private val mergePropertiesDataBaseUseCase: MergePropertiesDataBaseUseCase,
    private val applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {

    private var isTablet: Boolean = false

    val actionSingleLiveEventMainActivity: SingleLiveEvent<MainActivityViewAction> = SingleLiveEvent()
    val screenPositionSingleLiveEvent: SingleLiveEvent<ScreenPositionViewAction> =
        SingleLiveEvent()

    // Check permission (mvvm friendly)
    fun checkPermission(activity: Activity) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {}
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                permission.ACCESS_FINE_LOCATION
            ) -> {
                actionSingleLiveEventMainActivity.setValue(MainActivityViewAction.PERMISSION_DENIED)
            }
            else -> {
                actionSingleLiveEventMainActivity.setValue(MainActivityViewAction.PERMISSION_ASKED)
            }
        }
    }

    // On resume, the activity send a boolean to know which position the device is
    fun onConfigurationChanged(isTablet: Boolean) {
        this.isTablet = isTablet
    }

    // Update actionView with tablet boolean value
    init {
        screenPositionSingleLiveEvent.addSource(currentPropertyIdRepository.currentPropertyIdLiveData) {
            if (!isTablet) {
                screenPositionSingleLiveEvent.setValue(ScreenPositionViewAction.IsPortraitMode)
            }
        }
    }

    fun convertPrice() {
        priceConverterRepository.convertPricePlease()
    }

    // Check currentPropertyIdRepository to know if a property is selected
    // and currentPropertySaleStatus to check if sold
    fun checkPropertyStatus() {
        if(currentPropertyIdRepository.isProperty && currentPropertySaleStatus.isOnSale){
            actionSingleLiveEventMainActivity.setValue(MainActivityViewAction.GO_TO_EDIT_PROPERTY)

        }else if (currentPropertyIdRepository.isProperty && !currentPropertySaleStatus.isOnSale){
            actionSingleLiveEventMainActivity.setValue(MainActivityViewAction.PROPERTY_SOLD)

        }else{

            actionSingleLiveEventMainActivity.setValue(MainActivityViewAction.NO_PROPERTY_SELECTED)
        }
    }

    // Photo repository flush
    fun emptyCreatedPhotoRepository() {
        createPhotoRepository.emptyCreatePhotoList()
    }

    // Interest repository flush
    fun emptyInterestRepository() {
        interestRepository.emptyInterestList()
    }

    // Launch a synchronisation between room and firestore
    fun synchroniseWithFirestore() {
        viewModelScope.launch(applicationDispatchers.ioDispatcher) {
            mergePropertiesDataBaseUseCase.synchronisePropertiesDataBases()
        }
    }

    fun resetSearch() {
        currentSearchRepository.resetSearchParams()

    }
}