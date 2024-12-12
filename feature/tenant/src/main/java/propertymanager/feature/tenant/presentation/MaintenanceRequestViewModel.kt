package propertymanager.feature.tenant.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.propertymanager.common.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import propertymanager.feature.tenant.domian.model.MaintenanceRequest
import propertymanager.feature.tenant.domian.usecase.MaintenanceRequestUseCases
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MaintenanceRequestViewModel @Inject constructor(
    private val maintenanceRequestUseCases: MaintenanceRequestUseCases,
    private val firebaseStorage: FirebaseStorage,
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
            _maintenanceRequests.value = Response.Loading  // Show loading initially
            try {
                maintenanceRequestUseCases.getMaintenanceRequests().collect { response ->
                    _maintenanceRequests.value = response // Update the state based on response
                }
            } catch (e: Exception) {
                _maintenanceRequests.value = Response.Error("Error: ${e.message}")
            }
        }
    }

    fun getMaintenanceRequestById(requestId: String) {
        viewModelScope.launch {
            _maintenanceRequest.value = Response.Loading
            try {
                maintenanceRequestUseCases.getMaintenanceRequestById(requestId).collect { response ->
                    _maintenanceRequest.value = response
                }
            } catch (e: Exception) {
                _maintenanceRequest.value = Response.Error("Error: ${e.message}")
            }
        }
    }

    fun createMaintenanceRequest(request: MaintenanceRequest) {
        viewModelScope.launch {
            _createRequestResponse.value = Response.Loading
            try {
                maintenanceRequestUseCases.createMaintenanceRequest(request).collect { response ->
                    _createRequestResponse.value = response
                }
            } catch (e: Exception) {
                _createRequestResponse.value = Response.Error("Error creating request: ${e.message}")
            }
        }
    }

    fun updateMaintenanceRequest(request: MaintenanceRequest) {
        viewModelScope.launch {
            _updateRequestResponse.value = Response.Loading
            try {
                maintenanceRequestUseCases.updateMaintenanceRequest(request).collect { response ->
                    _updateRequestResponse.value = response
                }
            } catch (e: Exception) {
                _updateRequestResponse.value = Response.Error("Error updating request: ${e.message}")
            }
        }
    }

    fun deleteMaintenanceRequest(requestId: String) {
        viewModelScope.launch {
            _deleteRequestResponse.value = Response.Loading
            try {
                maintenanceRequestUseCases.deleteMaintenanceRequest(requestId).collect { response ->
                    _deleteRequestResponse.value = response
                }
            } catch (e: Exception) {
                _deleteRequestResponse.value = Response.Error("Error deleting request: ${e.message}")
            }
        }
    }

    fun uploadMedia(
        uri: Uri,
        mediaType: MediaType,
        onMediaUploaded: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val fileName = "media_${System.currentTimeMillis()}"
                val storageRef = firebaseStorage.reference.child("${mediaType.name.lowercase(Locale.ROOT)}/$fileName")

                storageRef.putFile(uri).await()

                val downloadUrl = storageRef.downloadUrl.await().toString()

                onMediaUploaded(downloadUrl)

            } catch (e: Exception) {
                Log.e("MaintenanceRequestViewModel", "Error uploading media: ${e.message}")
            }
        }
    }
}


enum class MediaType {
    IMAGE, VIDEO
}
