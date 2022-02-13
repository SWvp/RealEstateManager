package com.kardabel.realestatemanager.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    // Convert bitmap to byte array so we can store image in room database
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    // Convert byte array to bitmap so we can retrieve photo from database
    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    // Convert string to list of strings
    @TypeConverter
    fun toListOfStrings(flatStringList: String?): List<String>? {
        return flatStringList?.split(",")
    }

    // Convert list of strings
    @TypeConverter
    fun fromListOfStrings(listOfString: List<String>?): String? {
        return listOfString?.joinToString(",")
    }

}