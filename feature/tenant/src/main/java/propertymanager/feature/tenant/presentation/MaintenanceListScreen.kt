package propertymanager.feature.tenant.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.common.utils.Response
import propertymanager.feature.tenant.presentation.components.MaintenanceListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceListScreen(
    onNavigateToMaintenanceRequest: (String?) -> Unit,
) {
    val maintenanceViewModel = hiltViewModel<MaintenanceRequestViewModel>()
    val maintenanceRequestsState = maintenanceViewModel.maintenanceRequests.value
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        maintenanceViewModel.getMaintenanceRequests()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maintenance Requests") },
                actions = {
                    IconButton(onClick = { onNavigateToMaintenanceRequest(null) }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Request")
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (maintenanceRequestsState) {
                is Response.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
                }

                is Response.Success -> {
                    val requests = maintenanceRequestsState.data
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                        items(requests) { request ->
                            MaintenanceListItem(
                                maintenanceRequest = request,
                                onEditClick = { onNavigateToMaintenanceRequest(request.id) },
                                onDeleteClick = {
                                    request.id?.let { id ->
                                        maintenanceViewModel.deleteMaintenanceRequest(id)
                                        Toast.makeText(context, "Request deleted successfully", Toast.LENGTH_SHORT).show()
                                    }
                                },
                            )
                        }
                    }
                }

                is Response.Error -> {
                    Text(text = "Failed to load maintenance requests", color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
                }
            }
        }
    }
}
