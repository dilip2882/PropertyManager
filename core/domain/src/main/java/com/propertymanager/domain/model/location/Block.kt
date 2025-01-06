package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

@Serializable
data class Block(
    val id: Int,
    val name: String = "",    // e.g., "Block A", "Block B", "Community Hall"
    val type: String = "",    // e.g., "residential", "amenity"
    val towers: List<Tower> = emptyList()
) {
    constructor() : this(
        id = 0,
        name = "",
        type = "",
        towers = emptyList()
    )
}
