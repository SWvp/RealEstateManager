package com.kardabel.realestatemanager.repository

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentSearchRepository @Inject constructor() {

    val mySearchParamsFlow = MutableStateFlow<Boolean?>(null)


}