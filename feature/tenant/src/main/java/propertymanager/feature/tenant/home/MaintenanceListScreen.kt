package propertymanager.feature.tenant.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.RequestStatus
import kotlinx.coroutines.launch
import propertymanager.feature.tenant.home.components.MaintenancePostCard
import propertymanager.feature.tenant.home.components.PullRefresh

@Composable
fun MaintenanceListScreen(
    onNavigateToMaintenanceRequest: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
) {
    val viewModel = hiltViewModel<MaintenanceRequestViewModel>()
    val maintenanceRequests by viewModel.maintenanceRequests.collectAsState()
    val deleteResponse by viewModel.deleteRequestResponse.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    var isInitialLoading by remember { mutableStateOf(true) }
    val expandedCardState = remember { mutableStateOf<String?>(null) } // Track the currently expanded card

    LaunchedEffect(Unit) {
        viewModel.fetchMaintenanceRequests()
    }

    LaunchedEffect(maintenanceRequests) {
        if (maintenanceRequests !is Response.Loading) {
            isInitialLoading = false
        }
    }

    LaunchedEffect(deleteResponse) {
        when (deleteResponse) {
            is Response.Success -> {
                Toast.makeText(context, "Request deleted successfully", Toast.LENGTH_SHORT).show()
            }

            is Response.Error -> {
                Toast.makeText(
                    context,
                    "Failed to delete request: ${(deleteResponse as Response.Error).message}",
                    Toast.LENGTH_SHORT,
                ).show()
            }

            else -> {}
        }
    }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Maintenance Feed") },
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    listOf("Pending", "In Progress", "Completed").forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToMaintenanceRequest,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Request")
            }
        },
    ) { paddingValues ->
        PullRefresh(
            refreshing = !isInitialLoading && maintenanceRequests is Response.Loading,
            enabled = true,
            onRefresh = {
                isInitialLoading = false
                viewModel.fetchMaintenanceRequests()
            },
            modifier = Modifier.padding(paddingValues),
            indicatorPadding = PaddingValues(),
        ) {
            when (val state = maintenanceRequests) {
                is Response.Loading -> {
                    if (isInitialLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is Response.Success -> {
                    val requests = state.data.filter { request ->
                        when (selectedTab) {
                            0 -> request.status == RequestStatus.PENDING.label
                            1 -> request.status == RequestStatus.IN_PROGRESS.label
                            2 -> request.status == RequestStatus.COMPLETED.label
                            else -> false
                        }
                    }

                    if (requests.isEmpty()) {
                        EmptyStateContent(
                            onCreateClick = onNavigateToMaintenanceRequest,
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                        ) {
                            items(requests) { request ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
//                                        .clickable {
//                                            request.maintenanceRequestsId?.let { requestId ->
//                                                onNavigateToDetails(requestId)
//                                            }
//                                        }
                                ) {
                                    MaintenancePostCard(
                                        maintenanceRequest = request,
                                        onEditClick = {
                                            /* TODO */
                                        },
                                        onCardClick = {
                                            request.maintenanceRequestsId?.let { requestId ->
                                                onNavigateToDetails(requestId)
                                            }
                                        },
                                        onDeleteClick = {
                                            coroutineScope.launch {
                                                request.maintenanceRequestsId?.let { id ->
                                                    viewModel.deleteMaintenanceRequest(id)
                                                }
                                            }
                                        },
                                        revealState = expandedCardState.value == request.maintenanceRequestsId,
                                        onRevealStateChange = { isRevealed ->
                                            if (isRevealed) {
                                                expandedCardState.value = request.maintenanceRequestsId
                                            } else {
                                                expandedCardState.value = null
                                            }
                                        },
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                is Response.Error -> {
                    Text(
                        text = "Failed to load maintenance requests",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateContent(
    onCreateClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            imageVector = Icons.Filled.Task,
            contentDescription = "No tasks",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp),
        )
        Text(
            text = "No tasks in this category.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Click + to create your task.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
