package com.kardabel.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interest")
data class InterestEntity(
    @ColumnInfo(name = "interest") var interest: List<String>,
    @PrimaryKey(autoGenerate = true) val interestId: Int = 0,
)
