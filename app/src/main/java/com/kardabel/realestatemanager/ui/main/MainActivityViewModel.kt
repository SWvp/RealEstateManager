package com.kardabel.realestatemanager.ui.main

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.kardabel.realestatemanager.repository.LocationRepository
import com.kardabel.realestatemanager.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val actionSingleLiveEvent: SingleLiveEvent<PermissionViewAction> = SingleLiveEvent()

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

    private fun permissionGranted() {
        //locationRepository.StartLocationRequest()
    }
}