package propertymanager.presentation.components.property

import android.util.Log
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
    val propertyUseCases: PropertyUseCases
) : ViewModel() {

    private val _propertiesResponse = MutableStateFlow<Response<List<Property>>>(Response.Loading)
    val propertiesResponse: StateFlow<Response<List<Property>>> = _propertiesResponse

    private val _operationResponse = MutableStateFlow<Response<Unit>>(Response.Success(Unit))
    val operationResponse: StateFlow<Response<Unit>> = _operationResponse

    private var propertiesLoaded = false

    init {
        fetchProperties()
    }

    fun resetProperties() {
        propertiesLoaded = false
        fetchProperties()
    }

    private fun fetchProperties() {
        if (propertiesLoaded) return

        viewModelScope.launch {
            _propertiesResponse.value = Response.Loading
            try {
                val properties = propertyUseCases.getProperties()
                _propertiesResponse.value = Response.Success(properties)
                propertiesLoaded = true
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
                Log.d("PropertyViewModel", "Property updated successfully")
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to update property")
                Log.e("PropertyViewModel", "Error updating property: ${e.message}")
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

    fun addAddress(propertyId: String, address: Property.Address) {
        viewModelScope.launch {
            _operationResponse.value = Response.Loading
            try {
                propertyUseCases.addAddress(propertyId, address)
                fetchProperties()
                _operationResponse.value = Response.Success(Unit)
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to add address")
            }
        }
    }

    fun deleteAddress(propertyId: String) {
        viewModelScope.launch {
            _operationResponse.value = Response.Loading
            try {
                propertyUseCases.deleteAddress(propertyId)
                fetchProperties()
                _operationResponse.value = Response.Success(Unit)
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to delete address")
            }
        }
    }

    fun updateAddress(propertyId: String, address: Property.Address) {
        viewModelScope.launch {
            _operationResponse.value = Response.Loading
            try {
                propertyUseCases.updateAddress(propertyId, address)
                fetchProperties()
                _operationResponse.value = Response.Success(Unit)
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to update address")
            }
        }
    }
}
