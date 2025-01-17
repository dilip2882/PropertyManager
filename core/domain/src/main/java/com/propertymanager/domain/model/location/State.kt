package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

@Serializable
data class State(
    val id: Int,
    val countryId: Int,
    val name: String = "",
    val stateCode: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val type: String = ""
) {
    constructor() : this(
        id = 0,
        countryId = 0,
        name = "",
        stateCode = "",
        latitude = "",
        longitude = "",
        type = ""
    )
}
