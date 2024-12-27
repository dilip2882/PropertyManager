package com.propertymanager.domain.model.category

import kotlinx.serialization.Serializable

@Serializable
data class State(
    val id: Int,
    val name: String,
    val state_code: String,
    val latitude: String,
    val longitude: String,
    val type: String,
    val cities: List<City>
)
