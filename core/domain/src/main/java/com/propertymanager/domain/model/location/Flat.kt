package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

@Serializable
data class Flat(
    val id: Int,
    val societyId: Int,
    val blockId: Int?,
    val towerId: Int?,
    val number: String = "",
    val floor: Int = 0,
    val type: String = "",
    val area: Double = 0.0,
    val status: String = ""
) {
    constructor() : this(
        id = 0,
        societyId = 0,
        blockId = null,
        towerId = null,
        number = "",
        floor = 0,
        type = "",
        area = 0.0,
        status = ""
    )
}
