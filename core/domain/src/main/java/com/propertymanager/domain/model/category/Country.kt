package com.propertymanager.domain.model.category

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val id: Int,
    val name: String,
    val iso3: String,
    val iso2: String,
    val numeric_code: String,
    val phone_code: String,
    val capital: String,
    val currency: String,
    val currency_name: String,
    val currency_symbol: String,
    val tld: String,
    val native: String,
    val region: String,
    val region_id: Int,
    val subregion: String,
    val subregion_id: Int,
    val nationality: String,
    val latitude: String,
    val longitude: String,
    val emoji: String,
    val emojiU: String,
    val states: List<State>
)
