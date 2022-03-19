package com.kardabel.realestatemanager.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import com.kardabel.realestatemanager.model.PhotoEntity
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject


class SendPhotoToCloudStorageRepository @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
) {

    suspend fun createPhotoDocument(photos: List<PhotoEntity>, uid: String) {

        for (photo in photos) {

            val file = Uri.fromFile(File(photo.photoUri))


            val metadata = storageMetadata {
                contentType = "image/jpg"
                setCustomMetadata("photoDescription", photo.photoDescription)
            }

            getPhotoReference(uid, photo.photoCreationDate, photo.photoTimestamp)
                .putFile(
                    file,
                    metadata
                ).await()

        }
    }

    private fun getPhotoReference(
        uid: String?,
        photoCreationDate: String,
        photoTimestamp: String
    ): StorageReference {
        return firebaseStorage.reference.child("photos/$uid/$photoCreationDate/$photoTimestamp")
    }

    suspend fun updatePhotoOnCloudStorage(
        photos: List<PhotoEntity>,
        createLocalDateTime: String,
        uid: String
    ) {

        deleteOldDocument(createLocalDateTime, uid)

        createPhotoDocument(photos, uid)

    }

    private suspend fun deleteOldDocument(createLocalDateTime: String, uid: String) {


        val photosFromCloudStorage = firebaseStorage.reference.child("photos/$uid/$createLocalDateTime").listAll().await()

        for (photo in photosFromCloudStorage.items) {

            photo.delete()
        }


    }
}