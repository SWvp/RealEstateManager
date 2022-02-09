package com.kardabel.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MasterDetailsStatusRepository @Inject constructor() {

    // This repo notify the fragment if master details is enable or not,
    // so we can use toolbar wisely on details view

    private val currentMasterDetailsStatusMutableLiveData = MutableLiveData<Boolean>()
    val getCurrentMasterDetailsStatusLiveData: LiveData<Boolean> = currentMasterDetailsStatusMutableLiveData

    fun setMasterDetailsStatus(value: Boolean) {
        currentMasterDetailsStatusMutableLiveData.value = value

    }


}