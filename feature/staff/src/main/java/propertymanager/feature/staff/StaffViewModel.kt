package propertymanager.feature.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.model.Property
import com.propertymanager.domain.model.PropertyStatus
import com.propertymanager.domain.model.WorkerDetails
import com.propertymanager.domain.usecase.StaffUseCases
import com.propertymanager.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val staffUseCases: StaffUseCases,
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _assignedRequests = MutableStateFlow<Response<List<MaintenanceRequest>>>(Response.Loading)
    val assignedRequests: StateFlow<Response<List<MaintenanceRequest>>> = _assignedRequests.asStateFlow()

    private val _maintenanceRequestsMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val maintenanceRequestsMap: StateFlow<Map<String, Int>> = _maintenanceRequestsMap.asStateFlow()

    private val _filteredRequests = MutableStateFlow<List<MaintenanceRequest>>(emptyList())
    val filteredRequests: StateFlow<List<MaintenanceRequest>> = _filteredRequests.asStateFlow()

    private val _updateStatusResponse = MutableStateFlow<Response<Boolean>?>(null)
    val updateStatusResponse: StateFlow<Response<Boolean>?> = _updateStatusResponse.asStateFlow()

    private val _updatePriorityResponse = MutableStateFlow<Response<Boolean>?>(null)
    val updatePriorityResponse: StateFlow<Response<Boolean>?> = _updatePriorityResponse.asStateFlow()

    private val _assignWorkerResponse = MutableStateFlow<Response<Boolean>?>(null)
    val assignWorkerResponse: StateFlow<Response<Boolean>?> = _assignWorkerResponse.asStateFlow()

    private val _updateNotesResponse = MutableStateFlow<Response<Boolean>?>(null)
    val updateNotesResponse: StateFlow<Response<Boolean>?> = _updateNotesResponse.asStateFlow()

    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties = _properties.asStateFlow()

    fun updateFilteredRequests(requests: List<MaintenanceRequest>) {
        _filteredRequests.value = requests
    }

    fun fetchAssignedRequests(staffId: String) {
        viewModelScope.launch {
            staffUseCases.getAssignedRequests(staffId).collectLatest { response ->
                _assignedRequests.value = response
                if (response is Response.Success) {
                    // Update the maintenance request count map
                    val requestMap = response.data.groupBy { it.propertyId }
                        .mapValues { it.value.size }
                    _maintenanceRequestsMap.value = requestMap
                    _filteredRequests.value = response.data
                }
            }
        }
    }

//    fun fetchAllMaintenanceRequests() {
//        viewModelScope.launch {
//            staffUseCases.getAllMaintenanceRequests().collectLatest { response ->
//                if (response is Response.Success) {
//                    _filteredRequests.value = response.data
//                }
//            }
//        }
//    }

    fun updateRequestStatus(requestId: String, status: String) {
        viewModelScope.launch {
            staffUseCases.updateRequestStatus(requestId, status).collectLatest { response ->
                _updateStatusResponse.value = response
            }
        }
    }

    fun updateRequestPriority(requestId: String, priority: String) {
        viewModelScope.launch {
            staffUseCases.setPriorityFlag(requestId, priority).collectLatest { response ->
                _updatePriorityResponse.value = response
            }
        }
    }

    fun assignWorker(requestId: String, workerDetails: WorkerDetails) {
        viewModelScope.launch {
            staffUseCases.assignWorker(requestId, workerDetails).collectLatest { response ->
                _assignWorkerResponse.value = response
            }
        }
    }

    fun updateRequestNotes(requestId: String, notes: String) {
        viewModelScope.launch {
            staffUseCases.updateRequestNotes(requestId, notes).collectLatest { response ->
                _updateNotesResponse.value = response
            }
        }
    }

    fun resetResponses() {
        _updateStatusResponse.value = null
        _updatePriorityResponse.value = null
        _assignWorkerResponse.value = null
        _updateNotesResponse.value = null
    }

    fun loadPendingProperties() {
        viewModelScope.launch {
            propertyRepository.getProperties().collect { properties ->
                _properties.value = properties.filter { it.status == PropertyStatus.PENDING_APPROVAL }
            }
        }
    }
}
