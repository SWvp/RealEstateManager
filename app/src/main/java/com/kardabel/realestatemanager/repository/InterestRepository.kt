package com.kardabel.realestatemanager.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterestRepository @Inject constructor() {

    private var interestList = mutableListOf<String>()
    private val interestLiveData = MutableLiveData<List<String>>()

    fun getInterestLiveData(): LiveData<List<String>> = interestLiveData

    @MainThread
    fun addInterest(interest: String){
        interestList.add(interest)
        interestLiveData.postValue(interestList)
    }

    fun getInterest(): MutableList<String> {
        return interestList
    }

    fun emptyInterestList() {
        interestList.clear()
    }

    fun remove(interest: String) {
        interestList.remove(interest)
    }
}