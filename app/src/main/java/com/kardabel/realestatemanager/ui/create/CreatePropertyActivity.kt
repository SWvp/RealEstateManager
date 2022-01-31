package com.kardabel.realestatemanager.ui.create

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityCreatePropertyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePropertyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePropertyBinding

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

        // Set dropdown menu for type of property
        val items = arrayOf(
            "Flat", "House", "Duplex", "Penthouse", "Condo", "Apartment",
        )

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, R.layout.activity_create_property_type_dropdown, items
        )
        binding.propertyTypeDropdownMenu.setAdapter(adapter)

        binding.propertyTypeDropdownMenu.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                propertyType = parent.getItemAtPosition(position).toString()
            }

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
      //viewModel.createProperty(
      //    binding.inputPropertyAddress.text.toString(),
      //    binding.typeDescription.text.toString(),
      //    binding.type.text.toString(),

      //    )
    }
}