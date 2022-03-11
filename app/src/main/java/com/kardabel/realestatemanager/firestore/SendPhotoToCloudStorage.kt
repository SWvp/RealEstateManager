package com.kardabel.realestatemanager.firestore

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storageMetadata
import com.kardabel.realestatemanager.model.PhotoEntity
import java.io.File
import javax.inject.Inject


class SendPhotoToCloudStorage @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
) {

    fun createPhotoDocument(photos: List<PhotoEntity>) {

        for (photo in photos) {

            val file = Uri.fromFile(File(photo.photoUri))


            val metadata = storageMetadata {
                contentType = "image/jpg"
                setCustomMetadata("photoDescription", photo.photoDescription)
            }

            getPhotoReference(firebaseAuth.uid, photo.photoCreationDate, photo.photoTimestamp)
                .putFile(
                    file,
                    metadata
                )
                .addOnSuccessListener {
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot successfully written!"
                    )
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
        }
    }

    private fun getPhotoReference(
        uid: String?,
        photoCreationDate: String,
        photoTimestamp: String
    ): StorageReference {
        return firebaseStorage.reference.child("photos/$uid/$photoCreationDate/$photoTimestamp")
    }

    fun updatePhotoOnCloudStorage(
        photos: List<PhotoEntity>,
        createLocalDateTime: String,
        uid: String
    ) {

        deleteOldDocument(createLocalDateTime, uid)

        createPhotoDocument(photos)

    }

    private fun deleteOldDocument(createLocalDateTime: String, uid: String) {


        firebaseStorage.reference.child("photos/$uid/$createLocalDateTime").listAll()
            .addOnSuccessListener { (items, prefixes) ->
                prefixes.forEach { _ ->
                    // All the prefixes under listRef.
                    // You may call listAll() recursively on them.
                }

                items.forEach { photo ->

                    photo.delete()

                }
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }
    }
}