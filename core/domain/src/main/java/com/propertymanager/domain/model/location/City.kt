package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Int,
    val name: String = "",
    val latitude: String = "",
    val longitude: String = ""
) {
    constructor() : this(
        id = 0,
        name = "",
        latitude = "",
        longitude = ""
    )
}
