package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

@Serializable
data class Tower(
    val id: Int,
    val name: String = "",    // e.g., "Tower 1"
    val flats: List<Flat> = emptyList()
) {
    constructor() : this(
        id = 0,
        name = "",
        flats = emptyList()
    )
}

