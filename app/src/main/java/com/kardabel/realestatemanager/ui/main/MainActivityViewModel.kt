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
    private val createPhotoRepository: CreatePhotoRepository,
    private val interestRepository: InterestRepository,
    private val mergePropertiesDataBaseRepository: MergePropertiesDataBaseRepository,
    private val applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {

    private var isTablet: Boolean = false

    val mActionSingleLiveEventMainActivity: SingleLiveEvent<MainActivityViewAction> = SingleLiveEvent()
    val screenPositionSingleLiveEvent: SingleLiveEvent<ScreenPositionViewAction> =
        SingleLiveEvent()
    val startEditActivitySingleLiveEvent: SingleLiveEvent<NavigateToEditViewAction> =
        SingleLiveEvent()

    // CHECK PERMISSIONS WITH MVVM PATTERN
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
                mActionSingleLiveEventMainActivity.setValue(MainActivityViewAction.PERMISSION_DENIED)
            }
            else -> {
                mActionSingleLiveEventMainActivity.setValue(MainActivityViewAction.PERMISSION_ASKED)
            }
        }
    }

    init {
        screenPositionSingleLiveEvent.addSource(currentPropertyIdRepository.currentPropertyIdLiveData) {
            if (!isTablet) {
                screenPositionSingleLiveEvent.setValue(ScreenPositionViewAction.IsPortraitMode)
            }
        }
    }

    fun onConfigurationChanged(isTablet: Boolean) {
        this.isTablet = isTablet
    }

    fun convertPrice() {
        priceConverterRepository.convertPricePlease()
    }

    // Check currentPropertyIdRepository to know if a property is selected
    // and currentPropertySaleStatus to check if sold
    fun checkPropertyStatus() {
        if(currentPropertyIdRepository.isProperty && currentPropertySaleStatus.isOnSale){
            mActionSingleLiveEventMainActivity.setValue(MainActivityViewAction.GO_TO_EDIT_PROPERTY)

        }else if (currentPropertyIdRepository.isProperty && !currentPropertySaleStatus.isOnSale){
            mActionSingleLiveEventMainActivity.setValue(MainActivityViewAction.PROPERTY_SOLD)

        }else{

            mActionSingleLiveEventMainActivity.setValue(MainActivityViewAction.NO_PROPERTY_SELECTED)
        }
    }

    // Clear the createdPhotoRepoS for the next use
    fun emptyCreatedPhotoRepository() {
        createPhotoRepository.emptyCreatePhotoList()
    }

    fun emptyInterestRepository() {
        interestRepository.emptyInterestList()
    }

    fun synchroniseWithFirestore() {
        viewModelScope.launch(applicationDispatchers.ioDispatcher) {
            mergePropertiesDataBaseRepository.synchronisePropertiesDataBases()
        }
    }
}