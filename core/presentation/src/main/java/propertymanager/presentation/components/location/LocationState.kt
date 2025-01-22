package propertymanager.presentation.components.location

import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.model.location.Tower

data class LocationState(
    val isLoading: Boolean = false,
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
    val selectedBlock: Block? = null,
    val selectedTower: Tower? = null,
    val selectedFlat: Flat? = null,
    val error: String? = null
)
