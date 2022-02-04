package com.kardabel.realestatemanager.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property")
data class PropertyEntity constructor(
    @ColumnInfo(name = "address") var address: String?,
    @ColumnInfo(name = "apartment_number") var apartmentNumber: String?,
    @ColumnInfo(name = "city") var city: String?,
    @ColumnInfo(name = "zipcode") var zipcode: String?,
    @ColumnInfo(name = "county") var county: String?,
    @ColumnInfo(name = "country") var country: String?,
    @ColumnInfo(name = "property_description") var propertyDescription: String?,
    @ColumnInfo(name = "type") var type: String?,
    @ColumnInfo(name = "price") var price: Float?,
    @ColumnInfo(name = "surface") var surface: Double?,
    @ColumnInfo(name = "room") var room: Int?,
    @ColumnInfo(name = "bedroom") var bedroom: Int?,
    @ColumnInfo(name = "bathroom") var bathroom: Int?,
    @ColumnInfo(name = "user_id") var uid: String,
    @ColumnInfo(name = "create_local_date_time") var createLocalDateTime: String?,
    @ColumnInfo(name = "create_date_to_format") var createDateToFormat: String?,
    @ColumnInfo(name = "sale_status") var saleStatus: Boolean,
    @ColumnInfo(name = "purchase_date") var purchaseDate: String?,
    @ColumnInfo(name = "interest") var interest: List<String>?,
    @PrimaryKey(autoGenerate = true) val propertyId: Long = 0

)
