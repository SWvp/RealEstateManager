package com.kardabel.realestatemanager.model

import androidx.room.Embedded
import androidx.room.Relation


data class PropertyWithPhoto(
    @Embedded val propertyEntity: PropertyEntity,
    @Relation(
        parentColumn = "propertyId",
        entityColumn = "property_owner_id"
    )
    val photo: List<PhotoEntity>
)
