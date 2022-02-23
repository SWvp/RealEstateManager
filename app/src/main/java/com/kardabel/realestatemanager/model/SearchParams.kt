package com.kardabel.realestatemanager.model


data class SearchParams(

    val priceRange: IntRange?,
    val surfaceRange: IntRange?,
    val roomRange: IntRange?,

    val photo: Int?,

    val propertyType: String?,

    val interest: List<String>?,

    val county: String?,

)
