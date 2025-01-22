package propertymanager.presentation.components.location

import com.propertymanager.domain.model.location.*

sealed class LocationEvent {
    data class GetStatesForCountry(val countryId: Int) : LocationEvent()
    data class GetCitiesForState(val stateId: Int) : LocationEvent()
    data class GetSocietiesForCity(val cityId: Int) : LocationEvent()
    data class GetBlocksForSociety(val societyId: Int) : LocationEvent()
    data class GetTowersForSociety(val societyId: Int) : LocationEvent()
    data class GetFlatsForBlock(val blockId: Int) : LocationEvent()
    data class GetFlatsForTower(val towerId: Int) : LocationEvent()
    data class GetFlatsForSociety(val societyId: Int) : LocationEvent()

    data class SelectCountry(val country: Country) : LocationEvent()
    data class SelectState(val state: State) : LocationEvent()
    data class SelectCity(val city: City) : LocationEvent()
    data class SelectSociety(val society: Society) : LocationEvent()
    data class SelectBlock(val block: Block) : LocationEvent()
    data class SelectTower(val tower: Tower) : LocationEvent()
    data class SelectFlat(val flat: Flat) : LocationEvent()
}
