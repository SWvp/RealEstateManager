package com.kardabel.realestatemanager.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.databinding.ActivityMainBinding
import com.kardabel.realestatemanager.ui.details.DetailsFragment
import com.kardabel.realestatemanager.ui.map.MapFragment
import com.kardabel.realestatemanager.ui.properties.PropertiesFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val propertiesFragment = PropertiesFragment()
        val mapFragment = MapFragment()

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

        val isTablet = resources.getBoolean(R.bool.isTablet)

        when {
            !isTablet -> {

                binding
                    .bottomNavigationView
                    ?.setOnItemSelectedListener {
                        when (it.itemId) {
                            R.id.properties_icon -> setCurrentFragment(propertiesFragment)
                            R.id.map_icon -> setCurrentFragment(mapFragment)


                        }
                        true
                    }
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.properties_list_container, fragment)
            commit()
        }

}