package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

@Serializable
data class Society(
    val id: Int,
    val countryId: Int,
    val stateId: Int,
    val cityId: Int,
    val name: String = "",
    val latitude: String = "",
    val longitude: String = "",
) {
    constructor() : this(
        id = 0,
        countryId = 0,
        stateId = 0,
        cityId = 0,
        name = "",
        latitude = "",
        longitude = ""
    )
}
