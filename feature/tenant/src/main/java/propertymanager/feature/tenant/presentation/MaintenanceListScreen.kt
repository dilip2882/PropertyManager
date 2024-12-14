package propertymanager.feature.tenant.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.model.RequestStatus
import kotlinx.coroutines.launch
import propertymanager.feature.tenant.presentation.components.MaintenanceListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceListScreen(
    onNavigateToMaintenanceRequest: (String?) -> Unit,
) {
    val viewModel = hiltViewModel<MaintenanceRequestViewModel>()
    val maintenanceRequests by viewModel.maintenanceRequests.collectAsState()
    val deleteResponse by viewModel.deleteRequestResponse.collectAsState()
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        viewModel.fetchMaintenanceRequests()
    }

    LaunchedEffect(deleteResponse) {
        when (deleteResponse) {
            is Response.Success -> {
                Toast.makeText(
                    context,
                    "Request deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            is Response.Error -> {
                Toast.makeText(
                    context,
                    "Failed to delete request: ${(deleteResponse as Response.Error).message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maintenance Feed") },
                actions = {
                    IconButton(onClick = { onNavigateToMaintenanceRequest(null) }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Request")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToMaintenanceRequest(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Create Request")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = maintenanceRequests) {
                is Response.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }

                is Response.Success -> {
                    val requests = state.data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
                    ) {
                        items(requests) { request ->
                            MaintenancePostCard(
                                maintenanceRequest = request,
                                onEditClick = {
                                    onNavigateToMaintenanceRequest(request.maintenanceRequestsId)
                                },
                                onDeleteClick = {
                                    Log.d("MaintenanceListScreen", "Delete clicked for ID: ${request.maintenanceRequestsId}")
                                    coroutineScope.launch {
                                        request.maintenanceRequestsId?.let { id ->
                                            viewModel.deleteMaintenanceRequest(id)
                                        }
                                    }
                                }

                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                is Response.Error -> {
                    Text(
                        text = "Failed to load maintenance requests",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun MaintenancePostCard(
    maintenanceRequest: MaintenanceRequest,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // User and Time Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(8.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Tenant",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = maintenanceRequest.createdAt.toDate().toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Issue Description
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = maintenanceRequest.issueDescription,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Media Gallery
            if (maintenanceRequest.photos.isNotEmpty() || maintenanceRequest.videos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(maintenanceRequest.photos) { photoUrl ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photoUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Photo",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    items(maintenanceRequest.videos) { videoUrl ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Video",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }

            // Category and Status
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Category: ${maintenanceRequest.issueCategory}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Status: ${maintenanceRequest.status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = when (maintenanceRequest.status) {
                        RequestStatus.PENDING.label -> Color.Red
                        RequestStatus.IN_PROGRESS.label -> Color.Blue
                        RequestStatus.COMPLETED.label -> Color.Green
                        else -> Color.Gray
                    }
                )
            }

            // Actions
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
