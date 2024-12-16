package propertymanager.feature.tenant.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.common.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.propertymanager.domain.model.MaintenanceRequest
import com.google.firebase.Timestamp
import com.propertymanager.domain.model.MediaType
import com.propertymanager.domain.repository.MaintenanceRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import com.propertymanager.domain.usecase.MediaUploadUseCase
import javax.inject.Inject

@HiltViewModel
class MaintenanceRequestViewModel @Inject constructor(
    private val repository: MaintenanceRequestRepository,
    private val mediaUploadUseCase: MediaUploadUseCase,
) : ViewModel() {

    private val _maintenanceRequests = MutableStateFlow<Response<List<MaintenanceRequest>>>(Response.Loading)
    val maintenanceRequests: StateFlow<Response<List<MaintenanceRequest>>> = _maintenanceRequests.asStateFlow()

    private val _currentRequest = MutableStateFlow<Response<MaintenanceRequest>>(Response.Loading)
    val currentRequest: StateFlow<Response<MaintenanceRequest>> = _currentRequest.asStateFlow()

    private val _createRequestState = MutableStateFlow<Response<MaintenanceRequest>>(Response.Loading)
    val createRequestState: StateFlow<Response<MaintenanceRequest>> = _createRequestState.asStateFlow()

    private val _deleteRequestResponse = MutableStateFlow<Response<Boolean>>(Response.Loading)
    val deleteRequestResponse: StateFlow<Response<Boolean>> = _deleteRequestResponse.asStateFlow()

    private val _mediaUploadState = MutableStateFlow<Map<Uri, Response<String>>>(emptyMap())
    val mediaUploadState = _mediaUploadState.asStateFlow()

    fun fetchMaintenanceRequests() {
        viewModelScope.launch {
            repository.getMaintenanceRequests().collectLatest { response ->
                _maintenanceRequests.value = response
            }
        }
    }

    fun fetchMaintenanceRequestById(requestId: String) {
        viewModelScope.launch {
            repository.getMaintenanceRequestById(requestId).collectLatest { response ->
                _currentRequest.value = response
            }
        }
    }

    fun createMaintenanceRequest(request: MaintenanceRequest) {
        viewModelScope.launch {
            repository.createMaintenanceRequest(request).collectLatest { response ->
                _createRequestState.value = response
            }
        }
    }

    fun updateMaintenanceRequest(request: MaintenanceRequest) {
        viewModelScope.launch {
            repository.updateMaintenanceRequest(request).collectLatest { response ->
                _createRequestState.value = response
            }
        }
    }

    fun deleteMaintenanceRequest(requestId: String) {
        viewModelScope.launch {
            _deleteRequestResponse.value = Response.Loading
            try {
                if (requestId.isNotEmpty()) {
                    repository.deleteMaintenanceRequest(requestId).collectLatest { deleteResponse ->
                        _deleteRequestResponse.value = deleteResponse
                        fetchMaintenanceRequests() // refresh
                    }
                } else {
                    _deleteRequestResponse.value = Response.Error("No response from server")

                }
            } catch (e: Exception) {
                Log.e("MaintenanceRequestViewModel", "Error during deletion: ${e.localizedMessage}")
                _deleteRequestResponse.value = Response.Error(e.localizedMessage ?: "Failed to delete request")
            }
        }
    }

    fun uploadMedia(uri: Uri, mediaType: MediaType) {
        viewModelScope.launch {
            _mediaUploadState.update { it + (uri to Response.Loading) }

            mediaUploadUseCase.uploadMedia(uri, mediaType).collect { response ->
                _mediaUploadState.update { it + (uri to response) }
            }
        }
    }
    fun uploadMediaAndCreateRequest(
        request: MaintenanceRequest,
        photos: List<Uri>,
        videos: List<Uri>
    ) {
        viewModelScope.launch {
            try {
                _createRequestState.value = Response.Loading

                val photoUrls = photos.map { uri ->
                    mediaUploadUseCase.uploadMedia(uri, MediaType.IMAGE).first { it is Response.Success }
                }.mapNotNull { (it as? Response.Success)?.data }

                val videoUrls = videos.map { uri ->
                    mediaUploadUseCase.uploadMedia(uri, MediaType.VIDEO).first { it is Response.Success }
                }.mapNotNull { (it as? Response.Success)?.data }

                val finalRequest = request.copy(
                    photos = photoUrls,
                    videos = videoUrls,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )

                if (request.maintenanceRequestsId == null) {
                    repository.createMaintenanceRequest(finalRequest).collect {
                        _createRequestState.value = it
                    }
                } else {
                    repository.updateMaintenanceRequest(finalRequest).collect {
                        _createRequestState.value = it
                    }
                }
            } catch (e: Exception) {
                _createRequestState.value = Response.Error(e.message ?: "Failed to create request")
            }
        }
    }
}


