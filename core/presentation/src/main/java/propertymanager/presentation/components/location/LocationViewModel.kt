package propertymanager.presentation.components.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.domain.usecase.LocationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases,
) : ViewModel() {

    private val _state = MutableStateFlow(LocationState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadCountries()
    }

    private fun loadCountries() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                locationUseCases.getCountries().collect { countries ->
                    _state.update {
                        it.copy(
                            countries = countries,
                            isLoading = false,
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load countries"))
            }
        }
    }


    fun onEvent(event: LocationEvent) {
        when (event) {
            is LocationEvent.ResetLocations -> {
                _state.value = state.value.copy(
                    selectedState = null,
                    selectedCity = null,
                    selectedSociety = null,
                    states = emptyList(),
                    cities = emptyList(),
                    societies = emptyList(),
                    flats = emptyList()
                )
            }
            is LocationEvent.ResetFromState -> {
                _state.value = state.value.copy(
                    selectedCity = null,
                    selectedSociety = null,
                    cities = emptyList(),
                    societies = emptyList(),
                    flats = emptyList()
                )
            }
            is LocationEvent.ResetFromCity -> {
                _state.value = state.value.copy(
                    selectedSociety = null,
                    societies = emptyList(),
                    flats = emptyList()
                )
            }
            is LocationEvent.SelectCountry -> {
                _state.update { it.copy(
                    selectedCountry = event.country,
                    selectedState = null,
                    selectedCity = null,
                    selectedSociety = null,
                    selectedBlock = null,
                    selectedTower = null,
                    selectedFlat = null,
                    states = emptyList(),
                    cities = emptyList(),
                    societies = emptyList(),
                    blocks = emptyList(),
                    towers = emptyList(),
                    flats = emptyList()
                ) }
                loadStatesForCountry(event.country.id)
            }

            is LocationEvent.SelectState -> {
                _state.update { it.copy(
                    selectedState = event.state,
                    selectedCity = null,
                    selectedSociety = null,
                    selectedBlock = null,
                    selectedTower = null,
                    selectedFlat = null,
                    cities = emptyList(),
                    societies = emptyList(),
                    blocks = emptyList(),
                    towers = emptyList(),
                    flats = emptyList()
                ) }
                loadCitiesForState(event.state.id)
            }

            is LocationEvent.SelectCity -> {
                _state.update { it.copy(
                    selectedCity = event.city,
                    selectedSociety = null,
                    selectedBlock = null,
                    selectedTower = null,
                    selectedFlat = null,
                    societies = emptyList(),
                    blocks = emptyList(),
                    towers = emptyList(),
                    flats = emptyList()
                ) }
                loadSocietiesForCity(event.city.id)
            }

            is LocationEvent.SelectSociety -> {
                _state.update { it.copy(
                    selectedSociety = event.society,
                    selectedBlock = null,
                    selectedTower = null,
                    selectedFlat = null,
                    blocks = emptyList(),
                    towers = emptyList(),
                    flats = emptyList()
                ) }
                loadFlatsForSociety(event.society.id)
            }

            is LocationEvent.SelectBlock -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        selectedBlock = event.block,
                        selectedTower = null,
                        flats = emptyList()
                    ) }
                    locationUseCases.getFlatsForBlock(event.block.id).collect { flats ->
                        _state.update { it.copy(flats = flats) }
                    }
                }
            }

            is LocationEvent.SelectTower -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        selectedTower = event.tower,
                        selectedBlock = null,
                        flats = emptyList()
                    ) }
                    locationUseCases.getFlatsForTower(event.tower.id).collect { flats ->
                        _state.update { it.copy(flats = flats) }
                    }
                }
            }

            is LocationEvent.SelectFlat -> {
                _state.update { it.copy(selectedFlat = event.flat) }
            }

            is LocationEvent.GetStatesForCountry -> {
                viewModelScope.launch {
                    try {
                        _state.update { it.copy(isLoading = true) }
                        locationUseCases.getStatesForCountry(event.countryId).collect { states ->
                            _state.update {
                                it.copy(
                                    states = states,
                                    isLoading = false,
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load states"))
                    }
                }
            }

            is LocationEvent.GetCitiesForState -> {
                viewModelScope.launch {
                    try {
                        _state.update { it.copy(isLoading = true) }
                        locationUseCases.getCitiesForState(event.stateId).collect { cities ->
                            _state.update {
                                it.copy(
                                    cities = cities,
                                    isLoading = false,
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load cities"))
                    }
                }
            }

            is LocationEvent.GetSocietiesForCity -> {
                viewModelScope.launch {
                    try {
                        _state.update { it.copy(isLoading = true) }
                        locationUseCases.getSocietiesForCity(event.cityId).collect { societies ->
                            _state.update {
                                it.copy(
                                    societies = societies,
                                    isLoading = false,
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load societies"))
                    }
                }
            }

            is LocationEvent.GetBlocksForSociety -> {
                viewModelScope.launch {
                    try {
                        _state.update { it.copy(isLoading = true) }
                        locationUseCases.getBlocksForSociety(event.societyId).collect { blocks ->
                            _state.update {
                                it.copy(
                                    blocks = blocks,
                                    isLoading = false,
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load blocks"))
                    }
                }
            }

            is LocationEvent.GetTowersForSociety -> {
                viewModelScope.launch {
                    try {
                        _state.update { it.copy(isLoading = true) }
                        locationUseCases.getTowersForSociety(event.societyId).collect { towers ->
                            _state.update {
                                it.copy(
                                    towers = towers,
                                    isLoading = false,
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load towers"))
                    }
                }
            }

            is LocationEvent.GetFlatsForSociety -> {
                viewModelScope.launch {
                    try {
                        _state.update { it.copy(isLoading = true) }
                        locationUseCases.getFlatsForSociety(event.societyId).collect { flats ->
                            _state.update {
                                it.copy(
                                    flats = flats,
                                    isLoading = false,
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load flats"))
                    }
                }
            }

            is LocationEvent.GetFlatsForBlock -> {
                viewModelScope.launch {
                    try {
                        _state.update { it.copy(isLoading = true) }
                        locationUseCases.getFlatsForBlock(event.blockId).collect { flats ->
                            _state.update {
                                it.copy(
                                    flats = flats,
                                    isLoading = false,
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load flats"))
                    }
                }
            }

            is LocationEvent.GetFlatsForTower -> {
                viewModelScope.launch {
                    try {
                        _state.update { it.copy(isLoading = true) }
                        locationUseCases.getFlatsForTower(event.towerId).collect { flats ->
                            _state.update {
                                it.copy(
                                    flats = flats,
                                    isLoading = false,
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load flats"))
                    }
                }
            }
        }
    }

    fun loadStatesForCountry(countryId: Int) {
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
                            isLoading = false,
                        )
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: Error fetching states - ${e.message}")
                println("DEBUG: Stack trace - ${e.stackTraceToString()}")
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadCitiesForState(stateId: Int) {
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
            }
        }
    }

    fun loadSocietiesForCity(cityId: Int) {
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
            }

        }
    }

    fun loadBlocksAndTowersForSociety(societyId: Int) {
        viewModelScope.launch {
            try {
                locationUseCases.getBlocksForSociety(societyId).collect { blocks ->
                    _state.update { it.copy(blocks = blocks) }
                }
                locationUseCases.getTowersForSociety(societyId).collect { towers ->
                    _state.update { it.copy(towers = towers) }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadFlatsForBlock(blockId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getFlatsForBlock(blockId).collect { flats ->
                    _state.update { it.copy(
                        flats = flats,
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadFlatsForTower(towerId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getFlatsForTower(towerId).collect { flats ->
                    _state.update { it.copy(
                        flats = flats,
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadFlatsForSociety(societyId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getFlatsForSociety(societyId).collect { flats ->
                    _state.update { it.copy(
                        flats = flats,
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

}
