package com.kardabel.realestatemanager.ui.create

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityCreatePropertyBinding
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


const val RC_IMAGE_PERMS = 100
const val REQUEST_IMAGE_CAPTURE = 1
const val RC_CHOOSE_PHOTO = 200


@AndroidEntryPoint
class CreatePropertyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePropertyBinding
    private lateinit var interestChipGroup: ChipGroup

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


        // Set toolbar option
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.setTint(resources.getColor(R.color.white))

        // Set chip group binding
        interestChipGroup = binding.chipGroup

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
            OnItemClickListener { parent, view, position, id ->
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

        // Set the adapter to retrieve photo recently added
        val recyclerView: RecyclerView = binding.picturePropertyRecyclerView
        photosAdapter = CreatePropertyPhotosAdapter {

        }
        recyclerView.adapter = photosAdapter

        // and set the observer
        viewModel.getPhoto.observe(this) {
            photosAdapter.submitList(it)
        }

        viewModel.actionSingleLiveEvent.observe(this){ viewAction ->
            when(viewAction){
                CreateActivityViewAction.FIELDS_ERROR ->
                    Toast.makeText(applicationContext, getString(R.string.fields_error), Toast.LENGTH_SHORT).show()

                CreateActivityViewAction.FINISH_ACTIVITY -> onBackPressed()
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

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File = createImageFile()

        uriImageSelected = FileProvider.getUriForFile(
            this,
            "com.kardabel.realestatemanager.fileprovider",
            photoFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImageSelected)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    // When photo is created, we need to create an image file
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
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
                if (uriImageSelected == null) {
                    uriImageSelected = data!!.data
                }

                val bitmap: Bitmap? = uriImageSelected?.let { decodeUriToBitmap(this, it) }

                if (bitmap != null) {
                    confirmDialogFragment(bitmap)
                }
            }
        }
    }

    // Create an alert dialog to ask user type a photo description,
    // then, when validate, send whole photo object to a repo via VM
    private fun confirmDialogFragment(bitmap: Bitmap) {
        val confirmFragment = PhotoConfirmationFragment.newInstance(bitmap)
        confirmFragment.show(supportFragmentManager, "confirmPhotoMessage")

    }

    // Converter to get bitmap from Uri
    private fun decodeUriToBitmap(context: Context, sendUri: Uri): Bitmap {
        var getBitmap: Bitmap? = null
        try {
            val imageStream: InputStream
            try {
                imageStream = context.contentResolver.openInputStream(sendUri)!!
                getBitmap = BitmapFactory.decodeStream(imageStream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getBitmap!!
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