package propertymanager.feature.tenant.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.common.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import propertymanager.feature.tenant.domian.model.MaintenanceRequest
import propertymanager.feature.tenant.domian.usecase.MaintenanceRequestUseCases
import javax.inject.Inject

@HiltViewModel
class MaintenanceRequestViewModel @Inject constructor(
    private val maintenanceRequestUseCases: MaintenanceRequestUseCases,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _maintenanceRequests = mutableStateOf<Response<List<MaintenanceRequest>>>(Response.Loading)
    val maintenanceRequests: State<Response<List<MaintenanceRequest>>> = _maintenanceRequests

    private val _maintenanceRequest = mutableStateOf<Response<MaintenanceRequest>>(Response.Loading)
    val maintenanceRequest: State<Response<MaintenanceRequest>> = _maintenanceRequest

    private val _createRequestResponse = mutableStateOf<Response<Boolean>>(Response.Loading)
    val createRequestResponse: State<Response<Boolean>> = _createRequestResponse

    private val _updateRequestResponse = mutableStateOf<Response<Boolean>>(Response.Loading)
    val updateRequestResponse: State<Response<Boolean>> = _updateRequestResponse

    private val _deleteRequestResponse = mutableStateOf<Response<Boolean>>(Response.Loading)
    val deleteRequestResponse: State<Response<Boolean>> = _deleteRequestResponse

    fun getMaintenanceRequests() {
        viewModelScope.launch {
            maintenanceRequestUseCases.getMaintenanceRequests().collect { response ->
                _maintenanceRequests.value = response
            }
        }
    }

    fun getMaintenanceRequestById(requestId: String) {
        viewModelScope.launch {
            maintenanceRequestUseCases.getMaintenanceRequestById(requestId).collect { response ->
                _maintenanceRequest.value = response
            }
        }
    }

    fun createMaintenanceRequest(request: MaintenanceRequest) {
        viewModelScope.launch {
            maintenanceRequestUseCases.createMaintenanceRequest(request).collect { response ->
                _createRequestResponse.value = response
            }
        }
    }

    fun updateMaintenanceRequest(request: MaintenanceRequest) {
        viewModelScope.launch {
            maintenanceRequestUseCases.updateMaintenanceRequest(request).collect { response ->
                _updateRequestResponse.value = response
            }
        }
    }

    fun deleteMaintenanceRequest(requestId: String) {
        viewModelScope.launch {
            maintenanceRequestUseCases.deleteMaintenanceRequest(requestId).collect { response ->
                _deleteRequestResponse.value = response
            }
        }
    }
}
