package com.kardabel.realestatemanager.ui.main

import android.Manifest.permission
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityMainBinding
import com.kardabel.realestatemanager.ui.authentication.AuthActivity
import com.kardabel.realestatemanager.ui.create.CreatePropertyActivity
import com.kardabel.realestatemanager.ui.details.DetailsActivity
import com.kardabel.realestatemanager.ui.details.DetailsFragment
import com.kardabel.realestatemanager.ui.edit.EditPropertyActivity
import com.kardabel.realestatemanager.ui.map.MapActivity
import com.kardabel.realestatemanager.ui.properties.PropertiesFragment
import com.kardabel.realestatemanager.ui.search.SearchPropertyActivity
import com.kardabel.realestatemanager.utils.MainActivityViewAction
import com.kardabel.realestatemanager.utils.ScreenPositionViewAction
import dagger.hilt.android.AndroidEntryPoint

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val locationPermissionCode = 100

    lateinit var googleSignInClient: GoogleSignInClient

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // call requestIdToken for the Auth work
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Master details is not enable
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.propertiesListContainer.id, PropertiesFragment())
                .commitNow()
        }

        // Master details is enable
        if (binding.propertyDetailsContainer != null &&
            supportFragmentManager.findFragmentById(binding.propertyDetailsContainer.id) == null
        ) {
            supportFragmentManager.beginTransaction()
                .add(
                    binding.propertyDetailsContainer.id,
                    DetailsFragment()
                )
                .commitNow()
        }

        // Single live event to manage permission and property status
        viewModel.actionSingleLiveEventMainActivity.observe(this) { viewAction ->
            when (viewAction) {
                MainActivityViewAction.PERMISSION_ASKED -> ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission.ACCESS_FINE_LOCATION),
                    locationPermissionCode
                )
                MainActivityViewAction.PERMISSION_DENIED -> {
                    val alertDialogBuilder = MaterialAlertDialogBuilder(this@MainActivity)
                    alertDialogBuilder.setTitle(getString(R.string.title_alert))
                    alertDialogBuilder.setMessage(getString(R.string.rational))
                    alertDialogBuilder.show()
                }
                MainActivityViewAction.GO_TO_EDIT_PROPERTY ->
                    startEditActivity()
                MainActivityViewAction.PROPERTY_SOLD ->
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.sale),
                        Toast.LENGTH_SHORT
                    ).show()
                MainActivityViewAction.NO_PROPERTY_SELECTED ->
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.no_property_selected),
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

        // check if we are on landscape or portrait to chose the correct view to display,
        // if portrait, start details activity
        viewModel.screenPositionSingleLiveEvent.observe(this) {
            when (it) {
                ScreenPositionViewAction.IsPortraitMode -> startActivity(
                    Intent(
                        this,
                        DetailsActivity::class.java
                    )
                )
            }
        }

        binding.fab.setOnClickListener {
            viewModel.emptyInterestRepository()
            val intent = Intent(
                this@MainActivity,
                CreatePropertyActivity::class.java
            )
            startActivity(intent)
        }
        viewModel.synchroniseWithFirestore()
    }

    private fun startEditActivity() {
        viewModel.emptyCreatedPhotoRepository()
        startActivity(
            Intent(
                this,
                EditPropertyActivity::class.java
            )
        )
    }

    // Toolbar menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.map_item -> {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.edit_item -> {
                // If no property is selected, edit will not be called
                viewModel.checkPropertyStatus()

                true
            }
            R.id.converter_item -> {
                viewModel.convertPrice()
                true
            }
            R.id.search_item -> {
                val intent = Intent(this, SearchPropertyActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.reset_search_item -> {
                viewModel.resetSearch()
                true
            }
            R.id.logout_item -> {
                googleSignInClient.signOut().addOnCompleteListener {
                    val intent = Intent(this, AuthActivity::class.java)
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.login_out),
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(intent)
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkPermission(this)
        viewModel.onConfigurationChanged(resources.getBoolean(R.bool.isTablet))
    }
}