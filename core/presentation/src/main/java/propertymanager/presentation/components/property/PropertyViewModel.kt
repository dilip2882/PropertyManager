package propertymanager.presentation.components.property

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.propertymanager.domain.model.Property
import com.propertymanager.domain.model.PropertyStatus
import com.propertymanager.domain.repository.PropertyRepository
import com.propertymanager.domain.usecase.PropertyUseCases
import com.propertymanager.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PropertyViewModel @Inject constructor(
    private val propertyUseCases: PropertyUseCases,
    private val userUseCases: UserUseCases,
    private val auth: FirebaseAuth,
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PropertyState())
    val state = _state.asStateFlow()

    private val _property = MutableStateFlow<Property?>(null)
    val property = _property.asStateFlow()

    private var _searchQuery = MutableStateFlow("")
    private var _selectedStatus = MutableStateFlow<PropertyStatus?>(null)

    init {
        loadProperties()
        // Combine search and filter to update properties
        viewModelScope.launch {
            combine(
                _searchQuery,
                _selectedStatus,
                propertyRepository.getProperties()
            ) { query, status, properties ->
                filterProperties(properties, query, status)
            }.collect { filteredProperties ->
                _state.update { it.copy(properties = filteredProperties) }
            }
        }
    }

    private fun filterProperties(
        properties: List<Property>,
        query: String,
        status: PropertyStatus?
    ): List<Property> {
        return properties.filter { property ->
            val matchesQuery = property.address.toString().contains(query, ignoreCase = true)
            val matchesStatus = status == null || property.status == status
            matchesQuery && matchesStatus
        }
    }

    fun onEvent(event: PropertyEvent) {
        when (event) {
            is PropertyEvent.DeleteProperty -> {
                viewModelScope.launch {
                    try {
                        _state.update { it.copy(isLoading = true) }
                        propertyUseCases.deleteProperty(event.property.id)
                        // Refresh the properties list
//                        loadProperties() //  snapshot listener
                    } catch (e: Exception) {
                        _state.update { it.copy(
                            error = "Failed to delete property: ${e.message}",
                            isLoading = false
                        ) }
                    }
                }
            }

            is PropertyEvent.EditProperty -> {
                viewModelScope.launch {
                    try {
                        propertyUseCases.updateProperty(event.property)
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }

            is PropertyEvent.AddProperty -> {
                viewModelScope.launch {
                    try {
                        _state.update { it.copy(isLoading = true) }
                        val propertyId = propertyUseCases.addProperty(event.property)
                        
                        // Associate property with user
                        auth.currentUser?.uid?.let { userId ->
                            userUseCases.associateProperty(userId, propertyId)
                        //    userUseCases.updateSelectedProperty(userId, propertyId)
                        }

                        _state.update { it.copy(
                            lastAddedPropertyId = propertyId,
                            isLoading = false
                        ) }
                    } catch (e: Exception) {
                        _state.update { it.copy(
                            error = e.message,
                            isLoading = false
                        ) }
                    }
                }
            }

            is PropertyEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }

            is PropertyEvent.SearchQueryChanged -> {
                _searchQuery.value = event.query
            }

            is PropertyEvent.StatusFilterChanged -> {
                _selectedStatus.value = event.status
            }
        }
    }

    fun loadProperties() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                propertyRepository.getProperties()
                    .collect { properties ->
                        _state.update { it.copy(
                            properties = properties,
                            isLoading = false
                        ) }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message,
                    isLoading = false
                ) }
            }
        }
    }

    fun getPropertyById(propertyId: String) {
        viewModelScope.launch {
            try {
                val result = propertyRepository.getPropertyById(propertyId)
                _property.value = result
            } catch (e: Exception) {
            }
        }
    }

    fun updatePropertyStatus(propertyId: String, status: PropertyStatus) {
        viewModelScope.launch {
            try {
                propertyRepository.updatePropertyStatus(propertyId, status)
                // Refresh property after status update
                getPropertyById(propertyId)
            } catch (e: Exception) {

            }
        }
    }
}

data class PropertyState(
    val properties: List<Property> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastAddedPropertyId: String? = null
)

sealed class PropertyEvent {
    data class DeleteProperty(val property: Property) : PropertyEvent()
    data class EditProperty(val property: Property) : PropertyEvent()
    data class AddProperty(val property: Property) : PropertyEvent()
    data object ClearError : PropertyEvent()
    data class SearchQueryChanged(val query: String) : PropertyEvent()
    data class StatusFilterChanged(val status: PropertyStatus?) : PropertyEvent()
}
