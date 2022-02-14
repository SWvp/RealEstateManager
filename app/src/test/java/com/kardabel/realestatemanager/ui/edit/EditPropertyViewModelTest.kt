package com.kardabel.realestatemanager.ui.edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kardabel.realestatemanager.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

@ExperimentalCoroutinesApi
class EditPropertyViewModelTest {

    @get: Rule
    val testCoroutineRule = TestCoroutineRule()

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
}