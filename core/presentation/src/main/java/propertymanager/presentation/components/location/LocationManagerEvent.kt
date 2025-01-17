package propertymanager.presentation.components.location

import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.model.location.Tower

sealed class LocationManagerEvent {
    object LoadLocations : LocationManagerEvent()

    // Country
    data class AddCountry(val country: Country) : LocationManagerEvent()
    data class UpdateCountry(val country: Country) : LocationManagerEvent()
    data class DeleteCountry(val countryId: Int) : LocationManagerEvent()

    // State
    data class AddState(
        val countryId: Int,
        val state: State
    ) : LocationManagerEvent()
    data class UpdateState(
        val countryId: Int,
        val state: State
    ) : LocationManagerEvent()
    data class DeleteState(val stateId: Int) : LocationManagerEvent()

    // City
    data class AddCity(
        val countryId: Int,
        val stateId: Int,
        val city: City
    ) : LocationManagerEvent()
    data class UpdateCity(
        val countryId: Int,
        val stateId: Int,
        val city: City
    ) : LocationManagerEvent()
    data class DeleteCity(val cityId: Int) : LocationManagerEvent()

    data class AddSociety(val cityId: Int, val society: Society) : LocationManagerEvent()
    data class UpdateSociety(val society: Society) : LocationManagerEvent()
    data class DeleteSociety(val societyId: Int) : LocationManagerEvent()
    
    data class AddBlock(val block: Block) : LocationManagerEvent()
    data class UpdateBlock(val block: Block) : LocationManagerEvent()
    data class DeleteBlock(val blockId: Int) : LocationManagerEvent()

    data class AddTower(val tower: Tower) : LocationManagerEvent()
    data class UpdateTower(val tower: Tower) : LocationManagerEvent()
    data class DeleteTower(val towerId: Int) : LocationManagerEvent()
    
    data class AddFlat(val flat: Flat) : LocationManagerEvent()
    data class UpdateFlat(val flat: Flat) : LocationManagerEvent()
    data class DeleteFlat(val flatId: Int) : LocationManagerEvent()
}
