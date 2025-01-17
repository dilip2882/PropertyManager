package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

@Serializable
data class Block(
    val id: Int,
    val societyId: Int,
    val name: String = "",
    val type: String = "",
) {
    constructor() : this(
        id = 0,
        societyId = 0,
        name = "",
        type = "",
    )
}
