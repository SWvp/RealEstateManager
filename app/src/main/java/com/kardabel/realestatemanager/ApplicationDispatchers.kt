package com.kardabel.realestatemanager

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationDispatchers @Inject constructor() {
    val mainDispatcher: CoroutineDispatcher = Dispatchers.IO
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
}