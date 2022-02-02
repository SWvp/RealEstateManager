package com.kardabel.realestatemanager.ui.create

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityCreatePropertyBinding
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.FileNotFoundException
import java.io.InputStream


const val RC_IMAGE_PERMS = 100

@AndroidEntryPoint
class CreatePropertyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePropertyBinding

    private val PERMS: String = Manifest.permission.READ_EXTERNAL_STORAGE
    private var uriImageSelected: Uri? = null
    private val RC_CHOOSE_PHOTO = 200

    private var propertyType: String? = null
    private lateinit var interestChipGroup: ChipGroup

    private lateinit var photoAdapter: CreatePropertyAdapter

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

        binding.propertyTypeDropdownMenu.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                propertyType = parent.getItemAtPosition(position).toString()
            }
        binding.addInterestButton.setOnClickListener {
            val interest = binding.inputInterest.text.toString()
            viewModel.addInterest(interest)
            addNewChipInterest(interest)
        }

        binding.addStoragePictureButton.setOnClickListener {
            addPhotoFromStorage()
        }

        val recyclerView: RecyclerView = binding.picturePropertyRecyclerView
        photoAdapter = CreatePropertyAdapter{

        }
        recyclerView.adapter = photoAdapter



        viewModel.getPhoto.observe(this){
            photoAdapter.submitList(it)
        }


    }

    private fun addNewChipInterest(interest: String) {
        val inflater = LayoutInflater.from(this)
        val chip: Chip =
            inflater.inflate(R.layout.item_interest_chip, this.interestChipGroup, false) as Chip
        chip.text = interest
        interestChipGroup.addView(chip)
    }

    // private fun selectImage() {
    //     val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
    //         type = "image/*"
    //     }
    //     if (intent.resolveActivity(packageManager) != null) {
    //         startActivityForResult(intent, REQUEST_IMAGE_GET)
    //     }
    // }


    //  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    //      super.onActivityResult(requestCode, resultCode, data)
    //      if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
    //          val thumbnail: Bitmap = data?.getParcelableExtra("data")!!
    //          val fullPhotoUri: Uri = data.data!!
    //          // Do work with photo saved at fullPhotoUri


    //      }

    //  }

    @AfterPermissionGranted(RC_IMAGE_PERMS)
    private fun addPhotoFromStorage() {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handleResponse(requestCode, resultCode, data)
    }

    private fun handleResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                uriImageSelected = data!!.data
                //val bitmap =
                //    MediaStore.Images.Media.getBitmap(this.contentResolver, this.uriImageSelected)

                //val bitmap = BitmapFactory.decodeFile(uriImageSelected.toString())
                //val bitmap: Bitmap = getThumbnail(uriImageSelected)
                //val thumbnail: Bitmap = data.getParcelableExtra("data")!!

                //val description: String = alertDialogResponse()

                val bitmap: Bitmap? = uriImageSelected?.let { decodeUriToBitmap(this, it) }

                if (bitmap != null) {
                    alertDialogResponse(bitmap)
                }

                //if (bitmap != null) {
                //    viewModel.addPhoto(bitmap, description)
                //}

            }
        }
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

    private fun alertDialogResponse(bitmap: Bitmap) {
        var description: String
        val editText = EditText(this)
        editText.setTextColor(
            AppCompatResources.getColorStateList(
                this,
                R.color.black
            )
        )
        MaterialAlertDialogBuilder(this)
            .setTitle("Enter description")
            .setView(editText)
            .setPositiveButton("Validate") { dialog, _ ->
                if (!editText.text.isNullOrEmpty()) {
                    description = editText.text.toString()
                    viewModel.addPhoto(bitmap, description)
                    dialog.dismiss()
                }
            }
            .show()
    }


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
            binding.inputPropertyCity.text.toString(),
            binding.inputPropertyPostalCode.text.toString(),
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