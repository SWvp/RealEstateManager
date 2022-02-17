package com.kardabel.realestatemanager.ui.search

import androidx.lifecycle.ViewModel
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.repository.CurrentSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchPropertyViewModel @Inject constructor(
    private val searchRepository: CurrentSearchRepository,
    private val applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {






}