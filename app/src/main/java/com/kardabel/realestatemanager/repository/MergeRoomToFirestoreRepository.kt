package com.kardabel.realestatemanager.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.firestore.SendPropertyToFirestore
import com.kardabel.realestatemanager.model.PropertyEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MergeRoomToFirestoreRepository @Inject constructor(
    private val propertiesDao: PropertiesDao,
    private val sendPropertyToFirestore: SendPropertyToFirestore,
) {

    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    suspend fun synchroniseRoomToFirestore() {


        val roomProperties: List<PropertyEntity> = propertiesDao.getProperties()
        val firestoreProperties = mutableListOf<PropertyEntity>()

        firestore.collection("properties").addSnapshotListener { value, _ ->

            assert(value != null)


            for (document in value!!.documentChanges) {

                firestoreProperties.add(document.document.toObject(PropertyEntity::class.java))
            }

            if (roomProperties.isNotEmpty()) {

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
        }
        propertiesDao.insertProperties(firestoreProperties)

    }

    private fun createFirestoreProperties(property: PropertyEntity) {

        sendPropertyToFirestore.createPropertyDocument(property)

    }

    private fun updateFirestoreProperties(property: PropertyEntity) {

        sendPropertyToFirestore.updatePropertyDocumentFromRoom(property)

    }
}