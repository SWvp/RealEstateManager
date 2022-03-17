package com.kardabel.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceConverterRepository @Inject constructor(){

    private var isDollar: Boolean = true
    private val currentCurrencyMutableLiveData = MutableLiveData<Boolean>()
    val getCurrentCurrencyLiveData: LiveData<Boolean> = currentCurrencyMutableLiveData

    fun convertPricePlease(){
        isDollar = !isDollar
        currentCurrencyMutableLiveData.value = isDollar
    }
}