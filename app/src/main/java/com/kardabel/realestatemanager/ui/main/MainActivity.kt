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
import com.google.firebase.auth.FirebaseAuth
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
import com.kardabel.realestatemanager.utils.NavigateToEditViewAction
import com.kardabel.realestatemanager.utils.ScreenPositionViewAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_CODE = 100

    lateinit var googleSignInClient: GoogleSignInClient

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

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

        // Permission work is doing by viewModel
        viewModel.actionSingleLiveEvent.observe(this) {
            when (it) {
                PermissionViewAction.PERMISSION_ASKED -> ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_CODE
                )
                else -> {
                    val alertDialogBuilder = MaterialAlertDialogBuilder(this@MainActivity)
                    alertDialogBuilder.setTitle("R.string.title_alert")
                    alertDialogBuilder.setMessage("rational")
                    alertDialogBuilder.show()
                }
            }
        }

        // call requestIdToken for the Auth work
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // When this livedata is trigger, check if we are on landscape or portrait to chose the correct view to display
        viewModel.screenPositionSingleLiveEvent.observe(this) {
            when (it) {
                ScreenPositionViewAction.IsLandscapeMode -> startActivity(
                    Intent(
                        this,
                        DetailsActivity::class.java
                    )
                )
            }
        }

        // Single live event trigger when edit item is clicked
        viewModel.startEditActivitySingleLiveEvent.observe(this) {
            when (it) {

                NavigateToEditViewAction.GO_TO_EDIT_PROPERTY ->
                    startActivity(
                        Intent(
                            this,
                            EditPropertyActivity::class.java
                        )
                    )

                NavigateToEditViewAction.NO_PROPERTY_SELECTED ->
                    Toast.makeText(applicationContext, getString(R.string.no_property_selected), Toast.LENGTH_SHORT).show()
            }
        }


        binding.fab.setOnClickListener {
            // viewModel.notifyThisEdit(CreateOrEdit.CREATE_PROPERTY)
            val intent = Intent(
                this@MainActivity,
                CreatePropertyActivity::class.java
            )
            startActivity(intent)
        }
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
                viewModel.propertyIdRepositoryStatus()

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
            R.id.logout_item -> {
                googleSignInClient.signOut().addOnCompleteListener {
                    val intent = Intent(this, AuthActivity::class.java)
                    Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()
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