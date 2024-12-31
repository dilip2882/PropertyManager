package propertymanager.feature.staff.settings.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.usecase.LocationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(LocationState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        fetchCountries()
    }

    fun onEvent(event: LocationEvent) {
        when (event) {
            is LocationEvent.SelectCountry -> {
                _state.update {
                    it.copy(
                        selectedCountry = event.country,
                        selectedState = null,
                        selectedCity = null
                    )
                }
                fetchStatesForCountry(event.country.id)
            }
            is LocationEvent.SelectState -> {
                _state.update {
                    it.copy(
                        selectedState = event.state,
                        selectedCity = null
                    )
                }
                fetchCitiesForState(event.state.id)
            }
            is LocationEvent.SelectCity -> {
                _state.update {
                    it.copy(selectedCity = event.city)
                }
            }
        }
    }

    private fun fetchCountries() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getCountries().collect { countries ->
                    _state.update {
                        it.copy(countries = countries, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.emit(UiEvent.Error("Failed to load countries: ${e.message}"))
            }
        }
    }

    private fun fetchStatesForCountry(countryId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getStatesForCountry(countryId).collect { states ->
                    _state.update {
                        it.copy(states = states, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.emit(UiEvent.Error("Failed to load states: ${e.message}"))
            }
        }
    }

    private fun fetchCitiesForState(stateId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getCitiesForState(stateId).collect { cities ->
                    _state.update {
                        it.copy(cities = cities, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.emit(UiEvent.Error("Failed to load cities: ${e.message}"))
            }
        }
    }
}

data class LocationState(
    val countries: List<Country> = emptyList(),
    val states: List<State> = emptyList(),
    val cities: List<City> = emptyList(),
    val selectedCountry: Country? = null,
    val selectedState: State? = null,
    val selectedCity: City? = null,
    val isLoading: Boolean = false
)

sealed class LocationEvent {
    data class SelectCountry(val country: Country) : LocationEvent()
    data class SelectState(val state: State) : LocationEvent()
    data class SelectCity(val city: City) : LocationEvent()
}

sealed class UiEvent {
    data class Success(val message: String) : UiEvent()
    data class Error(val message: String) : UiEvent()
}
