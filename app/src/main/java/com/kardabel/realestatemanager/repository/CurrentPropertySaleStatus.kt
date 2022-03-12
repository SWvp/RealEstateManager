package com.kardabel.realestatemanager.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentPropertySaleStatus @Inject constructor() {

    private val saleStatusMutableStateFlow = MutableStateFlow<String?>(null)
    var isOnSale: Boolean = true
    fun saleStatusParamsFlow() : Flow<String?> = saleStatusMutableStateFlow.asStateFlow()

    fun updateSaleStatus(saleStatus: String) {
        saleStatusMutableStateFlow.value = saleStatus
        isOnSale = saleStatus == "On Sale !"
    }
}