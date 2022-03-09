package com.kardabel.realestatemanager.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.firestore.SendPropertyToFirestore
import com.kardabel.realestatemanager.model.PropertyEntity
import kotlinx.coroutines.tasks.await
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
        // val firestoreProperties = mutableListOf<PropertyEntity>()

        val propertiesDocuments = firestore.collection("properties").get().await()
        val propertiesFromFirestore = propertiesDocuments.toObjects(PropertyEntity::class.java)

        if (roomProperties.isNotEmpty()) {

            for (propertyFromRoom in roomProperties) {

                val property =
                    propertiesFromFirestore.firstOrNull { propertyFromFirestore ->
                        propertyFromFirestore.uid == propertyFromRoom.uid
                    }
                if (property == null) {
                    createFirestoreProperties(propertyFromRoom)
                    propertiesFromFirestore.remove(propertyFromRoom)

                } else if (property.updateTimestamp < propertyFromRoom.updateTimestamp) {
                    updateFirestoreProperties(propertyFromRoom)
                    propertiesFromFirestore.remove(propertyFromRoom)
                }
            }
        }
        //  firestore.collection("properties").addSnapshotListener { value, _ ->
        //      assert(value != null)
        //      for (document in value!!.documentChanges) {
        //          firestoreProperties.add(document.document.toObject(PropertyEntity::class.java))
        //      }
        //      if (roomProperties.isNotEmpty()) {
        //          for (property in roomProperties) {
        //              val firestoreIdMatcher =
        //                  firestoreProperties.firstOrNull { it.uid == property.uid }
        //              if (firestoreIdMatcher == null) {
        //                  createFirestoreProperties(property)
        //                  firestoreProperties.remove(property)
        //              } else if (firestoreIdMatcher.updateTimestamp < property.updateTimestamp) {
        //                  updateFirestoreProperties(property)
        //                  firestoreProperties.remove(property)
        //              }
        //          }
        //      }
        //  }
        propertiesDao.insertProperties(propertiesFromFirestore)

    }

    private fun createFirestoreProperties(property: PropertyEntity) {

        sendPropertyToFirestore.createPropertyDocument(property)

    }

    private fun updateFirestoreProperties(property: PropertyEntity) {

        sendPropertyToFirestore.updatePropertyDocumentFromRoom(property)

    }
}