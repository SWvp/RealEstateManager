package com.kardabel.realestatemanager.repository

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.BitmapFactory
import android.os.Environment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.firestore.SendPropertyToFirestore
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.utils.ImageStoreManager
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MergePropertiesDataBaseRepository @Inject constructor(
    private val propertiesDao: PropertiesDao,
    private val sendPropertyToFirestore: SendPropertyToFirestore,
    private val propertiesRepository: PropertiesRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val context: Application,
) {

    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var currentPhotoPath: String

    suspend fun synchronisePropertiesDataBases() {

        val roomProperties: List<PropertyEntity> = propertiesDao.getProperties()

        val propertiesDocuments = firestore.collection("properties").get().await()
        val propertiesFromFirestore = propertiesDocuments.toObjects(PropertyEntity::class.java)

        if (roomProperties.isNotEmpty() && propertiesFromFirestore.isNotEmpty()) {

            for (propertyFromRoom in roomProperties) {

                val propertyFromFirestore =
                    propertiesFromFirestore.firstOrNull { propertyFromFirestore ->
                        propertyFromFirestore.uid == propertyFromRoom.uid
                                && propertyFromFirestore.propertyCreationDate == propertyFromRoom.propertyCreationDate
                    }
                when {
                    propertyFromFirestore == null -> {
                        createFirestoreProperties(propertyFromRoom)
                        propertiesFromFirestore.remove(propertyFromFirestore)

                    }
                    propertyFromFirestore.updateTimestamp < propertyFromRoom.updateTimestamp -> {
                        updateFirestoreProperties(propertyFromRoom)
                        propertiesFromFirestore.remove(propertyFromFirestore)
                    }
                    else -> {

                        propertiesFromFirestore.remove(propertyFromFirestore)
                    }
                }


                //propertyFromFirestore.updateTimestamp > propertyFromRoom.updateTimestamp -> {

                //    // todo update property
                //}
            }
        }
        if (propertiesFromFirestore.isNotEmpty()) {

            insertPropertiesInLocalDataBase(propertiesFromFirestore)
        }
    }

    private suspend fun insertPropertiesInLocalDataBase(propertiesFromFirestore: List<PropertyEntity>) {
        for (property in propertiesFromFirestore) {
            val newPropertyId = insertProperty(property)
            createPhotoEntityWithPropertyId(newPropertyId, property.propertyCreationDate)

        }
    }

    private suspend fun insertProperty(property: PropertyEntity): Long {
        return propertiesRepository.insertProperty(property)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun createPhotoEntityWithPropertyId(
        newPropertyId: Long,
        createLocalDateTime: String
    ) {

        val uid = firebaseAuth.uid
        val photoListWithPropertyId = mutableListOf<PhotoEntity>()


        val photoDocuments =
            firebaseStorage.reference.child("photos/$uid/$createLocalDateTime").listAll().await()

        for (photo in photoDocuments.items) {

            val photoTimestamp = photo.name

            val photoFile: File = createImageFile()

            firebaseStorage.reference.child("photos/$uid/$createLocalDateTime/$photoTimestamp")
                .getFile(photoFile)
                .addOnSuccessListener {

                    val img = BitmapFactory.decodeFile(photoFile.absolutePath)
                    ImageStoreManager.saveToInternalStorage(context, img, photoTimestamp)

                }

            val meta = photo.metadata.await()
            val photoDescription = meta.getCustomMetadata("photoDescription").toString()

            val photoEntity = PhotoEntity(
                photoUri = currentPhotoPath,
                photoDescription = photoDescription,
                propertyOwnerId = newPropertyId,
                photoTimestamp = photoTimestamp,
                photoCreationDate = createLocalDateTime,
            )
            photoListWithPropertyId.add(photoEntity)

        }

        sendPhotosToLocalDataBase(photoListWithPropertyId)

    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat(context.getString(R.string.date_pattern)).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private suspend fun sendPhotosToLocalDataBase(photos: List<PhotoEntity>) =
        propertiesRepository.insertPhotos(photos)


    private fun createFirestoreProperties(property: PropertyEntity) {

        sendPropertyToFirestore.createPropertyDocument(property)

    }

    private fun updateFirestoreProperties(property: PropertyEntity) {

        sendPropertyToFirestore.updatePropertyDocumentFromRoom(property)

    }
}