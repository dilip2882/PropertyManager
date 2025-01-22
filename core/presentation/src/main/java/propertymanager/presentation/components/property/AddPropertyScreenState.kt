import com.propertymanager.domain.model.Property
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.State

data class AddPropertyScreenState(
    val selectedCountry: Country? = null,
    val selectedState: State? = null,
    val selectedCity: City? = null,
    val selectedSociety: Society? = null,
    val selectedBuilding: Property.Building = Property.Building.FLAT,
    val selectedFlat: Flat? = null,
    val parentId: Int? = null
) 
