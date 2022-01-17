package com.kardabel.realestatemanager.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kardabel.realestatemanager.databinding.ActivityMainBinding
import com.kardabel.realestatemanager.ui.details.DetailsFragment
import com.kardabel.realestatemanager.ui.properties.PropertiesFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


    }
}