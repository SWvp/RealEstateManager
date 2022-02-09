package com.kardabel.realestatemanager.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.FragmentDetailsBinding
import com.kardabel.realestatemanager.ui.edit.EditPropertyActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var photosAdapter: DetailsAdapter
    private lateinit var interestChipGroup: ChipGroup

    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // This observer notify the view about master details status,
        // if true, the toolbar gone
        viewModel.masterDetailsStatusLiveData.observe(this) { isMasterDetails ->
            when (isMasterDetails) {
                false -> setupToolbar()
                true -> binding.toolbar.visibility = View.GONE

            }
        }

        // Set chip group binding
        interestChipGroup = binding.chipGroup

        // Set the adapter to retrieve photo recently added
        val recyclerView: RecyclerView = binding.detailPortraitRecyclerView
        photosAdapter = DetailsAdapter {

        }

        recyclerView.adapter = photosAdapter

        // and set the observer
        viewModel.detailsLiveData.observe(this) {
            // set visibility first
            // cause we don't want to see anything before user click on a property item
            binding.detailDescriptionTitle.isVisible = it.visibility
            binding.detailsMediaTitle.isVisible = it.visibility
            binding.surfaceIcon.isVisible = it.visibility
            binding.surfaceTitle.isVisible = it.visibility
            binding.roomIcon.isVisible = it.visibility
            binding.roomTitle.isVisible = it.visibility
            binding.bedroomIcon.isVisible = it.visibility
            binding.bedroomTitle.isVisible = it.visibility
            binding.bathroomIcon.isVisible = it.visibility
            binding.bathroomTitle.isVisible = it.visibility
            binding.interestsIcon.isVisible = it.visibility
            binding.interestsTitle.isVisible = it.visibility
            binding.chipGroup.isVisible = it.visibility
            binding.startSaleDate.isVisible = it.visibility
            binding.locationIcon.isVisible = it.visibility
            binding.locationTitle.isVisible = it.visibility
            binding.startSaleDate.isVisible = it.visibility
            binding.map.isVisible = it.visibility

            // Set text
            binding.descriptionText.text = it.description
            binding.surfaceValue.text = it.surface
            binding.roomValue.text = it.room
            binding.bedroomValue.text = it.bedroom
            binding.bathroomValue.text = it.bathroom
            binding.address.text = it.address
            binding.apartment.text = it.apartment
            binding.city.text = it.city
            binding.county.text = it.county
            binding.zipCode.text = it.zipcode
            binding.country.text = it.country
            binding.startSaleDate.text = it.startSale

            Glide.with(binding.map.context).load(it.staticMap).into(binding.map)

            photosAdapter.submitList(it.photos)

            displayInterestAsChip(it.interest)
        }

        // Go to edit activity
        binding.button?.setOnClickListener {
            editProperty()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.back_arrow)
        binding.toolbar.title = "Details"
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    // Interest are display trough chip group
    private fun displayInterestAsChip(interests: List<String>?) {
        val inflater = LayoutInflater.from(requireContext())
        if (interests != null) {
            for (interest in interests) {
                val chip: Chip =
                    inflater.inflate(
                        R.layout.item_interest_chip,
                        this.interestChipGroup,
                        false
                    ) as Chip
                chip.text = interest
                interestChipGroup.addView(chip)
            }
        }
    }

    // Go to edit activity
    private fun editProperty() {
        val intent = Intent(activity, EditPropertyActivity::class.java)
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}