package com.kardabel.realestatemanager.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.RangeSlider
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivitySearchPropertyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchPropertyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchPropertyBinding

    private lateinit var interestChipGroup: ChipGroup
    // private var propertyType: String? = null

    private val viewModel by viewModels<SearchPropertyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // var priceSliderMinValue: Int? = null
        // var priceSliderMaxValue: Int? = null

        // var surfaceSliderMinValue: Int? = null
        // var surfaceSliderMaxValue: Int? = null

        // var roomSliderMinValue: Int? = null
        // var roomSliderMaxValue: Int? = null
        //
        // var numberOfPhotoSliderValue: Int? = null

        interestChipGroup = binding.chipGroup

        // Set toolbar option
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        binding.toolbar.setNavigationOnClickListener {
            viewModel.emptyInterestRepository()
            onBackPressed()
        }

        // Set dropdown menu for type of property
        val items = arrayOf(
            getString(R.string.Flat),
            getString(R.string.House),
            getString(R.string.Duplex),
            getString(R.string.Penthouse),
            getString(R.string.Condo),
            getString(R.string.Apartment),
        )

        val dropDownAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, R.layout.activity_create_property_type_dropdown, items
        )

        binding.propertyTypeDropdownMenu.setAdapter(dropDownAdapter)

        // Manage type
        binding.propertyTypeDropdownMenu.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                //propertyType = parent.getItemAtPosition(position).toString()
                viewModel.propertyType(parent.getItemAtPosition(position).toString())
            }

        // Manage interest
        binding.addInterestButton.setOnClickListener {
            val interest = binding.inputInterest.text.toString()
            viewModel.addInterest(interest)
            //addNewChipInterest(interest)
            binding.inputInterest.text?.clear()
        }

        // Price slider
        val priceRangeSlider = binding.priceRangeSlider
        priceRangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                val values = priceRangeSlider.values
                //priceSliderMinValue = values[0].toInt()
                //priceSliderMaxValue = values[1].toInt()
                viewModel.priceRange(values[0].toInt(), values[1].toInt())
            }
        })

        // Surface slider
        val surfaceRangeSlider = binding.surfaceRangeSlider
        surfaceRangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                val values = surfaceRangeSlider.values
                //surfaceSliderMinValue = values[0].toInt()
                //surfaceSliderMaxValue = values[1].toInt()
                viewModel.surfaceRange(values[0].toInt(), values[1].toInt())
            }
        })

        // Room slider
        val roomRangeSlider = binding.surfaceRangeSlider
        roomRangeSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                val values = roomRangeSlider.values
                //roomSliderMinValue = values[0].toInt()
                //roomSliderMaxValue = values[1].toInt()
                viewModel.roomRange(values[0].toInt(), values[1].toInt())
            }
        })

        // Photo slider
        val numberOfPhotoSlider = binding.photoSlider
        numberOfPhotoSlider.addOnChangeListener { slider, value, fromUser ->
            // numberOfPhotoSliderValue = value.toInt()
            viewModel.minPhoto(value.toInt())
        }

        // Retrieve interest list from repository and display them
        viewModel.getInterest.observe(this) { interestList ->
            displayInterestAsChip(interestList)
        }

        binding.searchButton.setOnClickListener {
            viewModel.search()
        }
    }

    // Interests are display as chip trough chip group
    private fun displayInterestAsChip(interests: List<String>) {

        interestChipGroup.clearCheck()
        interestChipGroup.removeAllViewsInLayout()

        val inflater = LayoutInflater.from(this)
        for (interest in interests) {
            val chip: Chip =
                inflater.inflate(R.layout.item_interest_chip, interestChipGroup, false) as Chip
            chip.text = interest
            interestChipGroup.addView(chip)
            chip.setOnClickListener {
                val parent = chip.parent as ChipGroup
                parent.removeView(chip)
                viewModel.removeInterest(interest)
            }
        }
    }

    private fun addNewChipInterest(interest: String) {
        val inflater = LayoutInflater.from(this)
        val chip: Chip =
            inflater.inflate(R.layout.item_interest_chip, this.interestChipGroup, false) as Chip
        chip.text = interest
        if (interest.length > 2) {
            interestChipGroup.addView(chip)
        }
    }


}