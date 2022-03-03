package com.kardabel.realestatemanager.firestore

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.model.PropertyUpdate
import javax.inject.Inject


class SendPropertyToFirestore @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
) {

    fun createPropertyDocument(property: PropertyEntity) {

        if (firebaseAuth.currentUser != null) {

            getCollectionReference(property.uid).document(property.uid + property.createLocalDateTime)
                .set(getMappedProperty(property))
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }

    fun updatePropertyDocument(
        property: PropertyUpdate,
        createLocalDateTime: String,
        dateToFormat: String
    ) {

        val newProperty: PropertyEntity =
            createPropertyEntity(property, createLocalDateTime, dateToFormat)

        getCollectionReference(newProperty.uid).document(newProperty.uid + newProperty.createLocalDateTime)
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
            property.address,
            property.apartmentNumber.toString(),
            property.city,
            property.zipcode,
            property.county.toString(),
            property.country.toString(),
            property.propertyDescription.toString(),
            property.type.toString(),
            property.price,
            property.surface,
            property.room,
            property.bedroom,
            property.bathroom,
            firebaseAuth.currentUser!!.uid,
            firebaseAuth.currentUser!!.displayName.toString(),
            property.staticMap,
            createLocalDateTime,
            dateToFormat,
            property.saleStatus,
            property.purchaseDate,
            property.interest,
            property.propertyId
        )
    }

    private fun interestsCantBeNull(interest: List<String>?): ArrayList<String> {

        return if (interest == null) {
            ArrayList()
        } else {
            interest as ArrayList<String>
        }
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
            "price" to property.price.toString(),
            "surface" to property.surface.toString(),
            "room" to property.room.toString(),
            "bedroom" to property.bedroom.toString(),
            "bathroom" to property.bathroom.toString(),
            "user_id" to property.uid,
            "vendor" to property.vendor,
            "static_map" to property.staticMap,
            "creation_local_date_time" to property.createLocalDateTime,
            "creation_date_to_format" to property.createDateToFormat,
            "on_sale_status" to property.saleStatus,
            "purchase_date" to property.purchaseDate.toString(),
            "interest" to interestsCantBeNull(property.interest),
            "propertyId" to property.propertyId
        )

    }

    private fun getCollectionReference(uid: String): CollectionReference {
        return firebaseFirestore.collection(uid)
    }
}