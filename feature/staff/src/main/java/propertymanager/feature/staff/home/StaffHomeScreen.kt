package propertymanager.feature.staff.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.model.PriorityLevel
import com.propertymanager.domain.model.RequestStatus
import com.propertymanager.domain.model.User
import com.propertymanager.domain.model.WorkerDetails
import com.propertymanager.domain.model.formatDate
import propertymanager.feature.staff.StaffViewModel
import propertymanager.presentation.components.user.UserViewModel
import androidx.compose.material3.rememberModalBottomSheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffHomeScreen(
    staffId: String,
    viewModel: StaffViewModel = hiltViewModel(),
    filterViewModel: MaintenanceFilterViewModel = hiltViewModel()
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    val assignedRequests by viewModel.assignedRequests.collectAsState()
    val filterState by filterViewModel.filterState.collectAsState()
    val updateStatusResponse by viewModel.updateStatusResponse.collectAsState()
    val updatePriorityResponse by viewModel.updatePriorityResponse.collectAsState()
    val assignWorkerResponse by viewModel.assignWorkerResponse.collectAsState()
    val context = LocalContext.current

    var showWorkerDialog by remember { mutableStateOf(false) }
    var selectedRequest by remember { mutableStateOf<MaintenanceRequest?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchAssignedRequests(staffId)
    }

    LaunchedEffect(updateStatusResponse, updatePriorityResponse, assignWorkerResponse) {
        when {
            updateStatusResponse is Response.Success -> {
                Toast.makeText(context, "Status updated successfully", Toast.LENGTH_SHORT).show()
                viewModel.resetResponses()
            }

            updatePriorityResponse is Response.Success -> {
                Toast.makeText(context, "Priority updated successfully", Toast.LENGTH_SHORT).show()
                viewModel.resetResponses()
            }

            assignWorkerResponse is Response.Success -> {
                Toast.makeText(context, "Worker assigned successfully", Toast.LENGTH_SHORT).show()
                viewModel.resetResponses()
            }
        }
    }

    // Filter and sort the requests based on filterState
    val filteredRequests = when (assignedRequests) {
        is Response.Success -> {
            var requests = (assignedRequests as Response.Success<List<MaintenanceRequest>>).data

            // Apply status filters
            if (filterState.selectedStatuses.isNotEmpty()) {
                requests = requests.filter { request ->
                    filterState.selectedStatuses.contains(request.status)
                }
            }

            // Apply priority filters
            if (filterState.selectedPriorities.isNotEmpty()) {
                requests = requests.filter { request ->
                    filterState.selectedPriorities.contains(request.priority)
                }
            }

            // Apply sorting
            requests = when (filterState.sortBy) {
                SortOption.ALPHABETICAL -> requests.sortedBy { it.issueDescription }
                SortOption.DATE_ADDED -> requests.sortedBy { it.createdAt }
                SortOption.LAST_UPDATED -> requests.sortedBy { it.updatedAt }
            }

            // Apply sort direction
            if (!filterState.isAscending) {
                requests = requests.reversed()
            }

            Response.Success(requests)
        }
        is Response.Loading -> Response.Loading
        is Response.Error -> assignedRequests
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff Dashboard") },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, "Filter")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (filteredRequests) {
                is Response.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }
                is Response.Success -> {
                    if (filteredRequests.data.isEmpty()) {
                        // Show empty state
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No maintenance requests found",
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (filterState.selectedStatuses.isNotEmpty() || 
                                filterState.selectedPriorities.isNotEmpty()
                            ) {
                                Text(
                                    text = "Try adjusting your filters",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(filteredRequests.data) { request ->
                                StaffMaintenanceCard(
                                    request = request,
                                    onStatusChange = { newStatus ->
                                        viewModel.updateRequestStatus(
                                            request.maintenanceRequestsId!!, 
                                            newStatus
                                        )
                                    },
                                    onPriorityChange = { newPriority ->
                                        viewModel.updateRequestPriority(
                                            request.maintenanceRequestsId!!, 
                                            newPriority
                                        )
                                    },
                                    onAssignWorker = {
                                        selectedRequest = request
                                        showWorkerDialog = true
                                    },
                                    userViewModel = hiltViewModel()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
                is Response.Error -> {
                    Text(
                        text = filteredRequests.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }

            if (showWorkerDialog && selectedRequest != null) {
                WorkerAssignmentDialog(
                    currentWorker = selectedRequest!!.workerDetails,
                    onDismiss = { showWorkerDialog = false },
                    onAssign = { workerDetails ->
                        viewModel.assignWorker(selectedRequest!!.maintenanceRequestsId!!, workerDetails)
                        showWorkerDialog = false
                    },
                )
            }

            if (showFilterSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showFilterSheet = false },
                    sheetState = bottomSheetState
                ) {
                    MaintenanceFilters()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun StaffMaintenanceCard(
    request: MaintenanceRequest,
    onStatusChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onAssignWorker: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    var statusExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    var tenantData by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(request.tenantId) {
        userViewModel.getUserDetailsByIdFlow(request.tenantId).collect { response ->
            when (response) {
                is Response.Success -> tenantData = response.data
                is Response.Error -> {
                    Log.e("StaffMaintenanceCard", "Error fetching tenant: ${response.message}")
                }

                is Response.Loading -> {
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // User Profile and Creation Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val imageUrl = tenantData?.imageUrl
                if (imageUrl.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile Icon",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = tenantData?.name ?: "Loading...",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Created at: ${request.createdAt.toDate().formatDate()}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Issue Details
            Text(
                text = request.issueDescription,
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 20.sp, fontWeight = FontWeight.Medium),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status Dropdown
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded },
            ) {
                OutlinedTextField(
                    value = request.status,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                    },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
                )

                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false },
                ) {
                    RequestStatus.getAllStatuses().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                onStatusChange(status)
                                statusExpanded = false
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Priority Dropdown
            ExposedDropdownMenuBox(
                expanded = priorityExpanded,
                onExpandedChange = { priorityExpanded = !priorityExpanded },
            ) {
                OutlinedTextField(
                    value = request.priority,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded)
                    },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
                )

                ExposedDropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false },
                ) {
                    PriorityLevel.getAllPriorities().forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority) },
                            onClick = {
                                onPriorityChange(priority)
                                priorityExpanded = false
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Assigned Worker Info
            if (request.workerDetails.name.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Assigned Worker",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(text = "Name: ${request.workerDetails.name}")
                        Text(text = "Phone: ${request.workerDetails.phone}")
                        Text(text = "Trade: ${request.workerDetails.trade}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                OutlinedButton(onClick = onAssignWorker) {
                    Icon(Icons.Default.Person, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (request.workerDetails.name.isEmpty()) "Assign Worker" else "Change Worker")
                }
            }
        }
    }
}

@Composable
fun WorkerAssignmentDialog(
    currentWorker: WorkerDetails,
    onDismiss: () -> Unit,
    onAssign: (WorkerDetails) -> Unit,
) {
    var name by remember { mutableStateOf(currentWorker.name) }
    var phone by remember { mutableStateOf(currentWorker.phone) }
    var trade by remember { mutableStateOf(currentWorker.trade) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Assign Worker",
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Worker Name") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = trade,
                    onValueChange = { trade = it },
                    label = { Text("Trade/Specialty") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onAssign(WorkerDetails(name, phone, trade))
                        },
                        enabled = name.isNotBlank() && phone.isNotBlank() && trade.isNotBlank(),
                    ) {
                        Text("Assign")
                    }
                }
            }
        }
    }
}
