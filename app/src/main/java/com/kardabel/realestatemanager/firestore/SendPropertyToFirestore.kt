package com.kardabel.realestatemanager.firestore

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.model.PropertyUpdate
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class SendPropertyToFirestore @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
) {

    fun createPropertyDocument(property: PropertyEntity) {

        if (firebaseAuth.currentUser != null) {

            getCollectionReference().document(property.uid + property.propertyCreationDate)
                .set(getMappedProperty(property))
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }

    fun updatePropertyDocumentFromEditView(
        property: PropertyUpdate,
        createLocalDateTime: String,
        dateToFormat: String
    ) {

        val newProperty: PropertyEntity =
            createPropertyEntity(property, createLocalDateTime, dateToFormat)

        getCollectionReference().document(newProperty.uid + newProperty.propertyCreationDate)
            .update(getMappedProperty(newProperty))
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    private fun createPropertyEntity(
        property: PropertyUpdate,
        createLocalDateTime: String,
        dateToFormat: String
    ): PropertyEntity {
        return PropertyEntity(
            address = property.address,
            apartmentNumber = property.apartmentNumber,
            city = property.city,
            zipcode = property.zipcode,
            county = property.county,
            country = property.country,
            propertyDescription = property.propertyDescription,
            type = property.type.toString(),
            price = property.price,
            surface = property.surface,
            room = property.room,
            bedroom = property.bedroom,
            bathroom = property.bathroom,
            uid = firebaseAuth.currentUser!!.uid,
            vendor = firebaseAuth.currentUser!!.displayName.toString(),
            staticMap = property.staticMap,
            propertyCreationDate = createLocalDateTime,
            creationDateToFormat = dateToFormat,
            saleStatus = property.saleStatus,
            purchaseDate = property.purchaseDate,
            interest = property.interest,
            updateTimestamp = property.updateTimestamp
        )
    }

    fun updatePropertyDocumentFromRoom(
        property: PropertyEntity,
    ) {

        getCollectionReference().document(property.uid + property.propertyCreationDate)
            .update(getMappedProperty(property))
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    private fun getMappedProperty(property: PropertyEntity): HashMap<String, Any> {

        return hashMapOf(
            "address" to property.address,
            "apartment_number" to property.apartmentNumber,
            "city" to property.city,
            "zipcode" to property.zipcode,
            "county" to property.county,
            "country" to property.country,
            "property_description" to property.propertyDescription,
            "type" to property.type,
            "price" to property.price,
            "surface" to property.surface,
            "room" to property.room,
            "bedroom" to property.bedroom,
            "bathroom" to property.bathroom,
            "uid" to property.uid,
            "vendor" to property.vendor,
            "staticMap" to property.staticMap,
            "propertyCreationDate" to property.propertyCreationDate,
            "creationDateToFormat" to property.creationDateToFormat,
            "saleStatus" to property.saleStatus,
            "purchaseDate" to property.purchaseDate.toString(),
            "interest" to interestsCantBeNull(property.interest),
            "updateTimestamp" to property.updateTimestamp,
        )

    }

    private fun interestsCantBeNull(interest: List<String>?): ArrayList<String> {

        return if (interest == null) {
            ArrayList()
        } else {
            interest as ArrayList<String>
        }
    }

    suspend fun updatePropertyWhenSold(
        uid: String,
        propertyCreationDate: String
    ) {

        getCollectionReference().document(uid + propertyCreationDate)
            .update("saleStatus" , "Sold").await()

    }

    private fun getCollectionReference(): CollectionReference {
        return firebaseFirestore.collection("properties")
    }
}