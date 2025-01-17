package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Int,
    val countryId: Int,
    val stateId: Int,
    val name: String = "",
    val latitude: String = "",
    val longitude: String = "",
) {
    constructor() : this(
        id = 0,
        countryId = 0,
        stateId = 0,
        name = "",
        latitude = "",
        longitude = "",
    )
}
