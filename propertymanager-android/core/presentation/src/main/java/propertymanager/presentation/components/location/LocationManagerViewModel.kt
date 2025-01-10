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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationManagerViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(LocationManagerState())
    val state: StateFlow<LocationManagerState> = _state.asStateFlow()

    init {
        loadCountries()
    }

    private fun loadCountries() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                locationUseCases.getCountries().collect { countries ->
                    _state.update {
                        it.copy(
                            countries = countries,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
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
        when (event) {
            is LocationManagerEvent.LoadLocations -> {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
                    locationUseCases.getCountries().collect { countries ->
                        _state.update { 
                            it.copy(
                                countries = countries,
                                isLoading = false
                            )
                        }
                    }
                }
            }

            // Country operations
            is LocationManagerEvent.AddCountry -> {
                viewModelScope.launch {
                    locationUseCases.addCountry(event.country).onSuccess {
                        loadCountries()
                    }.onFailure { error ->
                        _state.update { it.copy(error = error.message) }
                    }
                }
            }

            is LocationManagerEvent.UpdateCountry -> {
                viewModelScope.launch {
                    locationUseCases.updateCountry(event.country).onSuccess {
                        loadCountries()
                    }.onFailure { error ->
                        _state.update { it.copy(error = error.message) }
                    }
                }
            }

            is LocationManagerEvent.DeleteCountry -> {
        viewModelScope.launch {
                    locationUseCases.deleteCountry(event.countryId).onSuccess {
                        _state.update {
                            it.copy(
                                selectedCountry = null,
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
                        loadCountries()
                    }.onFailure { error ->
                        _state.update { it.copy(error = error.message) }
                    }
                }
            }

            // State operations
            is LocationManagerEvent.AddState -> {
                viewModelScope.launch {
                    val state = State(
                        id = event.state.id,
                        countryId = event.countryId,
                        name = event.state.name,
                        stateCode = event.state.stateCode,
                        latitude = event.state.latitude,
                        longitude = event.state.longitude,
                        type = event.state.type
                    )
                    locationUseCases.addState(state)
                }
            }

            is LocationManagerEvent.UpdateState -> {
        viewModelScope.launch {
                    locationUseCases.updateState(event.state).onSuccess {
                        state.value.selectedCountry?.let { country ->
                            locationUseCases.getStatesForCountry(country.id).collect { states ->
                                _state.update { it.copy(states = states) }
                            }
                        }
                    }.onFailure { error ->
                        _state.update { it.copy(error = error.message) }
                    }
                }
            }

            is LocationManagerEvent.DeleteState -> {
                viewModelScope.launch {
                    locationUseCases.deleteState(event.stateId)
                }
            }

            // City operations
            is LocationManagerEvent.AddCity -> {
        viewModelScope.launch {
                    val city = City(
                        id = event.city.id,
                        countryId = event.countryId,
                        stateId = event.stateId,
                        name = event.city.name,
                        latitude = event.city.latitude,
                        longitude = event.city.longitude
                    )
                    locationUseCases.addCity(city)
                }
            }

            is LocationManagerEvent.UpdateCity -> {
                viewModelScope.launch {
                    val city = event.city.copy(
                        countryId = event.countryId,
                        stateId = event.stateId
                    )
                    locationUseCases.updateCity(city)
                }
            }

            is LocationManagerEvent.DeleteCity -> {
        viewModelScope.launch {
                    locationUseCases.deleteCity(event.cityId)
                }
            }

            // Society operations
            is LocationManagerEvent.AddSociety -> {
                viewModelScope.launch {
                    val society = Society(
                        id = event.society.id,
                        countryId = event.countryId,
                        stateId = event.stateId,
                        cityId = event.cityId,
                        name = event.society.name,
                        latitude = event.society.latitude,
                        longitude = event.society.longitude
                    )
                    locationUseCases.addSociety(society)
                }
            }

            is LocationManagerEvent.UpdateSociety -> {
        viewModelScope.launch {
                    val society = event.society.copy(
                        countryId = event.countryId,
                        stateId = event.stateId,
                        cityId = event.cityId
                    )
                    locationUseCases.updateSociety(society)
                }
            }

            is LocationManagerEvent.DeleteSociety -> {
                viewModelScope.launch {
                    locationUseCases.deleteSociety(event.societyId)
                }
            }

            // Block operations
            is LocationManagerEvent.AddBlock -> {
        viewModelScope.launch {
                    val block = Block(
                        id = event.block.id,
                        societyId = event.societyId,
                        name = event.block.name,
                        type = event.block.type
                    )
                    locationUseCases.addBlock(block)
                }
            }

            is LocationManagerEvent.UpdateBlock -> {
                viewModelScope.launch {
                    val block = event.block.copy(societyId = event.societyId)
                    locationUseCases.updateBlock(block).onSuccess {
                        state.value.selectedSociety?.let { society ->
                            locationUseCases.getBlocksForSociety(society.id).collect { blocks ->
                                _state.update { it.copy(blocks = blocks) }
                            }
                        }
                    }.onFailure { error ->
                        _state.update { it.copy(error = error.message) }
                    }
                }
            }

            is LocationManagerEvent.DeleteBlock -> {
                viewModelScope.launch {
                    locationUseCases.deleteBlock(event.blockId)
                }
            }

            // Tower operations
            is LocationManagerEvent.AddTower -> {
        viewModelScope.launch {
                    val tower = Tower(
                        id = event.tower.id,
                        societyId = event.societyId,
                        blockId = event.blockId,
                        name = event.tower.name
                    )
                    locationUseCases.addTower(tower)
                }
            }

            is LocationManagerEvent.UpdateTower -> {
                viewModelScope.launch {
                    val tower = event.tower.copy(
                        societyId = event.societyId,
                        blockId = event.blockId
                    )
                    locationUseCases.updateTower(tower).onSuccess {
                        state.value.selectedSociety?.let { society ->
                            locationUseCases.getTowersForSociety(society.id).collect { towers ->
                                _state.update { it.copy(towers = towers) }
                            }
                        }
                    }.onFailure { error ->
                        _state.update { it.copy(error = error.message) }
                    }
                }
            }

            is LocationManagerEvent.DeleteTower -> {
                viewModelScope.launch {
                    locationUseCases.deleteTower(event.towerId)
                }
            }

            // Flat operations
            is LocationManagerEvent.AddFlat -> {
        viewModelScope.launch {
                    // Create flat with proper parent relationships
                    val flat = when {
                        state.value.selectedTower != null -> {
                            // If tower is selected, flat belongs to tower
                            Flat(
                                id = event.flat.id,
                                societyId = state.value.selectedSociety?.id ?: 0,
                                blockId = state.value.selectedBlock?.id,
                                towerId = state.value.selectedTower?.id,
                                number = event.flat.number,
                                floor = event.flat.floor,
                                type = event.flat.type,
                                area = event.flat.area,
                                status = event.flat.status
                            )
                        }
                        state.value.selectedBlock != null -> {
                            // If block is selected, flat belongs to block
                            Flat(
                                id = event.flat.id,
                                societyId = state.value.selectedSociety?.id ?: 0,
                                blockId = state.value.selectedBlock?.id,
                                towerId = null,
                                number = event.flat.number,
                                floor = event.flat.floor,
                                type = event.flat.type,
                                area = event.flat.area,
                                status = event.flat.status
                            )
                        }
                        state.value.selectedSociety != null -> {
                            // If only society is selected, flat belongs directly to society
                            Flat(
                                id = event.flat.id,
                                societyId = state.value.selectedSociety?.id ?: 0,
                                blockId = null,
                                towerId = null,
                                number = event.flat.number,
                                floor = event.flat.floor,
                                type = event.flat.type,
                                area = event.flat.area,
                                status = event.flat.status
                            )
                        }
                        else -> null
                    }

                    flat?.let { newFlat ->
                        locationUseCases.addFlat(newFlat).onSuccess {
                            // Refresh the appropriate flat list based on parent
                            when {
                                state.value.selectedTower != null -> {
                                    locationUseCases.getFlatsForTower(state.value.selectedTower!!.id)
                                        .collect { flats ->
                                            _state.update { it.copy(flats = flats) }
                                        }
                                }
                                state.value.selectedBlock != null -> {
                                    locationUseCases.getFlatsForBlock(state.value.selectedBlock!!.id)
                                        .collect { flats ->
                                            _state.update { it.copy(flats = flats) }
                                        }
                                }
                                state.value.selectedSociety != null -> {
                                    locationUseCases.getFlatsForSociety(state.value.selectedSociety!!.id)
                                        .collect { flats ->
                                            _state.update { it.copy(flats = flats) }
                                        }
                                }
                            }
                        }.onFailure { error ->
                            _state.update { it.copy(error = error.message) }
                        }
                    }
                }
            }

            is LocationManagerEvent.UpdateFlat -> {
                viewModelScope.launch {
                    val flat = event.flat.copy(
                        societyId = event.societyId,
                        blockId = if (event.parentId == event.societyId) null else event.parentId,
                        towerId = if (event.parentId != event.societyId) event.parentId else null
                    )
                    locationUseCases.updateFlat(flat).onSuccess {
                        when {
                            state.value.selectedTower != null -> {
                                locationUseCases.getFlatsForTower(state.value.selectedTower!!.id)
                                    .collect { flats ->
                                        _state.update { it.copy(flats = flats) }
                                    }
                            }
                            state.value.selectedBlock != null -> {
                                locationUseCases.getFlatsForBlock(state.value.selectedBlock!!.id)
                                    .collect { flats ->
                                        _state.update { it.copy(flats = flats) }
                                    }
                            }
                            state.value.selectedSociety != null -> {
                                locationUseCases.getFlatsForSociety(state.value.selectedSociety!!.id)
                                    .collect { flats ->
                                        _state.update { it.copy(flats = flats) }
                                    }
                            }
                        }
                    }.onFailure { error ->
                        _state.update { it.copy(error = error.message) }
                    }
                }
            }

            is LocationManagerEvent.DeleteFlat -> {
                viewModelScope.launch {
                    locationUseCases.deleteFlat(event.flatId)
                }
            }

            else -> {}
        }
    }
}

data class LocationManagerState(
    val countries: List<Country> = emptyList(),
    val selectedCountry: Country? = null,
    val selectedState: State? = null,
    val selectedCity: City? = null,
    val selectedSociety: Society? = null,
    val selectedBlock: Block? = null,
    val selectedTower: Tower? = null,
    val states: List<State> = emptyList(),
    val cities: List<City> = emptyList(),
    val societies: List<Society> = emptyList(),
    val blocks: List<Block> = emptyList(),
    val towers: List<Tower> = emptyList(),
    val flats: List<Flat> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
