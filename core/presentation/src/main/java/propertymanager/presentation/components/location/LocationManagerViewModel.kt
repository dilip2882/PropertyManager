package propertymanager.presentation.components.location

import LocationManagerEvent
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationManagerViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(LocationManagerState())
    val state: StateFlow<LocationManagerState> = _state

    init {
        loadCountries()
    }

    private fun loadCountries() {
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

    fun onCountrySelected(country: Country) {
        viewModelScope.launch {
            _state.update { 
                it.copy(
                    selectedCountry = country,
                    selectedState = null,
                    selectedCity = null,
                    selectedSociety = null,
                    selectedBlock = null,
                    selectedTower = null
                ) 
            }
            locationUseCases.getStatesForCountry(country.id).collect { states ->
                _state.update { it.copy(states = states) }
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
                    selectedTower = null
                ) 
            }
            // Load blocks, towers and flats
            locationUseCases.getBlocksForSociety(society.id).collect { blocks ->
                _state.update { it.copy(blocks = blocks) }
            }
            locationUseCases.getTowersForSociety(society.id).collect { towers ->
                _state.update { it.copy(towers = towers) }
            }
            locationUseCases.getFlatsForSociety(society.id).collect { flats ->
                _state.update { it.copy(flats = flats) }
            }
        }
    }

    fun onBlockSelected(block: Block) {
        viewModelScope.launch {
            _state.update { 
                it.copy(
                    selectedBlock = block,
                    selectedTower = null
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
                    selectedBlock = null
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
                    locationUseCases.addCountry(event.country)
                }
            }

            is LocationManagerEvent.UpdateCountry -> {
                viewModelScope.launch {
                    locationUseCases.updateCountry(event.country)
                }
            }

            is LocationManagerEvent.DeleteCountry -> {
                viewModelScope.launch {
                    locationUseCases.deleteCountry(event.countryId)
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
                    val state = event.state.copy(countryId = event.countryId)
                    locationUseCases.updateState(state)
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
                    locationUseCases.updateBlock(block)
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
                    locationUseCases.updateTower(tower)
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
                    val flat = Flat(
                        id = event.flat.id,
                        societyId = event.societyId,
                        blockId = if (event.parentId == event.societyId) null else event.parentId,
                        towerId = if (event.parentId != event.societyId) event.parentId else null,
                        number = event.flat.number,
                        floor = event.flat.floor,
                        type = event.flat.type,
                        area = event.flat.area,
                        status = event.flat.status
                    )
                    locationUseCases.addFlat(flat)
                }
            }

            is LocationManagerEvent.UpdateFlat -> {
                viewModelScope.launch {
                    val flat = event.flat.copy(
                        societyId = event.societyId,
                        blockId = if (event.parentId == event.societyId) null else event.parentId,
                        towerId = if (event.parentId != event.societyId) event.parentId else null
                    )
                    locationUseCases.updateFlat(flat)
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
