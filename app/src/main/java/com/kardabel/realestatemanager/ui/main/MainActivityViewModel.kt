package com.kardabel.realestatemanager.ui.main

import android.Manifest.permission
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kardabel.realestatemanager.repository.CreatePhotoRepository
import com.kardabel.realestatemanager.repository.CurrentPropertyIdRepository
import com.kardabel.realestatemanager.repository.LocationRepository
import com.kardabel.realestatemanager.repository.PriceConverterRepository
import com.kardabel.realestatemanager.utils.NavigateToEditViewAction
import com.kardabel.realestatemanager.utils.ScreenPositionViewAction
import com.kardabel.realestatemanager.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val context: Application,
    private val priceConverterRepository: PriceConverterRepository,
    private val locationRepository: LocationRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
    private val createPhotoRepository: CreatePhotoRepository,
) : ViewModel() {

    private var isTablet: Boolean = false

    val actionSingleLiveEvent: SingleLiveEvent<PermissionViewAction> = SingleLiveEvent()
    val screenPositionSingleLiveEvent: SingleLiveEvent<ScreenPositionViewAction> =
        SingleLiveEvent()
    val startEditActivitySingleLiveEvent: SingleLiveEvent<NavigateToEditViewAction> =
        SingleLiveEvent()

    val getCurrentId: LiveData<Long> = currentPropertyIdRepository.currentPropertyIdLiveData

    // CHECK PERMISSIONS WITH MVVM PATTERN
    fun checkPermission(activity: Activity) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                //permissionGranted()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                permission.ACCESS_FINE_LOCATION
            ) -> {
                actionSingleLiveEvent.setValue(PermissionViewAction.PERMISSION_DENIED)
            }
            else -> {
                actionSingleLiveEvent.setValue(PermissionViewAction.PERMISSION_ASKED)
            }
        }
    }

    init {
        screenPositionSingleLiveEvent.addSource(currentPropertyIdRepository.currentPropertyIdLiveData) {
            if (!isTablet) {
                screenPositionSingleLiveEvent.setValue(ScreenPositionViewAction.IsLandscapeMode)
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
    // If not, edit will not be called
    fun propertyIdRepositoryStatus() {
        if(currentPropertyIdRepository.isProperty){
            startEditActivitySingleLiveEvent.setValue(NavigateToEditViewAction.GO_TO_EDIT_PROPERTY)

        }else{

            startEditActivitySingleLiveEvent.setValue(NavigateToEditViewAction.NO_PROPERTY_SELECTED)
        }
    }

    // Clear the createdPhotoRepoS for the next use
    fun emptyCreatedPhotoRepository() {
        createPhotoRepository.emptyCreatePhotoList()
    }
}