package com.kardabel.realestatemanager.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class ConnectionTest {

    lateinit var connectivityManager: ConnectivityManager


    @Before
    fun setUp() {
        connectivityManager = findConnectivityManager()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.M], manifest = Config.NONE, application = Application::class)
    fun `Checking internet connection above Android M with success`() {
        val shadowNetworkCapabilities =
            Shadows.shadowOf(connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork))
        //Has Internet
        shadowNetworkCapabilities.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        Assert.assertTrue(Utils.isInternetAvailable(RuntimeEnvironment.getApplication().baseContext))

        //Hasn't Internet
        shadowNetworkCapabilities.removeTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        shadowNetworkCapabilities.removeTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        shadowNetworkCapabilities.addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
        Assert.assertFalse(Utils.isInternetAvailable(RuntimeEnvironment.getApplication().baseContext))

    }

    @Test
    @Config(
        sdk = [Build.VERSION_CODES.LOLLIPOP],
        manifest = Config.NONE,
        application = Application::class
    )
    fun `Checking internet connection below Android M with success`() {
        val shadowNetworkInfo = Shadows.shadowOf(connectivityManager.activeNetworkInfo)
        //Has Internet
        shadowNetworkInfo.setConnectionType(ConnectivityManager.TYPE_MOBILE)
        Assert.assertTrue(Utils.isInternetAvailable(RuntimeEnvironment.application))

        //Hasn't Internet
        shadowNetworkInfo.setConnectionType(ConnectivityManager.TYPE_BLUETOOTH)
        Assert.assertFalse(Utils.isInternetAvailable(RuntimeEnvironment.getApplication().baseContext))

    }

    private fun findConnectivityManager() =
        RuntimeEnvironment.getApplication().baseContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

}