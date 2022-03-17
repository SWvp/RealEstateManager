package com.kardabel.realestatemanager.ui.properties

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kardabel.realestatemanager.databinding.FragmentPropertiesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PropertiesFragment : Fragment() {

    private var _binding: FragmentPropertiesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PropertiesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertiesBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PropertiesAdapter {
            viewModel.onPropertyClicked(it)
        }

        binding.recyclerViewProperties.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProperties.adapter = adapter
        binding.recyclerViewProperties.itemAnimator = null

        viewModel.getPropertiesLiveData.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}