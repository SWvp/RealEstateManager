package com.kardabel.realestatemanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.firestore.SendPropertyToFirestoreRepository
import dagger.assisted.AssistedInject

@HiltWorker
class SynchronizeRoomToFirestoreWorker @AssistedInject constructor(
    context: Context,
    params: WorkerParameters,
    private val propertiesDao: PropertiesDao,
    private val sendPropertyToFirestoreRepository: SendPropertyToFirestoreRepository,
    private val applicationDispatchers: ApplicationDispatchers,

    ) : CoroutineWorker(
    context,
    params

) {

    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    override suspend fun doWork(): Result {
 //    withContext(applicationDispatchers.ioDispatcher) {

 //        val roomProperties: List<PropertyEntity> = propertiesDao.getProperties()
 //        val firestoreProperties = mutableListOf<PropertyEntity>()

 //        firestore.collection("properties").addSnapshotListener { value, error ->

 //            assert(value != null)
 //            for (document in value!!.documentChanges) {

 //                firestoreProperties.add(document.document.toObject(PropertyEntity::class.java))
 //            }

 //            for (property in roomProperties) {

 //                val firestoreIdMatcher =
 //                    firestoreProperties.firstOrNull { it.uid == property.uid }
 //                if (firestoreIdMatcher == null) {
 //                    createFirestoreProperties(property)
 //                    firestoreProperties.remove(property)

 //                } else if (firestoreIdMatcher.updateTimestamp < property.updateTimestamp) {
 //                    updateFirestoreProperties(property)
 //                    firestoreProperties.remove(property)
 //                }

 //            }
 //        }
 //        propertiesDao.insertProperties(firestoreProperties)
 //    }
        return Result.success()

    }

 // private suspend fun <T> Flow<List<T>>.flattenToList() =
 //     flatMapConcat { it.asFlow() }.toList()

 // private fun createFirestoreProperties(property: PropertyEntity) {

 //     sendPropertyToFirestore.createPropertyDocument(property)

 // }

 // private fun updateFirestoreProperties(property: PropertyEntity) {


 //     sendPropertyToFirestore.updatePropertyDocumentFromRoom(property)

 // }

}
