package com.kardabel.realestatemanager.ui.create

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kardabel.realestatemanager.databinding.ActivityCreatePropertyBinding

class CreatePropertyActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCreatePropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}