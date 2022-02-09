package com.kardabel.realestatemanager.ui.edit

import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.ChipGroup
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityCreatePropertyBinding
import com.kardabel.realestatemanager.ui.create.CreatePropertyPhotosAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPropertyActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCreatePropertyBinding
    private lateinit var interestChipGroup: ChipGroup

    private lateinit var photosAdapter: CreatePropertyPhotosAdapter

    private val PERMS: String = Manifest.permission.READ_EXTERNAL_STORAGE

    lateinit var currentPhotoPath: String
    private var uriImageSelected: Uri? = null
    private var propertyType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set toolbar option
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = "Edit property"
        binding.toolbar.navigationIcon?.setTint(resources.getColor(R.color.white))
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


    }
}