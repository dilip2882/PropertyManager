package com.propertymanager.domain.model.location

import kotlinx.serialization.Serializable

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
    val emojiU: String = "",
    val states: List<State> = emptyList()
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
        emojiU = "",
        states = emptyList()
    )
}

/*
locations/
├── countries/
│   ├── {countryId}/
│   │   └── states/
│   │       ├── {stateId}/
│   │       │   └── cities/
│   │       │       ├── {cityId}/
│   │       │       │   └── societies/
│   │       │       │       ├── flats/
│   │       │       │       ├── {societyId}/
│   │       │       │       │   ├── blocks/
│   │       │       │       │   │   ├── {blockId}/
│   │       │       │       │   │   │   └── flats/
│   │       │       │       │   └── towers/
│   │       │       │       │       ├── {towerId}/
│   │       │       │       │       │   └── flats/

 */
