package com.kardabel.realestatemanager.firestore

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kardabel.realestatemanager.model.PhotoEntity
import java.io.File
import javax.inject.Inject
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2


class SendPhotoToCloudStorage @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
) {


    fun createPhotoDocument(photos: List<PhotoEntity>, createLocalDateTime: String) {

        for (photo in photos) {

            getPhotoReference(firebaseAuth.uid!!, createLocalDateTime, photo.photoTimestamp)
                .putFile(
                    Uri.fromFile(File(photo.photoUri))
                )
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
        }

    }


    private fun getPhotoReference(
        uid: String,
        createLocalDate: String,
        photoTimestamp: Long
    ): StorageReference {
        return firebaseStorage.reference.child("$uid/$createLocalDate/$photoTimestamp")
    }

    private fun getFolderReference(
        uid: String,
        createLocalDate: String,
    ): StorageReference {
        return firebaseStorage.reference.child("$uid/$createLocalDate")
    }


    fun updateDocument(photos: List<PhotoEntity>, createLocalDateTime: String) {

        deleteOldDocument(createLocalDateTime)

        createPhotoDocument(photos, createLocalDateTime)

    }

    private fun deleteOldDocument(createLocalDateTime: String) {

        getFolderReference(firebaseAuth.uid!!, createLocalDateTime).listAll()
            .addOnSuccessListener { (items, prefixes) ->
                prefixes.forEach { prefix ->
                    // All the prefixes under listRef.
                    // You may call listAll() recursively on them.
                }

                items.forEach { item ->
                    item.delete()
                }
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }

    }


}