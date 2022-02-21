package com.kardabel.realestatemanager.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.repository.CurrentSearchRepository
import com.kardabel.realestatemanager.repository.InterestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchPropertyViewModel @Inject constructor(
    private val searchRepository: CurrentSearchRepository,
    private val interestRepository: InterestRepository,
    private val applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {

    var priceSliderMinValue: Int? = null
    var priceSliderMaxValue: Int? = null

    var surfaceSliderMinValue: Int? = null
    var surfaceSliderMaxValue: Int? = null

    var roomSliderMinValue: Int? = null
    var roomSliderMaxValue: Int? = null

    var numberOfPhotoSliderValue: Int? = null

    private var propertyType: String? = null

    val getInterest: LiveData<List<String>> = interestRepository.getInterestLiveData()



    fun addInterest(interest: String) {
        if(interest.length>2){
            interestRepository.addInterest(interest)
        }
    }

    fun removeInterest(interest: String) {

        interestRepository.remove(interest)

    }

    fun propertyType(value: String) {
        propertyType = value
    }

    fun priceRange(minValue: Int, maxValue: Int) {
        priceSliderMinValue = minValue
        priceSliderMaxValue = maxValue
    }

    fun surfaceRange(minValue: Int, maxValue: Int) {
        surfaceSliderMinValue = minValue
        surfaceSliderMaxValue = maxValue
    }

    fun roomRange(minValue: Int, maxValue: Int) {
        roomSliderMinValue = minValue
        roomSliderMaxValue = maxValue

    }

    fun minPhoto(value: Int) {
        numberOfPhotoSliderValue = value
    }

    fun search() {

    }

    fun emptyInterestRepository() {
        interestRepository.emptyInterestList()
    }

}