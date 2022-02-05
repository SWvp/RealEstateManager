package com.kardabel.realestatemanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class MainApplication : Application(){


    override fun onCreate() {
        super.onCreate()

        GlobalScope.launch(Dispatchers.IO) {
            // TODO Stephane firestore here !
        }
    }

}