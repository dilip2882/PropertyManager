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

    // Society
    data class AddSociety(
        val countryId: Int,
        val stateId: Int,
        val cityId: Int,
        val society: Society
    ) : LocationManagerEvent()
    data class UpdateSociety(
        val countryId: Int,
        val stateId: Int,
        val cityId: Int,
        val society: Society
    ) : LocationManagerEvent()
    data class DeleteSociety(val societyId: Int) : LocationManagerEvent()

    // Block
    data class AddBlock(
        val societyId: Int,
        val block: Block
    ) : LocationManagerEvent()
    data class UpdateBlock(
        val societyId: Int,
        val block: Block
    ) : LocationManagerEvent()
    data class DeleteBlock(val blockId: Int) : LocationManagerEvent()

    // Tower
    data class AddTower(
        val societyId: Int,
        val blockId: Int,
        val tower: Tower
    ) : LocationManagerEvent()
    data class UpdateTower(
        val societyId: Int,
        val blockId: Int,
        val tower: Tower
    ) : LocationManagerEvent()
    data class DeleteTower(val towerId: Int) : LocationManagerEvent()

    // Flat
    data class AddFlat(
        val societyId: Int,
        val parentId: Int,  // Can be societyId, blockId, or towerId
        val flat: Flat
    ) : LocationManagerEvent()
    data class UpdateFlat(
        val societyId: Int,
        val parentId: Int,  // Can be societyId, blockId, or towerId
        val flat: Flat
    ) : LocationManagerEvent()
    data class DeleteFlat(val flatId: Int) : LocationManagerEvent()
} 
