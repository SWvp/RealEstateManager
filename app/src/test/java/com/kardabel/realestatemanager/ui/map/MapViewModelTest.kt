package com.kardabel.realestatemanager.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kardabel.realestatemanager.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

@ExperimentalCoroutinesApi
class MapViewModelTest {

    @get: Rule
    val testCoroutineRule = TestCoroutineRule()

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
}