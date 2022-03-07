package com.kardabel.realestatemanager.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kardabel.realestatemanager.ApplicationDispatchers
import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.firestore.SendPropertyToFirestore
import com.kardabel.realestatemanager.model.PropertyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MergeRoomToFirestoreRepository @Inject constructor(
    private val propertiesDao: PropertiesDao,
    private val sendPropertyToFirestore: SendPropertyToFirestore,
    private val applicationDispatchers: ApplicationDispatchers,
) {

    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    suspend fun synchroniseRoomToFirestore() {


            val roomProperties: List<PropertyEntity> = propertiesDao.getProperties()
            val firestoreProperties = mutableListOf<PropertyEntity>()

            firestore.collection("properties").addSnapshotListener { value, error ->

                assert(value != null)
                for (document in value!!.documentChanges) {

                    firestoreProperties.add(document.document.toObject(PropertyEntity::class.java))
                }

                for (property in roomProperties) {

                    val firestoreIdMatcher =
                        firestoreProperties.firstOrNull { it.uid == property.uid }
                    if (firestoreIdMatcher == null) {
                        createFirestoreProperties(property)
                        firestoreProperties.remove(property)

                    } else if (firestoreIdMatcher.updateTimestamp < property.updateTimestamp) {
                        updateFirestoreProperties(property)
                        firestoreProperties.remove(property)
                    }

                }
            }
            propertiesDao.insertProperties(firestoreProperties)


    }

    private suspend fun <T> Flow<List<T>>.flattenToList() =
        flatMapConcat { it.asFlow() }.toList()

    private fun createFirestoreProperties(property: PropertyEntity) {

        sendPropertyToFirestore.createPropertyDocument(property)

    }

    private fun updateFirestoreProperties(property: PropertyEntity) {


        sendPropertyToFirestore.updatePropertyDocumentFromRoom(property)

    }
}