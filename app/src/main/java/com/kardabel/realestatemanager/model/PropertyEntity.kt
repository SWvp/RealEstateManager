package com.kardabel.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property")
data class PropertyEntity(
    @ColumnInfo(name = "address") var address: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "type") var type: String,
    @ColumnInfo(name = "place") var place: String,
    @ColumnInfo(name = "price") var price: Float,
    @PrimaryKey(autoGenerate = true) val id: Int = 0

)
