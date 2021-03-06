package com.kardabel.realestatemanager.usecase

import android.annotation.SuppressLint
import android.app.Application
import android.os.Environment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.repository.PropertiesRepository
import com.kardabel.realestatemanager.repository.SendPropertyToFirestoreRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.File.createTempFile
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MergePropertiesDataBaseUseCase @Inject constructor(
    private val propertiesRepository: PropertiesRepository,
    private val sendPropertyToFirestoreRepository: SendPropertyToFirestoreRepository,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseFirestore: FirebaseFirestore,
    private val context: Application,
) {

    suspend fun synchronisePropertiesDataBases() {

        supervisorScope {

            val roomProperties: List<PropertyEntity> = propertiesRepository.getProperties()

            // Get all documents
            val propertiesDocuments = firebaseFirestore.collection("properties").get().await()
            // Convert to data class
            val propertiesFromFirestore = propertiesDocuments.toObjects(PropertyEntity::class.java)

            var mergeTask: Deferred<Boolean>? = null

            // Compare all data bases
            for (propertyFromRoom in roomProperties) {

                mergeTask = async {

                    val propertyFromFirestore =
                        propertiesFromFirestore.firstOrNull { propertyFromFirestore ->
                            propertyFromFirestore.uid == propertyFromRoom.uid
                                    && propertyFromFirestore.propertyCreationDate == propertyFromRoom.propertyCreationDate
                        }
                    when {
                        // In case firestore does not have actual property
                        propertyFromFirestore == null -> {
                            createFirestoreProperty(propertyFromRoom)
                            propertiesFromFirestore.remove(propertyFromRoom)

                        }
                        // In case user does not have internet connection during the property creation/edition
                        propertyFromFirestore.updateTimestamp < propertyFromRoom.updateTimestamp -> {
                            updateFirestoreProperties(propertyFromRoom)
                            // TODO update photo in firestore cloud storage
                            propertiesFromFirestore.remove(propertyFromFirestore)
                        }
                        // In case firestore property is newer
                        propertyFromFirestore.updateTimestamp > propertyFromRoom.updateTimestamp -> {

                            updateRoomProperty(propertyFromFirestore, propertyFromRoom.propertyId)
                            updateRoomPhoto(
                                propertyFromRoom.propertyId,
                                propertyFromRoom.uid,
                                propertyFromRoom.propertyCreationDate
                            )
                            propertiesFromFirestore.remove(propertyFromFirestore)
                        }
                        else -> {

                            propertiesFromFirestore.remove(propertyFromFirestore)
                        }
                    }
                }
            }

            // Wait for the end of the merging task
            mergeTask?.join()


            // Then, if firestore list is not empty, replace empty interest list by null
            // and send it to room database
            if (propertiesFromFirestore.isNotEmpty()) {

                for (property in propertiesFromFirestore) {
                    if (property.interest?.isEmpty() == true) {
                        property.interest = null
                    }
                    launch {
                        insertPropertiesInLocalDataBase(property)
                    }
                }
            }
        }
    }

    private suspend fun insertPropertiesInLocalDataBase(property: PropertyEntity) {

        val newPropertyId = insertProperty(
            PropertyEntity(
                address = property.address,
                apartmentNumber = property.apartmentNumber,
                city = property.city,
                zipcode = property.zipcode,
                county = property.county,
                country = property.country,
                propertyDescription = property.propertyDescription,
                type = property.type,
                price = property.price,
                surface = property.surface,
                room = property.room,
                bedroom = property.bedroom,
                bathroom = property.bathroom,
                uid = property.uid,
                vendor = property.vendor,
                propertyCreationDate = property.propertyCreationDate,
                creationDateToFormat = property.creationDateToFormat,
                saleStatus = property.saleStatus,
                purchaseDate = property.purchaseDate,
                interest = interestCanBeNull(property.interest),
                staticMap = property.staticMap,
                updateTimestamp = property.updateTimestamp,

                )
        )

        // In this process, create all photos
        createRoomPhotoWithPropertyId(newPropertyId, property.propertyCreationDate, property.uid)

    }

    private suspend fun insertProperty(property: PropertyEntity): Long {
        return propertiesRepository.insertProperty(property)
    }

    private suspend fun createFirestoreProperty(property: PropertyEntity) {

        sendPropertyToFirestoreRepository.createPropertyDocument(property)

    }

    private fun updateFirestoreProperties(property: PropertyEntity) {

        sendPropertyToFirestoreRepository.updatePropertyDocumentFromRoom(property)

    }

    private suspend fun updateRoomProperty(property: PropertyEntity, propertyId: Long) {
        propertiesRepository.updateProperty(
            PropertyEntity(
                address = property.address,
                apartmentNumber = property.apartmentNumber,
                city = property.city,
                zipcode = property.zipcode,
                county = property.county,
                country = property.country,
                propertyDescription = property.propertyDescription,
                type = property.type,
                price = property.price,
                surface = property.surface,
                room = property.room,
                bedroom = property.bedroom,
                bathroom = property.bathroom,
                uid = property.uid,
                vendor = property.vendor,
                propertyCreationDate = property.propertyCreationDate,
                creationDateToFormat = property.creationDateToFormat,
                saleStatus = property.saleStatus,
                purchaseDate = property.purchaseDate,
                interest = interestCanBeNull(property.interest),
                staticMap = property.staticMap,
                updateTimestamp = property.updateTimestamp,
                propertyId = propertyId

            )
        )
    }

    private fun interestCanBeNull(interests: List<String>?): List<String>? {
        return interests?.ifEmpty {
            null
        }
    }

    private suspend fun createRoomPhotoWithPropertyId(
        newPropertyId: Long,
        propertyCreationDate: String,
        uid: String,
    ) {

        val photoDocuments =
            firebaseStorage.reference.child("photos/$uid/$propertyCreationDate").listAll().await()

        for (photo in photoDocuments.items) {

            createPhoto(photo, newPropertyId, propertyCreationDate)

        }
    }

    private suspend fun updateRoomPhoto(
        propertyId: Long,
        uid: String,
        propertyCreationDate: String
    ) {

        propertiesRepository.deleteAllPropertyPhotos(propertyId)

        val photosFromFirestore =
            firebaseStorage.reference.child("photos/$uid/$propertyCreationDate").listAll().await()

        for (photo in photosFromFirestore.items) {

            createPhoto(photo, propertyId, propertyCreationDate)

        }
    }

    private suspend fun createPhoto(
        photo: StorageReference,
        propertyId: Long,
        propertyCreationDate: String
    ) {

        val photoFile: File = createImageFile()

        photo.getFile(photoFile).await()

        val meta = photo.metadata.await()
        val photoDescription = meta.getCustomMetadata("photoDescription").toString()

        val photoEntity = PhotoEntity(
            photoUri = photoFile.absolutePath,
            photoDescription = photoDescription,
            propertyOwnerId = propertyId,
            photoTimestamp = photo.name,
            photoCreationDate = propertyCreationDate,
        )

        sendPhotoToLocalDataBase(photoEntity)

    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat(context.getString(R.string.date_pattern)).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        return createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private suspend fun sendPhotoToLocalDataBase(photo: PhotoEntity) =
        propertiesRepository.insertPhoto(photo)
}