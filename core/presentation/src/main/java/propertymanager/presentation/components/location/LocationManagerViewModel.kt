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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationManagerViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(LocationManagerState())
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

                locationUseCases.getCountries()
                    .catch { e ->
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load countries"))
                    }
                    .collect { countries ->
                        _state.update {
                            it.copy(
                                countries = countries,
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load countries"))
            }
        }
    }

    fun onCountrySelected(country: Country) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedCountry = country,
                    selectedState = null,
                    selectedCity = null,
                    selectedSociety = null,
                    selectedBlock = null,
                    selectedTower = null,
                    states = emptyList(),
                    cities = emptyList(),
                    societies = emptyList(),
                    blocks = emptyList(),
                    towers = emptyList(),
                    flats = emptyList()
                )
            }
            try {
                locationUseCases.getStatesForCountry(country.id).collect { states ->
                    _state.update { it.copy(states = states) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun onStateSelected(state: State) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedState = state,
                    selectedCity = null,
                    selectedSociety = null,
                    selectedBlock = null,
                    selectedTower = null
                )
            }
            locationUseCases.getCitiesForState(state.id).collect { cities ->
                _state.update { it.copy(cities = cities) }
            }
        }
    }

    fun onCitySelected(city: City) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedCity = city,
                    selectedSociety = null,
                    selectedBlock = null,
                    selectedTower = null
                )
            }
            locationUseCases.getSocietiesForCity(city.id).collect { societies ->
                _state.update { it.copy(societies = societies) }
            }
        }
    }

    fun onSocietySelected(society: Society) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedSociety = society,
                    selectedBlock = null,
                    selectedTower = null,
                    blocks = emptyList(),
                    towers = emptyList(),
                    flats = emptyList()
                )
            }

            launch {
                locationUseCases.getBlocksForSociety(society.id).collect { blocks ->
                    _state.update { it.copy(blocks = blocks) }
                }
            }

            launch {
                locationUseCases.getTowersForSociety(society.id).collect { towers ->
                    _state.update { it.copy(towers = towers) }
                }
            }

            launch {
                locationUseCases.getFlatsForSociety(society.id).collect { flats ->
                    _state.update { it.copy(flats = flats) }
                }
            }
        }
    }

    fun onBlockSelected(block: Block) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedBlock = block,
                    selectedTower = null,
                    flats = emptyList()
                )
            }
            locationUseCases.getFlatsForBlock(block.id).collect { flats ->
                _state.update { it.copy(flats = flats) }
            }
        }
    }

    fun onTowerSelected(tower: Tower) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedTower = tower,
                    selectedBlock = null,
                    flats = emptyList()
                )
            }
            locationUseCases.getFlatsForTower(tower.id).collect { flats ->
                _state.update { it.copy(flats = flats) }
            }
        }
    }

    fun onEvent(event: LocationManagerEvent) {
        viewModelScope.launch {
            when (event) {
                is LocationManagerEvent.LoadLocations -> {
                    loadInitialData()
                }
                is LocationManagerEvent.AddCountry -> {
                    try {
                        locationUseCases.addCountry(event.country)
//                        loadCountries() // Reload the countries list
                        _uiEvent.send(UiEvent.Success("Country added successfully"))
                    } catch (e: Exception) {
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to add country"))
                    }
                }
                is LocationManagerEvent.UpdateCountry -> {
                    try {
                        locationUseCases.updateCountry(event.country)
//                        loadCountries() // Reload the countries list
                        _uiEvent.send(UiEvent.Success("Country updated successfully"))
                    } catch (e: Exception) {
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to update country"))
                    }
                }
                is LocationManagerEvent.DeleteCountry -> {
                    try {
                        locationUseCases.deleteCountry(event.countryId)
//                        loadCountries() // Reload the countries list
                        _uiEvent.send(UiEvent.Success("Country deleted successfully"))
                    } catch (e: Exception) {
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to delete country"))
                    }
                }
                is LocationManagerEvent.AddState -> {
                    try {
                        locationUseCases.addState(event.state)
                        loadStatesForCountry(event.state.countryId)
                        _uiEvent.send(UiEvent.Success("State added successfully"))
                    } catch (e: Exception) {
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to add state"))
                    }
                }
                is LocationManagerEvent.UpdateState -> {
                    try {
                        locationUseCases.updateState(event.state)
                        loadStatesForCountry(event.state.countryId)
                        _uiEvent.send(UiEvent.Success("State updated successfully"))
                    } catch (e: Exception) {
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to update state"))
                    }
                }
                is LocationManagerEvent.DeleteState -> {
                    try {
                        locationUseCases.deleteState(event.stateId)
                        // Reload states for the current country
                        _state.value.selectedCountryId?.let { loadStatesForCountry(it) }
                        _uiEvent.send(UiEvent.Success("State deleted successfully"))
                    } catch (e: Exception) {
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to delete state"))
                    }
                }
                is LocationManagerEvent.AddCity -> {
                    try {
                        locationUseCases.addCity(event.city)
                        loadCitiesForState(event.city.stateId)
                        _uiEvent.send(UiEvent.Success("City added successfully"))
                    } catch (e: Exception) {
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to add city"))
                    }
                }
                is LocationManagerEvent.UpdateCity -> {
                    try {
                        locationUseCases.updateCity(event.city)
                        loadCitiesForState(event.city.stateId)
                        _uiEvent.send(UiEvent.Success("City updated successfully"))
                    } catch (e: Exception) {
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to update city"))
                    }
                }
                is LocationManagerEvent.DeleteCity -> {
                    try {
                        locationUseCases.deleteCity(event.cityId)
                        // Reload cities for the current state
                        _state.value.selectedStateId?.let { loadCitiesForState(it) }
                        _uiEvent.send(UiEvent.Success("City deleted successfully"))
                    } catch (e: Exception) {
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to delete city"))
                    }
                }
                is LocationManagerEvent.AddSociety -> {
                    locationUseCases.addSociety(event.society)
                    _uiEvent.send(UiEvent.Success("Society added successfully"))
                    loadSocietiesForCity(event.cityId)
                }
                is LocationManagerEvent.UpdateSociety -> {
                    locationUseCases.updateSociety(event.society)
                    _uiEvent.send(UiEvent.Success("Society updated successfully"))
                    event.society.cityId?.let { loadSocietiesForCity(it) }
                }
                is LocationManagerEvent.DeleteSociety -> {
                    locationUseCases.deleteSociety(event.societyId)
                    _uiEvent.send(UiEvent.Success("Society deleted successfully"))
                    _state.value.societies.firstOrNull()?.cityId?.let { loadSocietiesForCity(it) }
                }
                is LocationManagerEvent.AddBlock -> {
                    try {
                        locationUseCases.addBlock(event.block)
                        loadBlocksForSociety(event.block.societyId)
                        _uiEvent.send(UiEvent.Success("Block added successfully"))
                    } catch (e: Exception) {
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to add block"))
                    }
                }
                is LocationManagerEvent.UpdateBlock -> {
                    try {
                        locationUseCases.updateBlock(event.block)
                        loadBlocksForSociety(event.block.societyId)
                        _uiEvent.send(UiEvent.Success("Block updated successfully"))
                    } catch (e: Exception) {
                        _uiEvent.send(UiEvent.Error(e.message ?: "Failed to update block"))
                    }
                }
                is LocationManagerEvent.DeleteBlock -> {
                    locationUseCases.deleteBlock(event.blockId)
                    _uiEvent.send(UiEvent.Success("Block deleted successfully"))
                    _state.value.selectedSociety?.id?.let { loadBlocksForSociety(it) }
                }
                is LocationManagerEvent.AddTower -> {
                    locationUseCases.addTower(event.tower)
                    _uiEvent.send(UiEvent.Success("Tower added successfully"))
                    loadTowersForSociety(event.tower.societyId)
                }
                is LocationManagerEvent.UpdateTower -> {
                    locationUseCases.updateTower(event.tower)
                    _uiEvent.send(UiEvent.Success("Tower updated successfully"))
                    loadTowersForSociety(event.tower.societyId)
                }
                is LocationManagerEvent.DeleteTower -> {
                    locationUseCases.deleteTower(event.towerId)
                    _uiEvent.send(UiEvent.Success("Tower deleted successfully"))
                    _state.value.selectedSociety?.id?.let { loadTowersForSociety(it) }
                }
                is LocationManagerEvent.AddFlat -> {
                    locationUseCases.addFlat(event.flat)
                    _uiEvent.send(UiEvent.Success("Flat added successfully"))
                    when {
                        event.flat.towerId != null -> loadFlatsForTower(event.flat.towerId!!)
                        event.flat.blockId != null -> loadFlatsForBlock(event.flat.blockId!!)
                        else -> event.flat.societyId?.let { loadFlatsForSociety(it) }
                    }
                }
                is LocationManagerEvent.UpdateFlat -> {
                    locationUseCases.updateFlat(event.flat)
                    _uiEvent.send(UiEvent.Success("Flat updated successfully"))
                    when {
                        event.flat.towerId != null -> loadFlatsForTower(event.flat.towerId!!)
                        event.flat.blockId != null -> loadFlatsForBlock(event.flat.blockId!!)
                        else -> event.flat.societyId?.let { loadFlatsForSociety(it) }
                    }
                }
                is LocationManagerEvent.DeleteFlat -> {
                    locationUseCases.deleteFlat(event.flatId)
                    _uiEvent.send(UiEvent.Success("Flat deleted successfully"))
                    // Reload flats based on current context
                    _state.value.selectedTower?.id?.let { loadFlatsForTower(it) }
                    _state.value.selectedBlock?.id?.let { loadFlatsForBlock(it) }
                    _state.value.selectedSociety?.id?.let { loadFlatsForSociety(it) }
                }
                else -> {
                    _uiEvent.send(UiEvent.Error("Unsupported event"))
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getCountries().collect { countries ->
                    _state.update { it.copy(
                        countries = countries,
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load initial data"))
            }
        }
    }

    fun loadStatesForCountry(countryId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getStatesForCountry(countryId).collect { states ->
                    _state.update { it.copy(
                        states = states,
                        selectedCountryId = countryId,
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load states"))
            }
        }
    }


    fun loadCitiesForState(stateId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getCitiesForState(stateId).collect { cities ->
                    _state.update { it.copy(
                        cities = cities,
                        selectedStateId = stateId,
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load cities"))
            }
        }
    }

    fun loadSocietiesForCity(cityId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getSocietiesForCity(cityId).collect { societies ->
                    _state.update { it.copy(
                        societies = societies,
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load societies"))
            }
        }
    }

    fun loadBlocksForSociety(societyId: Int) {
        viewModelScope.launch {
            try {
                locationUseCases.getBlocksForSociety(societyId).collect { blocks ->
                    _state.update { currentState ->
                        currentState.copy(
                            blocks = blocks.filter { it.societyId == societyId }
                        )
                    }
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load blocks"))
            }
        }
    }

    fun loadTowersForSociety(societyId: Int) {
        viewModelScope.launch {
            try {
                locationUseCases.getTowersForSociety(societyId).collect { towers ->
                    println("Loaded towers: $towers") // Debug log
                    _state.update { currentState ->
                        currentState.copy(
                            towers = towers
                        )
                    }
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load towers"))
            }
        }
    }

    fun loadFlatsForBlock(blockId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getFlatsForBlock(blockId).collect { flats ->
                    _state.update { it.copy(flats = flats, isLoading = false) }
                }
            } catch (e: Exception) {
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
            }
        }
    }

    fun loadFlatsForSociety(societyId: Int) {
        viewModelScope.launch {
            try {
                locationUseCases.getFlatsForSociety(societyId).collect { flats ->
                    _state.update { currentState ->
                        currentState.copy(
                            flats = flats.filter { it.societyId == societyId }
                        )
                    }
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Error(e.message ?: "Failed to load flats"))
            }
        }
    }

}

data class LocationManagerState(
    // Lists
    val countries: List<Country> = emptyList(),
    val states: List<State> = emptyList(),
    val cities: List<City> = emptyList(),
    val societies: List<Society> = emptyList(),
    val blocks: List<Block> = emptyList(),
    val towers: List<Tower> = emptyList(),
    val flats: List<Flat> = emptyList(),
    val selectedCountryId: Int? = null,
    val selectedStateId: Int? = null,
    val selectedCityId: Int? = null,
    val selectedSocietyId: Int? = null,

    // Selected items
    val selectedCountry: Country? = null,
    val selectedState: State? = null,
    val selectedCity: City? = null,
    val selectedSociety: Society? = null,
    val selectedBlock: Block? = null,
    val selectedTower: Tower? = null,
    val selectedFlat: Flat? = null,

    // UI state
    val isLoading: Boolean = false,
    val error: String? = null,
    val expandedSocietyId: Int? = null
)
