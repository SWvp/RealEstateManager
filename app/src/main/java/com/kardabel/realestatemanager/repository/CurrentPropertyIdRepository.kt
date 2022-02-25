package com.kardabel.realestatemanager.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPropertyIdRepository @Inject constructor(){

    private val currentPropertyIdMutableLiveData = MutableLiveData<Long>()
    private val isBackFromSearchActivityLiveData = MutableLiveData<Boolean>()

    var isProperty: Boolean = false

    val currentPropertyIdLiveData: LiveData<Long> = currentPropertyIdMutableLiveData
    val isFromSearchLiveData: LiveData<Boolean> = isBackFromSearchActivityLiveData

    @MainThread
    fun setCurrentPropertyId(propertyId: Long){
        currentPropertyIdMutableLiveData.postValue(propertyId)
        isProperty = true
    }

    // In case of search, inform details view that is nothing to display
    fun isBackFromSearchActivity(){
        isBackFromSearchActivityLiveData.postValue(false)
    }
}