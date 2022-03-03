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

class SendPhotoToStorage @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
) {


    fun createPhotoDocument(photos: List<PhotoEntity>, createLocalDate: String) {

        for (photo in photos) {

            getPhotoReference(firebaseAuth.uid!!, createLocalDate, photo.photoUri)
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
        photoUri: String
    ): StorageReference {
        return firebaseStorage.reference.child("$uid/$createLocalDate/$photoUri")
    }


}