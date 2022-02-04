package com.kardabel.realestatemanager.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPropertyIdRepository @Inject constructor(){
    private val currentPropertyIdMutableLiveData = MutableLiveData<String>()
    val currentPropertyIdLiveData: LiveData<String> = currentPropertyIdMutableLiveData

    @MainThread
    fun setCurrentPropertyId(propertyId: String){
        currentPropertyIdMutableLiveData.value = propertyId
    }
}