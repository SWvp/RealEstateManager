package com.kardabel.realestatemanager

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MainApplication() : Application() {


// override fun onCreate() {
//     super.onCreate()
//     synchronizeDataBase()
// }

// private fun synchronizeDataBase() {
//     CoroutineScope(Dispatchers.IO).launch {
//         val workRequest: PeriodicWorkRequest =
//             PeriodicWorkRequest.Builder(
//                 SynchronizeRoomToFirestoreWorker::class.java,
//                 1,
//                 TimeUnit.MINUTES
//             ).build()

//         WorkManager.getInstance(applicationContext).enqueue(workRequest)
//     }


//     // val blurBuilder = OneTimeWorkRequestBuilder<SynchronizeRoomToFirestoreWorker>()
//     // val workManager = WorkManager.getInstance(applicationContext)
//     // val continuation = workManager.beginUniqueWork("start",ExistingWorkPolicy.REPLACE, blurBuilder.build())
//     // continuation.enqueue()

// }

// override fun getWorkManagerConfiguration(): Configuration {
//
// }

}