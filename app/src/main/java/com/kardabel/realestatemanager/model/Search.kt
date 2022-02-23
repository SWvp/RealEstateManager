package com.kardabel.realestatemanager.model


data class Search(

    val priceMin: String?,
    val priceMax: String?,

    val surfaceMin: String?,
    val surfaceMax: String?,

    val roomMin: String?,
    val roomMax: String?,

    val photo: String?,

    val propertyType: String?,

    val interest: List<String>?,

    val county: String?,

)
