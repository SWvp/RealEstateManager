package com.kardabel.realestatemanager.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kardabel.realestatemanager.databinding.ActivityMapBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

}