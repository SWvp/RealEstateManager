package com.kardabel.realestatemanager.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentSearchRepository @Inject constructor() {

    private val searchParamsMutableStateFlow = MutableStateFlow<SearchParams?>(null)
    val searchParamsFlow : Flow<SearchParams?> = searchParamsMutableStateFlow.asStateFlow()

    fun updateSearchParams(searchParams: SearchParams) {
        searchParamsMutableStateFlow.value = searchParams
    }

    data class SearchParams(
        val foo: String,
        val bar: Boolean,
    )


}