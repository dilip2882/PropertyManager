package propertymanager.feature.staff.settings.property

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Property
import com.propertymanager.domain.usecase.PropertyUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyViewModel @Inject constructor(
    private val propertyUseCases: PropertyUseCases,
) : ViewModel() {

    private val _propertiesResponse = MutableStateFlow<Response<List<Property>>>(Response.Loading)
    val propertiesResponse: StateFlow<Response<List<Property>>> = _propertiesResponse

    private val _operationResponse = MutableStateFlow<Response<Unit>>(Response.Success(Unit))
    val operationResponse: StateFlow<Response<Unit>> = _operationResponse

    init {
        fetchProperties()
    }

    private fun fetchProperties() {
        viewModelScope.launch {
            _propertiesResponse.value = Response.Loading
            try {
                val properties = propertyUseCases.getProperties()
                _propertiesResponse.value = Response.Success(properties)
            } catch (e: Exception) {
                _propertiesResponse.value = Response.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun addProperty(property: Property) {
        viewModelScope.launch {
            _operationResponse.value = Response.Loading
            try {
                propertyUseCases.addProperty(property)
                fetchProperties()
                _operationResponse.value = Response.Success(Unit)
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to add property")
            }
        }
    }

    fun updateProperty(property: Property) {
        viewModelScope.launch {
            _operationResponse.value = Response.Loading
            try {
                propertyUseCases.updateProperty(property)
                fetchProperties()
                _operationResponse.value = Response.Success(Unit)
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to update property")
            }
        }
    }

    fun deleteProperty(propertyId: String) {
        viewModelScope.launch {
            _operationResponse.value = Response.Loading
            try {
                propertyUseCases.deleteProperty(propertyId)
                fetchProperties()
                _operationResponse.value = Response.Success(Unit)
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to delete property")
            }
        }
    }
}
