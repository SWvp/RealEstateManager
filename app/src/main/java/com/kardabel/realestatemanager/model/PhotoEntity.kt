package com.kardabel.realestatemanager.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo")
data class PhotoEntity(
    @ColumnInfo(name = "photo_list") val photoList: Bitmap,
    @ColumnInfo(name = "photo_description") val photoDescription: String,
    @ColumnInfo(name = "property_owner_id") val propertyOwnerId: Int,
    @PrimaryKey(autoGenerate = true) val photoId: Int = 0,

    )
