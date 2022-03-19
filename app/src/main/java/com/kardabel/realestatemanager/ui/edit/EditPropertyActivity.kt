package com.kardabel.realestatemanager.ui.edit

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.kardabel.realestatemanager.ui.create.RC_CHOOSE_PHOTO
import com.kardabel.realestatemanager.ui.create.RC_IMAGE_PERMS
import com.kardabel.realestatemanager.ui.create.REQUEST_IMAGE_CAPTURE
import com.kardabel.realestatemanager.ui.dialog.AddPhotoConfirmationDialogFragment
import com.kardabel.realestatemanager.ui.dialog.ValidatePhotoDialogFragment
import com.kardabel.realestatemanager.utils.ActivityViewAction
import com.kardabel.realestatemanager.utils.UriPathHelper
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditPropertyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePropertyBinding

    private var isFromCamera = false

    private lateinit var currentPhotoPath: String
    private var uriImageSelected: Uri? = null
    private var propertyType: String? = null

    private val PERMS: String = Manifest.permission.READ_EXTERNAL_STORAGE

    private val viewModel by viewModels<EditPropertyActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manageToolbar()
        managePropertyTypeDropdownMenu()
        managePhotoAdapter()
        manageInput()

    }

    private fun populateViewWithOldProperty(property: EditPropertyViewState) {

        // set visibility of sold button (basically invisible for create activity)
        binding.soldButton.isVisible = property.visibility

        binding.propertyTypeDropdownMenu.setText(property.type, false)
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

        viewModel.getInterest.observe(this) { interestList ->
            displayInterestAsChip(interestList)
        }
    }

    private fun manageToolbar() {

        // Set toolbar option
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = getString(R.string.edit_property)
        binding.toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        binding.toolbar.setNavigationOnClickListener {
            emptyCache()
            onBackPressed()
        }
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun managePropertyTypeDropdownMenu() {

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

        // Populate fields with old property
        viewModel.getDetailsLiveData.observe(this) { property ->
            populateViewWithOldProperty(property)
        }
    }

    private fun managePhotoAdapter() {

        // Set the adapter to retrieve photo
        val recyclerView: RecyclerView = binding.picturePropertyRecyclerView
        val photosAdapter = EditPropertyPhotoAdapter {
            editDialogFragment(it)

        }
        recyclerView.adapter = photosAdapter

        // and set the observer
        viewModel.getPhoto.observe(this) {
            photosAdapter.submitList(it)
        }
    }

    private fun manageInput() {

        // Manage type
        binding.propertyTypeDropdownMenu.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                propertyType = parent.getItemAtPosition(position).toString()
            }

        // Manage interest
        binding.addInterestButton.setOnClickListener {
            val interest = binding.inputInterest.text.toString()
            viewModel.addInterest(interest)
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

        // Sold button
        binding.soldButton.setOnClickListener {
            alertDialog()
        }

        // Manage action after click save button
        viewModel.actionSingleLiveEvent.observe(this) { viewAction ->
            when (viewAction) {
                ActivityViewAction.FIELDS_ERROR ->
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.fields_error),
                        Toast.LENGTH_SHORT
                    ).show()

                ActivityViewAction.FINISH_ACTIVITY ->
                    finish()
                else -> {}
            }
        }
    }

    // Interests are display as chip trough chip group
    private fun displayInterestAsChip(interests: List<String>) {

        val interestChipGroup: ChipGroup = binding.chipGroup

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun addPhotoFromStorage() {
        // uri could be non null if have already add photo for this property
        uriImageSelected = null
        isFromCamera = false
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.popup_title_permission_files_access),
                RC_IMAGE_PERMS,
                PERMS
            )
            return
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RC_CHOOSE_PHOTO)
    }

    private fun capturePhoto() {
        isFromCamera = true
        val cameraInt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File = createImageFile()
        uriImageSelected = FileProvider.getUriForFile(
            this,
            getString(R.string.authority),
            photoFile
        )
        cameraInt.putExtra(MediaStore.EXTRA_OUTPUT, uriImageSelected)
        startActivityForResult(cameraInt, REQUEST_IMAGE_CAPTURE)
    }

    // When photo is created, we need to create an image file
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat(getString(R.string.date_pattern)).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with CAMERA
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handleResponse(requestCode, resultCode, data)
    }

    private fun handleResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CHOOSE_PHOTO || requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {

                if (!isFromCamera) {
                    uriImageSelected = data!!.data
                    val uriPathHelper = UriPathHelper()
                    val filePath = uriImageSelected?.let { uriPathHelper.getPath(this, it) }
                    confirmDialogFragment(filePath!!)

                }else{
                    confirmDialogFragment(currentPhotoPath)

                }
            }
        }
    }

    // Create an alert dialog to ask user type a photo description,
    // then, when validate, send whole photo object to a repo via VM
    private fun confirmDialogFragment(photoUriString: String) {
        val confirmFragment = AddPhotoConfirmationDialogFragment.newInstance(photoUriString)
        confirmFragment.show(supportFragmentManager, getString(R.string.confirm_Photo_Message))
    }

    // Create an alert dialog to allow user change description or delete photo
    private fun editDialogFragment(editPropertyPhotoViewState: EditPropertyPhotoViewState) {
        val confirmFragment = ValidatePhotoDialogFragment.editPropertyActivityInstance(editPropertyPhotoViewState)
        confirmFragment.show(supportFragmentManager, getString(R.string.confirm_Photo_Message))
    }

    // Alert user he is about to definitely sell the property
    private fun alertDialog() {
        val userChoice = arrayOf(getString(R.string.yes), getString(R.string.not_yes))
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(getString(R.string.confirm_sale))
        builder.setCancelable(true)
        builder.setIcon(R.drawable.warning)
        builder.setSingleChoiceItems(userChoice, -1) { _, which ->
            binding.alertTv.text = userChoice[which]
        }
        builder.setPositiveButton(getString(R.string.confirm_sold)) { dialog, _ ->
            val position = (dialog as AlertDialog).listView.checkedItemPosition
            if (position != -1) {
                when (userChoice[position]) {
                    getString(R.string.yes) -> viewModel.propertySold()
                    getString(R.string.not_yes) -> dialog.cancel()
                }
            }
        }
        builder.create().show()
    }

    // Empty photo and interest repo
    private fun emptyCache() {
        viewModel.emptyAllPhotoRepository()
        viewModel.emptyInterestRepository()
    }

    private fun saveProperty() {
        viewModel.editProperty(
            binding.inputPropertyAddress.text.toString(),
            binding.inputApartmentNumber.text.toString(),
            binding.inputPropertyCounty.text.toString(),
            binding.inputPropertyCity.text.toString(),
            binding.inputPropertyZipCode.text.toString(),
            binding.inputPropertyCountry.text.toString(),
            binding.inputDescription.text.toString(),
            binding.propertyTypeDropdownMenu.text.toString(),
            binding.inputPrice.text.toString(),
            binding.inputSurface.text.toString(),
            binding.inputRoom.text.toString(),
            binding.inputBedroom.text.toString(),
            binding.inputBathroom.text.toString(),
        )
    }
}