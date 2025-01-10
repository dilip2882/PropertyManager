package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

@Serializable
data class Tower(
    val id: Int,
    val societyId: Int,
    val blockId: Int,
    val name: String = "",
    val flats: List<Flat> = emptyList()
) {
    constructor() : this(
        id = 0,
        societyId = 0,
        blockId = 0,
        name = "",
        flats = emptyList()
    )
}
