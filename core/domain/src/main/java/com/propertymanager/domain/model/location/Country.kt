package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

/*
Countries
└── States
    └── Cities
        └── Societies
            ├── Blocks
            │   └── Flats (if block selected)
            ├── Towers
            │   └── Flats (if tower selected)
            └── Flats (direct society flats)
 */
@Serializable
data class Country(
    val id: Int,
    val name: String = "",
    val iso3: String = "",
    val iso2: String = "",
    val numericCode: String = "",
    val phoneCode: String = "",
    val capital: String = "",
    val currency: String = "",
    val currencyName: String = "",
    val currencySymbol: String = "",
    val tld: String = "",
    val native: String = "",
    val region: String = "",
    val regionId: Int = 0,
    val subregion: String = "",
    val subregionId: Int = 0,
    val nationality: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val emoji: String = "",
    val emojiU: String = ""
) {
    constructor() : this(
        id = 0,
        name = "",
        iso3 = "",
        iso2 = "",
        numericCode = "",
        phoneCode = "",
        capital = "",
        currency = "",
        currencyName = "",
        currencySymbol = "",
        tld = "",
        native = "",
        region = "",
        regionId = 0,
        subregion = "",
        subregionId = 0,
        nationality = "",
        latitude = "",
        longitude = "",
        emoji = "",
        emojiU = ""
    )
}
