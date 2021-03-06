package com.kardabel.realestatemanager.ui.create

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.kardabel.realestatemanager.ui.dialog.AddPhotoConfirmationDialogFragment
import com.kardabel.realestatemanager.ui.dialog.ValidatePhotoDialogFragment
import com.kardabel.realestatemanager.utils.CreateActivityViewAction
import com.kardabel.realestatemanager.utils.UriPathHelper
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


const val RC_IMAGE_PERMS = 100
const val REQUEST_IMAGE_CAPTURE = 1
const val RC_CHOOSE_PHOTO = 200


@AndroidEntryPoint
class CreatePropertyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePropertyBinding
    private lateinit var interestChipGroup: ChipGroup

    private var isFromCamera = false

    private lateinit var photosAdapter: CreatePropertyPhotosAdapter

    private val PERMS: String = Manifest.permission.READ_EXTERNAL_STORAGE

    lateinit var currentPhotoPath: String
    private var uriImageSelected: Uri? = null
    private var propertyType: String? = null

    private val viewModel: CreatePropertyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set chip group binding
        interestChipGroup = binding.chipGroup

        if (ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        )
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )

        manageToolbar()
        managePropertyTypeDropdownMenu()
        managePhotoAdapter()
        manageInput()
        liveEventAction()
    }

    private fun manageToolbar() {

        // Set toolbar option
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))

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
                viewModel.emptyPhotoRepository()
                onBackPressed()
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

    }

    private fun managePhotoAdapter() {

        // Set the adapter to retrieve photo recently added
        // On item click go to edit dialog
        val recyclerView: RecyclerView = binding.picturePropertyRecyclerView
        photosAdapter = CreatePropertyPhotosAdapter {
            editPhotoDialogFragment(it)
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
            OnItemClickListener { parent, _, position, _ ->
                propertyType = parent.getItemAtPosition(position).toString()
            }

        // Manage interest
        binding.addInterestButton.setOnClickListener {
            val interest = binding.inputInterest.text.toString()
            viewModel.addInterest(interest)
            // addNewChipInterest(interest)
            binding.inputInterest.text?.clear()
        }

        // Get interests
        viewModel.getInterest.observe(this) { interestList ->
            displayInterestAsChip(interestList)
        }


        // Manage storage photo picker
        binding.addStoragePictureButton.setOnClickListener {
            addPhotoFromStorage()
        }

        // Manage camera to capture a pic
        binding.addCameraPictureButton.setOnClickListener {
            capturePhoto()
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

    @AfterPermissionGranted(RC_IMAGE_PERMS)
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
                } else {
                    confirmDialogFragment(currentPhotoPath)
                }
            }
        }
    }

    // Create an alert dialog to ask user a photo description,
    // then, when validate, send whole photo object to a repo via VM
    private fun confirmDialogFragment(photoUriString: String) {
        val confirmFragment = AddPhotoConfirmationDialogFragment.newInstance(photoUriString)
        confirmFragment.show(supportFragmentManager, getString(R.string.confirm_Photo_Message))

    }

    // Create an alert dialog to allow user change description or delete photo
    private fun editPhotoDialogFragment(propertyPhotoViewState: CreatePropertyPhotoViewState) {
        val confirmFragment =
            ValidatePhotoDialogFragment.createPropertyActivityInstance(propertyPhotoViewState)
        confirmFragment.show(supportFragmentManager, getString(R.string.confirm_Photo_Message))

    }

    private fun liveEventAction() {

        // Inform user if fields are missing or close activity
        viewModel.actionSingleLiveEvent.observe(this) { viewAction ->
            when (viewAction) {
                CreateActivityViewAction.FIELDS_ERROR ->
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.fields_error),
                        Toast.LENGTH_SHORT
                    ).show()

                CreateActivityViewAction.FINISH_ACTIVITY -> {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.creating_property_successful),
                        Toast.LENGTH_SHORT
                    ).show()
                    onBackPressed()
                }

                CreateActivityViewAction.INTEREST_FIELD_ERROR ->
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.interest_input_problem),
                        Toast.LENGTH_SHORT
                    ).show()
                else -> {}
            }
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