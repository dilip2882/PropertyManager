package propertymanager.feature.tenant.support

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.model.PropertyStatus
import com.propertymanager.domain.model.RequestStatus
import com.propertymanager.domain.model.isActive
import kotlinx.coroutines.launch
import propertymanager.feature.tenant.support.components.MaintenancePostCard
import propertymanager.feature.tenant.support.components.PullRefresh
import propertymanager.presentation.components.property.PropertyViewModel
import propertymanager.presentation.components.user.UserEvent
import propertymanager.presentation.components.user.UserViewModel

@Composable
fun MaintenanceListScreen(
    onNavigateToMaintenanceRequest: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToAddProperty: () -> Unit,
    propertyViewModel: PropertyViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val viewModel = hiltViewModel<MaintenanceRequestViewModel>()
    val maintenanceRequests by viewModel.maintenanceRequests.collectAsState()
    val deleteResponse by viewModel.deleteRequestResponse.collectAsState()
    val propertyState by propertyViewModel.state.collectAsState()
    val userState by userViewModel.state.collectAsState()
    var showDropdown by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val selectedProperty = remember(propertyState.properties, userState.user?.selectedPropertyId) {
        propertyState.properties.find { it.id == userState.user?.selectedPropertyId }
    }

    // Map tab index to RequestStatus
    val tabToStatus = remember {
        mapOf(
            0 to RequestStatus.PENDING.label,
            1 to RequestStatus.IN_PROGRESS.label,
            2 to RequestStatus.COMPLETED.label,
        )
    }

    // Filter maintenance requests based on selected property and status
    val filteredRequests = remember(maintenanceRequests, selectedProperty, selectedTab) {
        when (maintenanceRequests) {
            is Response.Success -> {
                val requests = (maintenanceRequests as Response.Success<List<MaintenanceRequest>>).data
                val propertyFiltered = if (selectedProperty != null) {
                    requests.filter { it.propertyId == selectedProperty.id }
                } else {
                    emptyList()
                }
                // filter by status
                Response.Success(
                    propertyFiltered.filter {
                        it.status == tabToStatus[selectedTab]
                    },
                )
            }

            else -> maintenanceRequests
        }
    }

    var isInitialLoading by remember { mutableStateOf(true) }
    val expandedCardState = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedProperty?.id) {
        // Fetch maintenance requests when selected property changes
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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDropdown = true },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape,
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "Z",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        text = selectedProperty?.let {
                                            with(it.address) {
                                                "Block-${building} ${flatNo}"
                                            }
                                        } ?: "Select Location",
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                    )
                                }
                            }
                        },
                    )

                    // Only show tabs when dropdown is not visible
                    if (!showDropdown) {
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
                }
            },
            floatingActionButton = {
                // Only show FAB if property is active
                if (selectedProperty?.isActive() == true) {
                    FloatingActionButton(
                        onClick = onNavigateToMaintenanceRequest,
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create Maintenance Request")
                    }
                }
            },
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Only show main content when dropdown is not visible
                if (!showDropdown) {
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
                        when (val requests = filteredRequests) {
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
                                if (requests.data.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = when {
                                                selectedProperty == null -> "Please select a property"
                                                else -> "No ${tabToStatus[selectedTab]?.lowercase()} maintenance requests"
                                            },
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        state = listState,
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                                    ) {
                                        items(requests.data) { request ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
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

                // Property selection dropdown
                if (showDropdown) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = with(LocalDensity.current) { 64.dp }) // Height of TopAppBar
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                showDropdown = false
                            },
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { /* Prevent clicks from reaching the background */ },
                        ) {
                            items(propertyState.properties) { property ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            userState.user?.userId?.let { userId ->
                                                userViewModel.onEvent(UserEvent.SelectProperty(property.id))
                                            }
                                            showDropdown = false
                                        },
                                ) {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = with(property.address) {
                                                    buildString {
                                                        append("Block-${building}-$flatNo, ")
                                                        append(society)
                                                    }
                                                },
                                            )
                                        },
                                        trailingContent = if (property.id == userState.user?.selectedPropertyId) {
                                            {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(
                                                            color = MaterialTheme.colorScheme.primary,
                                                            shape = CircleShape,
                                                        ),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = "✔",
                                                        color = MaterialTheme.colorScheme.onError,
                                                        style = MaterialTheme.typography.labelMedium,
                                                    )
                                                }
                                            }
                                        } else null,
                                    )
                                    HorizontalDivider()
                                }
                            }

                            // Add Property option
                            item {
                                ListItem(
                                    headlineContent = { Text("Add Flat/Villa/Office") },
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add property",
                                            modifier = Modifier
                                                .background(
                                                    color = MaterialTheme.colorScheme.surface,
                                                    shape = CircleShape,
                                                )
                                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                                .padding(8.dp),
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        showDropdown = false
                                        onNavigateToAddProperty()
                                    },
                                )
                            }
                        }
                    }
                }

                // Show status message for non-active properties
                if (selectedProperty?.status == PropertyStatus.PENDING_APPROVAL) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "This property is pending approval. Maintenance requests will be available once approved.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
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
            imageVector = Icons.Filled.Add,
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
