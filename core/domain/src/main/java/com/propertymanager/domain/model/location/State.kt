package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

@Serializable
data class State(
    val id: Int,
    val name: String = "",
    val stateCode: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val type: String = "",
    val cities: List<City> = emptyList()
) {
    constructor() : this(
        id = 0,
        name = "",
        stateCode = "",
        latitude = "",
        longitude = "",
        type = "",
        cities = emptyList()
    )
}
