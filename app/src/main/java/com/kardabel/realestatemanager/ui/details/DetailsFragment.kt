package com.kardabel.realestatemanager.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kardabel.realestatemanager.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.detailsLiveData.observe(this){
            binding.descriptionText?.text= it.description
            binding.surfaceValue.text = it.surface
            binding.roomValue?.text = it.room
            binding.bedroomValue?.text = it.bedroom
            binding.bathroomValue?.text = it.bathroom
            binding.address?.text = it.address
            binding.apartment.text = it.apartment
            binding.city.text = it.city
            binding.county.text = it.county
            binding.zipCode.text = it.zipcode
            binding.country.text = it.country
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}