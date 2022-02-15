package com.kardabel.realestatemanager.ui.edit

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.kardabel.realestatemanager.ui.create.CreateActivityViewAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPropertyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePropertyBinding
    private lateinit var interestChipGroup: ChipGroup

    private lateinit var photosAdapter: EditPropertyPhotoAdapter

    private val PERMS: String = Manifest.permission.READ_EXTERNAL_STORAGE

    lateinit var currentPhotoPath: String
    private var uriImageSelected: Uri? = null
    private var propertyType: String? = null

    private val viewModel by viewModels<EditPropertyActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.emptyPhotoRepository()

        // Set chip group binding
        interestChipGroup = binding.chipGroup

        // Set toolbar option
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = "Edit property"
        binding.toolbar.navigationIcon?.setTint(resources.getColor(R.color.white))
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        //////// POPULATE FIELDS WITH OLD PROPERTY ////////////

        viewModel.getDetailsLiveData.observe(this) { property ->
            populateViewWithOldProperty(property)
        }

        // Set the adapter to retrieve photo recently added
        val recyclerView: RecyclerView = binding.picturePropertyRecyclerView
        photosAdapter = EditPropertyPhotoAdapter {

        }
        recyclerView.adapter = photosAdapter

        // and set the observer
        viewModel.getPhoto.observe(this) {
            photosAdapter.submitList(it)
        }


        //////// BINDS ////////////

        // Set dropdown menu for type of property
        val items = arrayOf(
            "Flat", "House", "Duplex", "Penthouse", "Condo", "Apartment",
        )

        val dropDownAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, R.layout.activity_create_property_type_dropdown, items
        )

        binding.propertyTypeDropdownMenu.setAdapter(dropDownAdapter)

        // Manage type
        binding.propertyTypeDropdownMenu.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                propertyType = parent.getItemAtPosition(position).toString()
            }

        // Manage interest
        binding.addInterestButton.setOnClickListener {
            val interest = binding.inputInterest.text.toString()
            viewModel.addInterest(interest)
            addNewChipInterest(interest)
            binding.inputInterest.text?.clear()
        }

        // Manage photos from storage
        binding.addStoragePictureButton.setOnClickListener {
            addPhotoFromStorage()
        }

        // Manage camera to capture a pic
        binding.addCameraPictureButton.setOnClickListener {
            capturePhoto()
        }

        viewModel.actionSingleLiveEvent.observe(this){ viewAction ->
            when(viewAction){
                CreateActivityViewAction.FIELDS_ERROR ->
                    Toast.makeText(applicationContext, getString(R.string.fields_error), Toast.LENGTH_SHORT).show()

                CreateActivityViewAction.FINISH_ACTIVITY ->
                    onBackPressed()
            }
        }
    }

    private fun capturePhoto() {
        TODO("Not yet implemented")
    }

    private fun addPhotoFromStorage() {
        TODO("Not yet implemented")
    }

    private fun populateViewWithOldProperty(property: EditPropertyViewState) {
        binding.propertyTypeDropdownMenu.setText(property.type)
        binding.inputDescription.setText(property.description)
        binding.inputSurface.setText(property.surface)
        binding.inputBedroom.setText(property.bedroom)
        binding.inputRoom.setText(property.room)
        binding.inputBathroom.setText(property.bathroom)
        binding.inputPropertyAddress.setText(property.address)
        binding.inputApartmentNumber.setText(property.apartment)
        binding.inputPropertyCounty.setText(property.county)
        binding.inputPropertyCountry.setText(property.country)
        binding.inputPropertyCity.setText(property.city)
        binding.inputPropertyZipCode.setText(property.zipcode)
        binding.inputPrice.setText(property.price)

        displayInterestAsChip(property.interest)
    }

    // Interests are display trough chip group
    private fun displayInterestAsChip(interests: List<String>?) {
        val inflater = LayoutInflater.from(this)
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

    // When user type something in interest field, create a chip to display
    private fun addNewChipInterest(interest: String) {
        val inflater = LayoutInflater.from(this)
        val chip: Chip =
            inflater.inflate(R.layout.item_interest_chip, this.interestChipGroup, false) as Chip
        chip.text = interest
        interestChipGroup.addView(chip)

    }

    // Toolbar menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_create, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_item -> {
                saveProperty()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveProperty() {
        viewModel.createProperty(
            binding.inputPropertyAddress.text.toString(),
            binding.inputApartmentNumber.text.toString(),
            binding.inputPropertyCounty.text.toString(),
            binding.inputPropertyCity.text.toString(),
            binding.inputPropertyZipCode.text.toString(),
            binding.inputPropertyCountry.text.toString(),
            binding.inputDescription.text.toString(),
            propertyType.toString(),
            binding.inputPrice.text.toString(),
            binding.inputSurface.text.toString(),
            binding.inputRoom.text.toString(),
            binding.inputBedroom.text.toString(),
            binding.inputBathroom.text.toString(),
        )
    }
}