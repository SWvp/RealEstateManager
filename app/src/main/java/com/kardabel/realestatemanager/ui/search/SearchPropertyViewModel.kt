package com.kardabel.realestatemanager.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kardabel.realestatemanager.model.SearchParams
import com.kardabel.realestatemanager.repository.CurrentPropertyIdRepository
import com.kardabel.realestatemanager.repository.CurrentSearchRepository
import com.kardabel.realestatemanager.repository.InterestRepository
import com.kardabel.realestatemanager.utils.SearchActivityViewAction
import com.kardabel.realestatemanager.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchPropertyViewModel @Inject constructor(
    private val searchRepository: CurrentSearchRepository,
    private val interestRepository: InterestRepository,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository,
) : ViewModel() {

    val actionSingleLiveEvent = SingleLiveEvent<SearchActivityViewAction>()

    private var priceSliderMinValue: Int? = null
    private var priceSliderMaxValue: Int? = null

    private var surfaceSliderMinValue: Int? = null
    private var surfaceSliderMaxValue: Int? = null

    private var roomSliderMinValue: Int? = null
    private var roomSliderMaxValue: Int? = null

    private var numberOfPhotoSliderValue: Int? = null

    private var propertyType: String? = null

    private var interestList = mutableListOf<String>()
    val getInterest: LiveData<List<String>> = interestRepository.getInterestLiveData()

    fun addInterest(interest: String) {
        if (interest.length > 2) {
            interestRepository.addInterest(interest)
            interestList.add(interest)
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

    fun onSearchClick(county: String) {

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
            roomRange = roomSliderMinValue?.let { roomMin ->
                roomSliderMaxValue?.let { roomMax ->
                    IntRange(
                        roomMin,
                        roomMax
                    )
                }
            },
            numberOfPhotoSliderValue,
            propertyType,
            interestListCanBeNull(interestList),
            countyCanBeNull(county)
        )

        if (userChooseAtLeastOneParameter(newSearchParams)) {
            sendSearchParamsToRepository(newSearchParams)
            emptyInterestRepository()
            emptyDetailsViewWhenDone()
            actionSingleLiveEvent.setValue(SearchActivityViewAction.FINISH_ACTIVITY)

        } else {
            actionSingleLiveEvent.setValue(SearchActivityViewAction.NO_PARAMETER_SELECTED)

        }
    }

    private fun interestListCanBeNull(interests: List<String>): List<String>? {
        return interests.ifEmpty {
            null
        }
    }

    private fun countyCanBeNull(county: String): String? {
        return county.ifEmpty {
            null
        }
    }

    private fun userChooseAtLeastOneParameter(newSearchParams: SearchParams): Boolean {
        return !(newSearchParams.priceRange == null
                && newSearchParams.surfaceRange == null
                && newSearchParams.roomRange == null
                && newSearchParams.photo == null
                && newSearchParams.propertyType == null
                && newSearchParams.interest == null
                && newSearchParams.county == null
                )
    }

    private fun sendSearchParamsToRepository(newSearchParams: SearchParams) {
        searchRepository.updateSearchParams(newSearchParams)
    }

    // In case of back pressed or search, re initialised interest repository
    fun emptyInterestRepository() {
        interestRepository.emptyInterestList()
    }

    // In case of search, inform details view that is nothing to display until user click on a property
    private fun emptyDetailsViewWhenDone() {
        currentPropertyIdRepository.isBackFromSearchActivity()
    }
}