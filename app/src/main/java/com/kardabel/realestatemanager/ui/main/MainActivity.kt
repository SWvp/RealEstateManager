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
import com.kardabel.realestatemanager.ui.map.MapActivity
import com.kardabel.realestatemanager.ui.properties.PropertiesFragment
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

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.propertiesListContainer.id, PropertiesFragment())
                .commitNow()
        }

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

        // This single live event is trigger when the device is not in tablet mode
        viewModel.navigationSingleLiveEvent.observe(this) {
            when (it) {
                NavigateViewAction.NavigateToDetailActivity -> startActivity(Intent(this, DetailsActivity::class.java))
            }
        }


        binding.fab.setOnClickListener {
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