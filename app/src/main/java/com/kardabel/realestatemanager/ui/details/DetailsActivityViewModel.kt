package com.kardabel.realestatemanager.ui.details

import androidx.lifecycle.ViewModel
import com.kardabel.realestatemanager.repository.CreatePhotoRepository
import com.kardabel.realestatemanager.repository.CurrentPropertySaleStatus
import com.kardabel.realestatemanager.utils.ActivityViewAction
import com.kardabel.realestatemanager.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsActivityViewModel @Inject constructor(
    private val createPhotoRepository: CreatePhotoRepository,
    private val currentPropertySaleStatus: CurrentPropertySaleStatus,
) : ViewModel() {

    val actionSingleLiveEvent = SingleLiveEvent<ActivityViewAction>()


    // Clear the createdPhotoRepoS for the next use
    fun emptyCreatedPhotoRepository() {
        createPhotoRepository.emptyCreatePhotoList()
    }

    fun checkSaleStatusBeforeAccessToEditPropertyActivity() {

        if (currentPropertySaleStatus.isOnSale) {
            actionSingleLiveEvent.setValue(ActivityViewAction.ON_SALE)
        } else {
            actionSingleLiveEvent.setValue(ActivityViewAction.SALE)
        }

    }


}