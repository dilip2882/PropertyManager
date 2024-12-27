package com.propertymanager.domain.model.category

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Int,
    val name: String,
    val latitude: String,
    val longitude: String
)
