package com.kardabel.realestatemanager.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityDetailsBinding
import com.kardabel.realestatemanager.ui.edit.EditPropertyActivity
import com.kardabel.realestatemanager.utils.DetailsActivityViewAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity: AppCompatActivity() {

    private val viewModel by viewModels<DetailsActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon?.setTint(resources.getColor(R.color.white))

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.detailContainer.id, DetailsFragment())
                .commitNow()
        }


        viewModel.actionSingleLiveEvent.observe(this) { viewAction ->
            when (viewAction) {
                DetailsActivityViewAction.SALE ->
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.sale),
                        Toast.LENGTH_SHORT
                    ).show()

                DetailsActivityViewAction.ON_SALE ->{
                    val intent = Intent(this, EditPropertyActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {}
            }
        }
    }

    // Toolbar menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_item -> {
                emptyCreatedPhotoRepository()
                viewModel.checkSaleStatusBeforeAccessToEditPropertyActivity()
                true
            }
            android.R.id.home -> {
                super.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun emptyCreatedPhotoRepository() {
        viewModel.emptyCreatedPhotoRepository()
    }
}