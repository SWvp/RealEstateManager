package com.kardabel.realestatemanager.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.model.SearchParams
import com.kardabel.realestatemanager.repository.CurrentSearchRepository
import com.kardabel.realestatemanager.repository.InterestRepository
import com.kardabel.realestatemanager.utils.ActivityViewAction
import com.kardabel.realestatemanager.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchPropertyViewModel @Inject constructor(
    private val searchRepository: CurrentSearchRepository,
    private val interestRepository: InterestRepository,
    private val applicationDispatchers: ApplicationDispatchers,
) : ViewModel() {

    val actionSingleLiveEvent = SingleLiveEvent<ActivityViewAction>()

    private var priceSliderMinValue: Int? = null
    private var priceSliderMaxValue: Int? = null

    private var surfaceSliderMinValue: Int? = null
    private var surfaceSliderMaxValue: Int? = null

    private var roomSliderMinValue: Int? = null
    private var roomSliderMaxValue: Int? = null

    private var numberOfPhotoSliderValue: Int? = null

    private var propertyType: String? = null

    private var interestList: List<String>? = null
    val getInterest: LiveData<List<String>> = interestRepository.getInterestLiveData()

    fun addInterest(interest: String) {
        if (interest.length > 2) {
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

    fun interest(interests: List<String>) {
        interestList = interests
    }

    fun search(county: String?) {

        val newSearchParams = SearchParams(
            priceRange = priceSliderMinValue?.let { priceMin ->
                priceSliderMaxValue?.let { priceMax ->
                    IntRange(
                        priceMin,
                        priceMax
                    )
                }
            },
            surfaceRange = surfaceSliderMinValue?.let { surfaceMin ->
                surfaceSliderMaxValue?.let { surfaceMax ->
                    IntRange(
                        surfaceMin,
                        surfaceMax
                    )
                }
            },
            roomRange = roomSliderMinValue?.let { rooomMin ->
                roomSliderMaxValue?.let { roomMax ->
                    IntRange(
                        rooomMin,
                        roomMax
                    )
                }
            },
            numberOfPhotoSliderValue,
            propertyType,
            interestList,
            county
        )
        searchRepository.updateSearchParams(newSearchParams)

        actionSingleLiveEvent.setValue(ActivityViewAction.FINISH_ACTIVITY)
    }

    fun emptyInterestRepository() {
        interestRepository.emptyInterestList()
    }
}