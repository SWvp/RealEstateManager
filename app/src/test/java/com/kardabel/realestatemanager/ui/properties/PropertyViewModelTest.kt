package com.kardabel.realestatemanager.ui.properties

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kardabel.realestatemanager.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

@ExperimentalCoroutinesApi
class PropertyViewModelTest {

    @get: Rule
    val testCoroutineRule = TestCoroutineRule()

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


}