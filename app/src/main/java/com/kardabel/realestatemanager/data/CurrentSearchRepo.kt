package com.kardabel.realestatemanager.data

import kotlinx.coroutines.flow.MutableStateFlow

class CurrentSearchRepo {

    val mySearchParamsFlow = MutableStateFlow<Boolean?>(null)

}
