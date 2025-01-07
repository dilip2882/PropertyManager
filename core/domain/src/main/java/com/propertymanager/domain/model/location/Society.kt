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
    val blocks: List<Block> = emptyList(),
    val towers: List<Tower> = emptyList(),
    val flats: List<Flat> = emptyList()
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
