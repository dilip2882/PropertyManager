package propertymanager.core.network.demo

import propertymanager.core.network.model.CityData
import propertymanager.core.network.model.CountryData
import propertymanager.core.network.model.StateData

interface LocationDataSource {
    suspend fun getCountries(): List<CountryData>
    suspend fun getStatesForCountry(countryId: Int): List<StateData>
    suspend fun getCitiesForState(stateId: Int): List<CityData>
}
