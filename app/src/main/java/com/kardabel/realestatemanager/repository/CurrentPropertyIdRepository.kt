package com.kardabel.realestatemanager.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPropertyIdRepository @Inject constructor(){
    private val currentPropertyIdMutableLiveData = MutableLiveData<Long>()
    val currentPropertyIdLiveData: LiveData<Long> = currentPropertyIdMutableLiveData

    @MainThread
    fun setCurrentPropertyId(propertyId: Long){
        currentPropertyIdMutableLiveData.value = propertyId
    }
}