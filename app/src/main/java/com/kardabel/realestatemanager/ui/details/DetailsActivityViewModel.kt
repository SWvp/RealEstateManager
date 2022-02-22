package com.kardabel.realestatemanager.ui.details

import androidx.lifecycle.ViewModel
import com.kardabel.realestatemanager.repository.CreatePhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsActivityViewModel @Inject constructor(
    private val createPhotoRepository: CreatePhotoRepository,
) : ViewModel() {


    // Clear the createdPhotoRepoS for the next use
    fun emptyCreatedPhotoRepository() {
        createPhotoRepository.emptyCreatePhotoList()
    }


}