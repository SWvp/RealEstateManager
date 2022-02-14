package com.kardabel.realestatemanager

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
fun getApplicationDispatchersTest(testCoroutineRule: TestCoroutineRule): ApplicationDispatchers = mockk {
    every { mainDispatcher } returns testCoroutineRule.testCoroutineDispatcher
    every { ioDispatcher } returns testCoroutineRule.testCoroutineDispatcher
}