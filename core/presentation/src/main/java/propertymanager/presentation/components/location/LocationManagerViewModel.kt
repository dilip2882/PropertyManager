package propertymanager.presentation.components.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.model.location.Tower
import com.propertymanager.domain.usecase.LocationUseCases
import com.propertymanager.domain.usecase.location.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationManagerViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases,
) : ViewModel() {

    private val _state = MutableStateFlow(LocationManagerState())
    val state: StateFlow<LocationManagerState> = _state

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        loadLocations()
    }

    fun onEvent(event: LocationManagerEvent) {
        when (event) {
            is LocationManagerEvent.LoadLocations -> loadLocations()

            // Country
            is LocationManagerEvent.AddCountry -> addCountry(event.country)
            is LocationManagerEvent.UpdateCountry -> updateCountry(event.country)
            is LocationManagerEvent.DeleteCountry -> deleteCountry(event.countryId)

            // State
            is LocationManagerEvent.AddState -> addState(event.countryId, event.state)
            is LocationManagerEvent.UpdateState -> updateState(event.countryId, event.state)
            is LocationManagerEvent.DeleteState -> deleteState(event.countryId, event.stateId)

            // City
            is LocationManagerEvent.AddCity -> addCity(event.stateId, event.city)
            is LocationManagerEvent.UpdateCity -> updateCity(event.stateId, event.city)
            is LocationManagerEvent.DeleteCity -> deleteCity(event.stateId, event.cityId)

            // Society
            is LocationManagerEvent.AddSociety -> addSociety(event.cityId, event.society)
            is LocationManagerEvent.UpdateSociety -> updateSociety(event.cityId, event.society)
            is LocationManagerEvent.DeleteSociety -> deleteSociety(event.cityId, event.societyId)

            // Block
            is LocationManagerEvent.AddBlock -> addBlock(event.societyId, event.block)
            is LocationManagerEvent.UpdateBlock -> updateBlock(event.societyId, event.block)
            is LocationManagerEvent.DeleteBlock -> deleteBlock(event.societyId, event.blockId)

            // Tower
            is LocationManagerEvent.AddTower -> addTower(event.blockId, event.tower)
            is LocationManagerEvent.UpdateTower -> updateTower(event.blockId, event.tower)
            is LocationManagerEvent.DeleteTower -> deleteTower(event.blockId, event.towerId)

            // Flat
            is LocationManagerEvent.AddFlat -> addFlat(event.towerId, event.flat)
            is LocationManagerEvent.UpdateFlat -> updateFlat(event.towerId, event.flat)
            is LocationManagerEvent.DeleteFlat -> deleteFlat(event.towerId, event.flatId)
        }
    }

    private fun loadLocations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getCountries().collect { countries ->
                    _state.update {
                        it.copy(
                            countries = countries,
                            isLoading = false,
                        )
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun addCountry(country: Country) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when (val result = locationUseCases.validateLocation(country, null, null)) {
                    is ValidationResult.Success -> {
                        locationUseCases.addCountry(country)
                            .onSuccess {
                                _uiEvent.emit(UiEvent.Success("Country added successfully"))
                                loadLocations()
                            }
                            .onFailure { handleError(it) }
                    }

                    is ValidationResult.Error -> {
                        _uiEvent.emit(UiEvent.Error(result.message))
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun updateCountry(country: Country) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when (val result = locationUseCases.validateLocation(country, null, null)) {
                    is ValidationResult.Success -> {
                        locationUseCases.updateCountry(country)
                            .onSuccess {
                                _uiEvent.emit(UiEvent.Success("Country updated successfully"))
                                loadLocations()
                            }
                            .onFailure { handleError(it) }
                    }

                    is ValidationResult.Error -> {
                        _uiEvent.emit(UiEvent.Error(result.message))
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun deleteCountry(countryId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.deleteCountry(countryId)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Country deleted successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun addState(countryId: Int, state: State) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when (val result = locationUseCases.validateLocation(null, state, null)) {
                    is ValidationResult.Success -> {
                        locationUseCases.addState(countryId, state)
                            .onSuccess {
                                _uiEvent.emit(UiEvent.Success("State added successfully"))
                                loadLocations()
                            }
                            .onFailure { handleError(it) }
                    }

                    is ValidationResult.Error -> {
                        _uiEvent.emit(UiEvent.Error(result.message))
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun updateState(countryId: Int, state: State) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when (val result = locationUseCases.validateLocation(null, state, null)) {
                    is ValidationResult.Success -> {
                        locationUseCases.updateState(countryId, state)
                            .onSuccess {
                                _uiEvent.emit(UiEvent.Success("State updated successfully"))
                                loadLocations()
                            }
                            .onFailure { handleError(it) }
                    }

                    is ValidationResult.Error -> {
                        _uiEvent.emit(UiEvent.Error(result.message))
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun deleteState(countryId: Int, stateId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.deleteState(countryId, stateId)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("State deleted successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun addCity(stateId: Int, city: City) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when (val result = locationUseCases.validateLocation(null, null, city)) {
                    is ValidationResult.Success -> {
                        locationUseCases.addCity(stateId, city)
                            .onSuccess {
                                _uiEvent.emit(UiEvent.Success("City added successfully"))
                                loadLocations()
                            }
                            .onFailure { handleError(it) }
                    }

                    is ValidationResult.Error -> {
                        _uiEvent.emit(UiEvent.Error(result.message))
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun updateCity(stateId: Int, city: City) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when (val result = locationUseCases.validateLocation(null, null, city)) {
                    is ValidationResult.Success -> {
                        locationUseCases.updateCity(stateId, city)
                            .onSuccess {
                                _uiEvent.emit(UiEvent.Success("City updated successfully"))
                                loadLocations()
                            }
                            .onFailure { handleError(it) }
                    }

                    is ValidationResult.Error -> {
                        _uiEvent.emit(UiEvent.Error(result.message))
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun deleteCity(stateId: Int, cityId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.deleteCity(stateId, cityId)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("City deleted successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    // Society
    private fun addSociety(cityId: Int, society: Society) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.addSociety(cityId, society)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Society added successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun updateSociety(cityId: Int, society: Society) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.updateSociety(cityId, society)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Society updated successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun deleteSociety(cityId: Int, societyId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.deleteSociety(cityId, societyId)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Society deleted successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    // Block
    private fun addBlock(societyId: Int, block: Block) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.addBlock(societyId, block)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Block added successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun updateBlock(societyId: Int, block: Block) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.updateBlock(societyId, block)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Block updated successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun deleteBlock(societyId: Int, blockId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.deleteBlock(societyId, blockId)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Block deleted successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    // Tower
    private fun addTower(blockId: Int, tower: Tower) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.addTower(blockId, tower)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Tower added successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun updateTower(blockId: Int, tower: Tower) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.updateTower(blockId, tower)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Tower updated successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun deleteTower(blockId: Int, towerId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.deleteTower(blockId, towerId)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Tower deleted successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    // Flat
    private fun addFlat(towerId: Int, flat: Flat) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.addFlat(towerId, flat)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Flat added successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun updateFlat(towerId: Int, flat: Flat) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.updateFlat(towerId, flat)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Flat updated successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun deleteFlat(towerId: Int, flatId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.deleteFlat(towerId, flatId)
                    .onSuccess {
                        _uiEvent.emit(UiEvent.Success("Flat deleted successfully"))
                        loadLocations()
                    }
                    .onFailure { handleError(it) }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private suspend fun handleError(e: Throwable) {
        _uiEvent.emit(UiEvent.Error(e.message ?: "An unknown error occurred"))
        _state.update { it.copy(isLoading = false) }
    }
}

data class LocationManagerState(
    val countries: List<Country> = emptyList(),
    val states: List<State> = emptyList(),
    val cities: List<City> = emptyList(),
    val societies: List<Society> = emptyList(),
    val blocks: List<Block> = emptyList(),
    val towers: List<Tower> = emptyList(),
    val flats: List<Flat> = emptyList(),
    val isLoading: Boolean = false,
)

sealed class LocationManagerEvent {
    object LoadLocations : LocationManagerEvent()

    // Country
    data class AddCountry(val country: Country) : LocationManagerEvent()
    data class UpdateCountry(val country: Country) : LocationManagerEvent()
    data class DeleteCountry(val countryId: Int) : LocationManagerEvent()

    // State
    data class AddState(val countryId: Int, val state: State) : LocationManagerEvent()
    data class UpdateState(val countryId: Int, val state: State) : LocationManagerEvent()
    data class DeleteState(val countryId: Int, val stateId: Int) : LocationManagerEvent()

    // City
    data class AddCity(val stateId: Int, val city: City) : LocationManagerEvent()
    data class UpdateCity(val stateId: Int, val city: City) : LocationManagerEvent()
    data class DeleteCity(val stateId: Int, val cityId: Int) : LocationManagerEvent()

    // Society
    data class AddSociety(val cityId: Int, val society: Society) : LocationManagerEvent()
    data class UpdateSociety(val cityId: Int, val society: Society) : LocationManagerEvent()
    data class DeleteSociety(val cityId: Int, val societyId: Int) : LocationManagerEvent()

    // Block
    data class AddBlock(val societyId: Int, val block: Block) : LocationManagerEvent()
    data class UpdateBlock(val societyId: Int, val block: Block) : LocationManagerEvent()
    data class DeleteBlock(val societyId: Int, val blockId: Int) : LocationManagerEvent()

    // Tower
    data class AddTower(val blockId: Int, val tower: Tower) : LocationManagerEvent()
    data class UpdateTower(val blockId: Int, val tower: Tower) : LocationManagerEvent()
    data class DeleteTower(val blockId: Int, val towerId: Int) : LocationManagerEvent()

    // Flat
    data class AddFlat(val towerId: Int, val flat: Flat) : LocationManagerEvent()
    data class UpdateFlat(val towerId: Int, val flat: Flat) : LocationManagerEvent()
    data class DeleteFlat(val towerId: Int, val flatId: Int) : LocationManagerEvent()
}
