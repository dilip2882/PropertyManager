package propertymanager.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StateData(
    val id: Int,
    val name: String,
    @SerialName("state_code")
    val stateCode: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val type: String? = null,
    val cities: List<CityData> = emptyList()
)

@Serializable
data class CityData(
    val id: Int,
    val name: String,
    val latitude: String? = null,
    val longitude: String? = null
)

@Serializable
data class CountryData(
    val id: Int,
    val name: String,
    val iso3: String,
    val iso2: String,
    @SerialName("phone_code")
    val phoneCode: String,
    val capital: String,
    val currency: String,
    @SerialName("currency_name")
    val currencyName: String? = null,
    @SerialName("currency_symbol")
    val currencySymbol: String? = null,
    val tld: String? = null,
    val native: String? = null,
    val region: String? = null,
    @SerialName("region_id")
    val regionId: Int? = null,
    val subregion: String? = null,
    @SerialName("subregion_id")
    val subregionId: Int? = null,
    val nationality: String? = null,
    val timezones: List<TimezoneData>? = null,
    val translations: Map<String, String>? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val emoji: String? = null,
    val emojiU: String? = null,
    val states: List<StateData> = emptyList()
)

@Serializable
data class TimezoneData(
    val zoneName: String,
    val gmtOffset: Int,
    val gmtOffsetName: String,
    val abbreviation: String,
    val tzName: String
)
