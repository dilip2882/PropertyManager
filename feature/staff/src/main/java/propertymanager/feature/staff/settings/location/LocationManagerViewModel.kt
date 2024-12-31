package propertymanager.feature.staff.settings.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.usecase.LocationUseCases
import com.propertymanager.domain.usecase.location.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationManagerViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases
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
            is LocationManagerEvent.AddCountry -> addCountry(event.country)
            is LocationManagerEvent.UpdateCountry -> updateCountry(event.country)
            is LocationManagerEvent.DeleteCountry -> deleteCountry(event.countryId)
            is LocationManagerEvent.AddState -> addState(event.countryId, event.state)
            is LocationManagerEvent.UpdateState -> updateState(event.countryId, event.state)
            is LocationManagerEvent.DeleteState -> deleteState(event.countryId, event.stateId)
            is LocationManagerEvent.AddCity -> addCity(event.stateId, event.city)
            is LocationManagerEvent.UpdateCity -> updateCity(event.stateId, event.city)
            is LocationManagerEvent.DeleteCity -> deleteCity(event.stateId, event.cityId)
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
                            isLoading = false
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

    private fun handleError(error: Throwable) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = false) }
            _uiEvent.emit(UiEvent.Error(error.message ?: "An unknown error occurred"))
        }
    }
}

data class LocationManagerState(
    val countries: List<Country> = emptyList(),
    val states: List<State> = emptyList(),
    val cities: List<City> = emptyList(),
    val isLoading: Boolean = false
)

sealed class LocationManagerEvent {
    object LoadLocations : LocationManagerEvent()
    data class AddCountry(val country: Country) : LocationManagerEvent()
    data class UpdateCountry(val country: Country) : LocationManagerEvent()
    data class DeleteCountry(val countryId: Int) : LocationManagerEvent()
    data class AddState(val countryId: Int, val state: State) : LocationManagerEvent()
    data class UpdateState(val countryId: Int, val state: State) : LocationManagerEvent()
    data class DeleteState(val countryId: Int, val stateId: Int) : LocationManagerEvent()
    data class AddCity(val stateId: Int, val city: City) : LocationManagerEvent()
    data class UpdateCity(val stateId: Int, val city: City) : LocationManagerEvent()
    data class DeleteCity(val stateId: Int, val cityId: Int) : LocationManagerEvent()
}
