package com.kardabel.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterestRepository @Inject constructor() {

    private var interestList = mutableListOf<String>()
    private val interestLiveData = MutableLiveData<List<String>>()

    fun getInterestLiveData(): LiveData<List<String>> = interestLiveData

    fun emptyPhotoList() {
        interestList.clear()
    }
}