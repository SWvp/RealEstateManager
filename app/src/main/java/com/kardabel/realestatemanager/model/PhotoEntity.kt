package com.kardabel.realestatemanager.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo")
data class PhotoEntity constructor(
    @ColumnInfo(name = "photo_uri") val photoUri: String,
    @ColumnInfo(name = "photo_description") var photoDescription: String,
    @ColumnInfo(name = "property_owner_id") var propertyOwnerId: Long?,
    @ColumnInfo(name = "photo_timestamp") var photoTimestamp: String,
    @ColumnInfo(name = "photo_creation_date") var photoCreationDate: String,
    @PrimaryKey(autoGenerate = true) val photoId: Int = 0,
    )
