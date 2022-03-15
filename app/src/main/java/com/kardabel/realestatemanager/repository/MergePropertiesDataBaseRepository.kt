package com.kardabel.realestatemanager.repository

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.BitmapFactory
import android.os.Environment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kardabel.realestatemanager.R
import com.kardabel.realestatemanager.database.PropertiesDao
import com.kardabel.realestatemanager.firestore.SendPropertyToFirestore
import com.kardabel.realestatemanager.model.PhotoEntity
import com.kardabel.realestatemanager.model.PropertyEntity
import com.kardabel.realestatemanager.utils.ImageStoreManager
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.File.createTempFile
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
    private val firebaseStorage: FirebaseStorage,
    private val context: Application,
) {

    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun synchronisePropertiesDataBases() {

        val roomProperties: List<PropertyEntity> = propertiesDao.getProperties()
        val roomPropertiesToAddToFirestore = mutableListOf<PropertyEntity>()

        // Get all documents
        val propertiesDocuments = firestore.collection("properties").get().await()
        // Convert to data class
        val propertiesFromFirestore = propertiesDocuments.toObjects(PropertyEntity::class.java)

        // Compare all data bases
        if (roomProperties.isNotEmpty() && propertiesFromFirestore.isNotEmpty()) {

            for (propertyFromRoom in roomProperties) {

                val propertyFromFirestore =
                    propertiesFromFirestore.firstOrNull { propertyFromFirestore ->
                        propertyFromFirestore.uid == propertyFromRoom.uid
                                && propertyFromFirestore.propertyCreationDate == propertyFromRoom.propertyCreationDate
                    }
                when {
                    // In case firestore does not have actual property
                    propertyFromFirestore == null -> {
                        roomPropertiesToAddToFirestore.add(propertyFromRoom)
                        propertiesFromFirestore.remove(propertyFromRoom)

                    }
                    // In case user does not have internet connection
                    propertyFromFirestore.updateTimestamp < propertyFromRoom.updateTimestamp -> {
                        updateFirestoreProperties(propertyFromRoom)
                        // TODO update firestore photo
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
        if (roomPropertiesToAddToFirestore.isNotEmpty()) {

            createFirestoreProperties(roomPropertiesToAddToFirestore)

        }

        if (propertiesFromFirestore.isNotEmpty()) {

            val finalProperty = mutableListOf<PropertyEntity>()

            for (property in propertiesFromFirestore) {
                if (property.interest?.isEmpty() == true) {
                    property.interest = null
                }
                finalProperty.add(property)
            }

            insertPropertiesInLocalDataBase(finalProperty)
        }
    }

    private suspend fun insertPropertiesInLocalDataBase(propertiesFromFirestore: List<PropertyEntity>) {
        for (property in propertiesFromFirestore) {
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
                    purchaseDate = null,
                    interest = interestCanBeNull(property.interest),
                    staticMap = property.staticMap,
                    updateTimestamp = property.updateTimestamp,

                    )
            )
            val uid = property.uid
            createRoomPhotoWithPropertyId(newPropertyId, property.propertyCreationDate, uid)

        }
    }

    private suspend fun insertProperty(property: PropertyEntity): Long {
        return propertiesRepository.insertProperty(property)
    }

    private fun createFirestoreProperties(propertyList: List<PropertyEntity>) {

        for (property in propertyList) {
            sendPropertyToFirestore.createPropertyDocument(property)
        }
    }

    private fun updateFirestoreProperties(property: PropertyEntity) {

        sendPropertyToFirestore.updatePropertyDocumentFromRoom(property)

    }

    private suspend fun updateRoomProperty(property: PropertyEntity, propertyId: Long) {
        propertiesDao.updateProperty(
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
                purchaseDate = null,
                interest = interestCanBeNull(property.interest),
                staticMap = property.staticMap,
                updateTimestamp = property.updateTimestamp,
                propertyId = propertyId

            )
        )
    }

    private fun interestCanBeNull(interests: List<String>?): List<String>? {
        return if (interests != null) {
            if (interests.isEmpty()) {
                null
            } else {
                interests
            }
        } else {
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

        propertiesDao.deleteAllPropertyPhotos(propertyId)

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

        val photoTimestamp = photo.name

        val photoFile: File = createImageFile()

        photo.getFile(photoFile).await()

        val img = BitmapFactory.decodeFile(photoFile.absolutePath)
        ImageStoreManager.saveToInternalStorage(context, img, photoTimestamp)

        val meta = photo.metadata.await()
        val photoDescription = meta.getCustomMetadata("photoDescription").toString()

        val photoEntity = PhotoEntity(
            photoUri = photoFile.absolutePath,
            photoDescription = photoDescription,
            propertyOwnerId = propertyId,
            photoTimestamp = photoTimestamp,
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

    private suspend fun sendPhotoToLocalDataBase(photos: PhotoEntity) =
        propertiesDao.insertPhoto(photos)
}