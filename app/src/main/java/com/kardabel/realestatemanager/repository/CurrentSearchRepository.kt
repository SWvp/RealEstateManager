package com.kardabel.realestatemanager.repository

import com.kardabel.realestatemanager.model.SearchParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentSearchRepository @Inject constructor() {

    private val searchParamsMutableStateFlow = MutableStateFlow<SearchParams?>(null)
    fun searchParamsParamsFlow() : Flow<SearchParams?> = searchParamsMutableStateFlow.asStateFlow()

    fun updateSearchParams(searchParamsParams: SearchParams) {
        searchParamsMutableStateFlow.value = searchParamsParams
    }

}