package propertymanager.feature.staff.settings.property

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.common.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import propertymanager.core.network.demo.LocationDataSource
import propertymanager.core.network.model.CityData
import propertymanager.core.network.model.CountryData
import propertymanager.core.network.model.StateData
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationDataSource: LocationDataSource
) : ViewModel() {

    private val _countries = MutableStateFlow<Response<List<CountryData>>>(Response.Loading)
    val countries: StateFlow<Response<List<CountryData>>> = _countries.asStateFlow()

    private val _selectedCountry = MutableStateFlow<CountryData?>(null)
    val selectedCountry = _selectedCountry.asStateFlow()

    private val _selectedState = MutableStateFlow<StateData?>(null)
    val selectedState = _selectedState.asStateFlow()

    private val _selectedCity = MutableStateFlow<CityData?>(null)
    val selectedCity = _selectedCity.asStateFlow()

    init {
        loadCountries()
    }

    private fun loadCountries() {
        viewModelScope.launch {
            try {
                val countriesList = locationDataSource.getCountries()
                _countries.value = Response.Success(countriesList)
            } catch (e: Exception) {
                _countries.value = Response.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun selectCountry(country: CountryData) {
        Log.d("LocationViewModel", "Selecting country: ${country.name}")
        _selectedCountry.value = country
        _selectedState.value = null
        _selectedCity.value = null
    }

    fun selectState(state: StateData) {
        Log.d("LocationViewModel", "Selecting state: ${state.name}")
        _selectedState.value = state
        _selectedCity.value = null
    }
    fun selectCity(city: CityData) {
        _selectedCity.value = city
    }
}
