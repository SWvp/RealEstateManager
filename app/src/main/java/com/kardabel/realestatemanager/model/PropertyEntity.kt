package com.kardabel.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property")
data class PropertyEntity constructor(
    @ColumnInfo(name = "address") val address: String = "",
    @ColumnInfo(name = "apartment_number") val apartmentNumber: String = "",
    @ColumnInfo(name = "city") val city: String = "",
    @ColumnInfo(name = "zipcode") val zipcode: String = "",
    @ColumnInfo(name = "county") val county: String = "",
    @ColumnInfo(name = "country") val country: String = "",
    @ColumnInfo(name = "property_description") val propertyDescription: String = "",
    @ColumnInfo(name = "type") val type: String = "",
    @ColumnInfo(name = "price") val price: String = "",
    @ColumnInfo(name = "surface") val surface: String = "",
    @ColumnInfo(name = "room") val room: String = "",
    @ColumnInfo(name = "bedroom") val bedroom: String = "",
    @ColumnInfo(name = "bathroom") val bathroom: String = "",
    @ColumnInfo(name = "user_id") val uid: String = "",
    @ColumnInfo(name = "vendor") val vendor: String = "",
    @ColumnInfo(name = "static_map") val staticMap: String = "",
    @ColumnInfo(name = "creation_local_date_time") val propertyCreationDate: String = "",
    @ColumnInfo(name = "creation_date_to_format") val creationDateToFormat: String = "",
    @ColumnInfo(name = "on_sale_status") val saleStatus: String = "",
    @ColumnInfo(name = "purchase_date") val purchaseDate: String? = null,
    @ColumnInfo(name = "interest") val interest: List<String>? = null,
    @ColumnInfo(name = "update_timestamp") val updateTimestamp: String = "",
    @PrimaryKey(autoGenerate = true) val propertyId: Long = 0,

    )
