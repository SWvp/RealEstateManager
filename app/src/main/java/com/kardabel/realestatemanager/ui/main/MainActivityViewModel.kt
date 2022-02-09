package com.kardabel.realestatemanager.ui.main

import android.Manifest.permission
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.kardabel.realestatemanager.repository.CurrentPropertyIdRepository
import com.kardabel.realestatemanager.repository.LocationRepository
import com.kardabel.realestatemanager.repository.MasterDetailsStatusRepository
import com.kardabel.realestatemanager.utils.NavigateViewAction
import com.kardabel.realestatemanager.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val context: Application,
    private val masterDetailsStatusRepository: MasterDetailsStatusRepository,
    private val locationRepository: LocationRepository,
    currentPropertyIdRepository: CurrentPropertyIdRepository,
) : ViewModel() {

    private var isTablet: Boolean = false

    val actionSingleLiveEvent: SingleLiveEvent<PermissionViewAction> = SingleLiveEvent()
    val navigationSingleLiveEvent : SingleLiveEvent<NavigateViewAction> = SingleLiveEvent()

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
        navigationSingleLiveEvent.addSource(currentPropertyIdRepository.currentPropertyIdLiveData) {
            if (!isTablet) {
                navigationSingleLiveEvent.setValue(NavigateViewAction.IsLandscapeMode)
            }
        }
    }

    fun onConfigurationChanged(isTablet: Boolean) {
        this.isTablet = isTablet
    }

    fun masterDetailsStatus(value: Boolean){
        masterDetailsStatusRepository.setMasterDetailsStatus(value)
    }
}