package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

@Serializable
data class Flat(
    val id: Int,
    val number: String = "",  // e.g., "1A", "1B", "1C"
    val floor: Int = 0,       // The floor number (e.g., 1, 2, 3)
    val type: String = "",    // e.g., "2BHK", "3BHK"
    val area: Double = 0.0,   // Area in square feet or square meters
    val status: String = ""   // e.g., "occupied", "vacant"
) {
    constructor() : this(
        id = 0,
        number = "",
        floor = 0,
        type = "",
        area = 0.0,
        status = ""
    )
}
