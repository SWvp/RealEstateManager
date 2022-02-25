package com.kardabel.realestatemanager.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var photosAdapter: DetailsAdapter


    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the adapter to retrieve photo recently added
        val recyclerView: RecyclerView = binding.detailPortraitRecyclerView
        photosAdapter = DetailsAdapter {

        }

        recyclerView.adapter = photosAdapter

        // Observe the selected property
        viewModel.detailsLiveData.observe(this) { property ->

            setVisibility(property)
            setView(property)

        }

        // Observe if empty the view is needed (when back from a search)
        viewModel.isFromSearchLiveData.observe(this){

            setViewWhenBackFromSearchActivity(it)

        }
    }

    private fun setVisibility(property: DetailsViewState) {

        // set visibility first
        // cause we don't want to see anything before user click on a property item
        binding.detailDescriptionTitle.isVisible = property.visibility
        binding.detailsMediaTitle.isVisible = property.visibility
        binding.surfaceIcon.isVisible = property.visibility
        binding.surfaceTitle.isVisible = property.visibility
        binding.roomIcon.isVisible = property.visibility
        binding.roomTitle.isVisible = property.visibility
        binding.bedroomIcon.isVisible = property.visibility
        binding.bedroomTitle.isVisible = property.visibility
        binding.bathroomIcon.isVisible = property.visibility
        binding.bathroomTitle.isVisible = property.visibility
        binding.interestsIcon.isVisible = property.visibility
        binding.interestsTitle.isVisible = property.visibility
        binding.chipGroup.isVisible = property.visibility
        binding.startSaleDate.isVisible = property.visibility
        binding.locationIcon.isVisible = property.visibility
        binding.locationTitle.isVisible = property.visibility
        binding.startSaleDate.isVisible = property.visibility
        binding.map.isVisible = property.visibility

        binding.emptyLayout?.setBackgroundColor(resources.getColor(R.color.transparent))
    }

    private fun setView(property: DetailsViewState) {

        // Set text
        binding.descriptionText.text = property.description
        binding.surfaceValue.text = property.surface
        binding.roomValue.text = property.room
        binding.bedroomValue.text = property.bedroom
        binding.bathroomValue.text = property.bathroom
        binding.address.text = property.address
        binding.apartment.text = property.apartment
        binding.city.text = property.city
        binding.county.text = property.county
        binding.zipCode.text = property.zipcode
        binding.country.text = property.country
        binding.startSaleDate.text = property.startSale

        Glide.with(binding.map.context).load(property.staticMap).into(binding.map)

        photosAdapter.submitList(property.photos)

        displayInterestAsChip(property.interest)
    }

    // Fake layer to don't show details when back from searchActivity (when search is done)
    private fun setViewWhenBackFromSearchActivity(it: Boolean) {

        if(!it){
            binding.emptyLayout?.setBackgroundColor(resources.getColor(R.color.white))
        }
    }

    // Interests are display trough chip group
    private fun displayInterestAsChip(interests: List<String>?) {

        val inflater = LayoutInflater.from(requireContext())

        // Set chip group binding
        val interestChipGroup: ChipGroup = binding.chipGroup
        // Remove previous selection (for master-details)
        interestChipGroup.removeAllViews()

        if (interests != null) {
            if (interests.isNotEmpty()) {
                for (interest in interests) {
                    val chip: Chip =
                        inflater.inflate(
                            R.layout.item_interest_chip_details,
                            interestChipGroup,
                            false
                        ) as Chip
                    chip.text = interest
                    interestChipGroup.addView(chip)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}