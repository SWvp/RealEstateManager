package com.kardabel.realestatemanager

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.kardabel.realestatemanager.database.PropertiesRoomDatabase
import com.kardabel.realestatemanager.model.PropertyEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.lang.IllegalArgumentException


class RealEstateContentProvider: ContentProvider() {

    companion object {
        const val AUTHORITY: String = "com.kardabel.realestatemanager.provider"
        val TABLE_REAL_ESTATE: String = PropertyEntity::class.java.simpleName
    }


    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        if (context != null) {
            val idRealEstate = ContentUris.parseId(uri)
            val cursor: Cursor =
                PropertiesRoomDatabase.getDatabase(context!!)
                    .propertiesDao().getPropertiesWithCursor(idRealEstate)
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor

        }
        throw IllegalArgumentException("Failed to query row for uri -> $uri")
    }

    override fun getType(uri: Uri): String {
        return "vnd.android.cursor.item/$AUTHORITY.$TABLE_REAL_ESTATE"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}