package propertymanager.presentation.components.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.domain.model.Property
import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.model.location.Tower
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
    private val locationUseCases: LocationUseCases,
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
                viewModelScope.launch {
                    try {
                        println("DEBUG: Country selected - ${event.country.name} (ID: ${event.country.id})")
                        
                        // Clear previous selections first
                        _state.update {
                            it.copy(
                                selectedCountry = event.country,
                                selectedState = null,
                                selectedCity = null,
                                selectedSociety = null,
                                states = emptyList(),
                                cities = emptyList(),
                                societies = emptyList()
                            )
                        }
                        
                        // Then fetch states
                        fetchStatesForCountry(event.country.id)
                    } catch (e: Exception) {
                        println("DEBUG: Error in SelectCountry - ${e.message}")
                        _uiEvent.emit(UiEvent.Error("Error selecting country: ${e.message}"))
                    }
                }
            }

            is LocationEvent.SelectState -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            selectedState = event.state,
                            selectedCity = null,
                            selectedSociety = null,
                            cities = emptyList()
                        )
                    }
                    println("Selected State: ${event.state.name}")
                    fetchCitiesForState(event.state.id)
                }
            }

            is LocationEvent.SelectCity -> {
                _state.update {
                    it.copy(
                        selectedCity = event.city,
                        selectedSociety = null
                    )
                }
                fetchSocietiesForCity(event.city.id)
            }

            is LocationEvent.SelectSociety -> {
                _state.update { it.copy(selectedSociety = event.society) }
                fetchBlocksAndTowersForSociety(event.society.id)
            }

            is LocationEvent.SelectBuildingType -> {
                _state.update {
                    it.copy(
                        selectedBuildingType = event.type,
                        selectedBlock = null,
                        selectedTower = null
                    )
                }
            }

            is LocationEvent.SelectBlock -> {
                _state.update {
                    it.copy(
                        selectedBlock = event.block,
                        selectedTower = null
                    )
                }
            }

            is LocationEvent.SelectTower -> {
                _state.update {
                    it.copy(
                        selectedTower = event.tower,
                        selectedBlock = null
                    )
                }
            }

            is LocationEvent.SelectCountryByName -> {
                viewModelScope.launch {
                    val country = state.value.countries.find { it.name == event.name }
                    country?.let {
                        onEvent(LocationEvent.SelectCountry(it))
                    }
                }
            }

            is LocationEvent.SelectStateByName -> {
                viewModelScope.launch {
                    val state = state.value.states.find { it.name == event.name }
                    state?.let {
                        onEvent(LocationEvent.SelectState(it))
                    }
                }
            }

            is LocationEvent.SelectCityByName -> {
                viewModelScope.launch {
                    val city = state.value.cities.find { it.name == event.name }
                    city?.let {
                        onEvent(LocationEvent.SelectCity(it))
                    }
                }
            }

            is LocationEvent.SelectFlat -> {
                _state.update { it.copy(selectedFlat = event.flat) }
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
            try {
                println("DEBUG: Fetching states for country ID: $countryId")
                _state.update { it.copy(isLoading = true) }
                
                locationUseCases.getStatesForCountry(countryId).collect { states ->
                    println("DEBUG: Received ${states.size} states")
                    states.forEach { state ->
                        println("DEBUG: State - ${state.name} (ID: ${state.id}, CountryID: ${state.countryId})")
                    }
                    _state.update {
                        it.copy(
                            states = states,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: Error fetching states - ${e.message}")
                println("DEBUG: Stack trace - ${e.stackTraceToString()}")
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

    private fun fetchSocietiesForCity(cityId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getSocietiesForCity(cityId).collect { societies ->
                    _state.update {
                        it.copy(societies = societies, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.emit(UiEvent.Error("Failed to load societies: ${e.message}"))
            }

        }
    }

    private fun fetchBlocksAndTowersForSociety(societyId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Fetch both blocks and towers
                locationUseCases.getBlocksForSociety(societyId).collect { blocks ->
                    _state.update { it.copy(blocks = blocks) }
                }
                locationUseCases.getTowersForSociety(societyId).collect { towers ->
                    _state.update { it.copy(towers = towers, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                handleError(e)
            }
        }
    }

    private suspend fun handleError(e: Throwable) {
        _uiEvent.emit(UiEvent.Error(e.message ?: "An unknown error occurred"))
        _state.update { it.copy(isLoading = false) }
    }

    fun loadFlatsForBlock(blockId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getFlatsForBlock(blockId).collect { flats ->
                    _state.update { it.copy(flats = flats, isLoading = false) }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun loadFlatsForTower(towerId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getFlatsForTower(towerId).collect { flats ->
                    _state.update { it.copy(flats = flats, isLoading = false) }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }
}

data class LocationState(
    val countries: List<Country> = emptyList(),
    val states: List<State> = emptyList(),
    val cities: List<City> = emptyList(),
    val societies: List<Society> = emptyList(),
    val blocks: List<Block> = emptyList(),
    val towers: List<Tower> = emptyList(),
    val flats: List<Flat> = emptyList(),
    val selectedCountry: Country? = null,
    val selectedState: State? = null,
    val selectedCity: City? = null,
    val selectedSociety: Society? = null,
    val selectedBuildingType: Property.Building? = null,
    val selectedBlock: Block? = null,
    val selectedTower: Tower? = null,
    val isLoading: Boolean = false,
    val selectedFlat: Flat? = null,
)

sealed class LocationEvent {
    data class SelectCountry(val country: Country) : LocationEvent()
    data class SelectState(val state: State) : LocationEvent()
    data class SelectCity(val city: City) : LocationEvent()
    data class SelectSociety(val society: Society) : LocationEvent()
    data class SelectBuildingType(val type: Property.Building) : LocationEvent()
    data class SelectBlock(val block: Block) : LocationEvent()
    data class SelectTower(val tower: Tower) : LocationEvent()
    data class SelectCountryByName(val name: String) : LocationEvent()
    data class SelectStateByName(val name: String) : LocationEvent()
    data class SelectCityByName(val name: String) : LocationEvent()
    data class SelectFlat(val flat: Flat) : LocationEvent()
}

sealed class UiEvent {
    data class Success(val message: String) : UiEvent()
    data class Error(val message: String) : UiEvent()
}
