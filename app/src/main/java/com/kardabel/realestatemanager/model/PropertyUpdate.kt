package com.kardabel.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


// Entity to deal with Room-update
@Entity
data class PropertyUpdate(
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "apartment_number") val apartmentNumber: String?,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "zipcode") val zipcode: String,
    @ColumnInfo(name = "county") val county: String?,
    @ColumnInfo(name = "country") val country: String?,
    @ColumnInfo(name = "property_description") val propertyDescription: String?,
    @ColumnInfo(name = "type") val type: String?,
    @ColumnInfo(name = "price") val price: String?,
    @ColumnInfo(name = "surface") val surface: String?,
    @ColumnInfo(name = "room") val room: String?,
    @ColumnInfo(name = "bedroom") val bedroom: String?,
    @ColumnInfo(name = "bathroom") val bathroom: String?,
    @ColumnInfo(name = "on_sale_status") val saleStatus: String,
    @ColumnInfo(name = "purchase_date") val purchaseDate: String?,
    @ColumnInfo(name = "interest") val interest: List<String>?,
    @ColumnInfo(name = "static_map") val staticMap: String,
    @ColumnInfo(name = "update_timestamp") val updateTimestamp: String,
    @PrimaryKey val propertyId: Long,
)
