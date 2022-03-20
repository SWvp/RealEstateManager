package com.kardabel.realestatemanager.repository

import android.annotation.SuppressLint
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.kardabel.realestatemanager.model.UserLocation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val client: FusedLocationProviderClient,
) {

    companion object {
        private const val UPDATE_INTERVAL_SECS = 10000L
        private const val FASTEST_UPDATE_INTERVAL_SECS = 2000L
        private const val ZOOM_FOCUS = 15f
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation(): Flow<UserLocation> = callbackFlow {
        val locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(UPDATE_INTERVAL_SECS)
            fastestInterval = TimeUnit.SECONDS.toMillis(FASTEST_UPDATE_INTERVAL_SECS)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val callBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val it = locationResult.lastLocation
                val userLocation = UserLocation(
                    LatLng(
                        it.latitude,
                        it.longitude,

                        ),
                    ZOOM_FOCUS
                )
                trySend(userLocation)
            }
        }

        client.requestLocationUpdates(locationRequest, callBack, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callBack) }
    }
}