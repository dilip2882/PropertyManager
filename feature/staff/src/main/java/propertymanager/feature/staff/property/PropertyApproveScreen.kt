package propertymanager.feature.staff.property

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.PropertyStatus
import com.propertymanager.domain.model.formatDate
import propertymanager.presentation.components.property.PropertyViewModel
import propertymanager.presentation.components.user.UserViewModel

@Composable
fun PropertyApproveScreen(
    propertyId: String,
    onNavigateUp: () -> Unit,
    propertyViewModel: PropertyViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val property by propertyViewModel.property.collectAsState()
    var showStatusDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf<PropertyStatus?>(null) }

    LaunchedEffect(Unit) {
//        propertyViewModel.getPropertyById(propertyId)
        propertyViewModel.loadProperties()
    }

    // Fetch tenant details when property is loaded
    LaunchedEffect(property) {
        property?.currentTenantId?.let { tenantId ->
            userViewModel.getUserInfo(tenantId)
        }
    }

    val tenantDetails by userViewModel.getUserData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Property Approval") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { showStatusDialog = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    ) {
                        Text("Change Status")
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                },
            )
        },
    ) { padding ->
        when {
            property == null || tenantDetails is Response.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            tenantDetails is Response.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (tenantDetails as Response.Error).message,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            else -> {
                val tenant = (tenantDetails as? Response.Success)?.data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState()),
                ) {
                    // Status Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (property?.status) {
                                PropertyStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
                                PropertyStatus.PENDING_APPROVAL -> MaterialTheme.colorScheme.tertiaryContainer
                                PropertyStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
                                PropertyStatus.EXPIRED -> MaterialTheme.colorScheme.surfaceVariant
                                null -> MaterialTheme.colorScheme.surface
                            },
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = property?.status?.label ?: "",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = "Request created on ${property?.createdAt?.toDate()?.formatDate()}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }

                    // Property Details
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Property Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            DetailRow(
                                label = "Flat/Villa No",
                                value = "${property?.address?.flatNo}",
                            )
                            DetailRow(
                                label = "Society",
                                value = "${property?.address?.society}",
                            )
                            DetailRow(
                                label = "City",
                                value = "${property?.address?.city}",
                            )
                            DetailRow(
                                label = "State",
                                value = "${property?.address?.state}",
                            )
                        }
                    }

                    // Tenant Details
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Tenant Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            DetailRow(
                                label = "Name",
                                value = tenant?.name ?: "N/A",
                            )
                            DetailRow(
                                label = "Email",
                                value = tenant?.email ?: "N/A",
                            )
                            DetailRow(
                                label = "Phone",
                                value = tenant?.phone ?: "N/A",
                            )
                        }
                    }
                }
            }
        }
    }

    // Status Change Dialog
    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Change Property Status") },
            text = {
                Column {
                    PropertyStatus.entries.forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedStatus = status }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selectedStatus == status,
                                onClick = { selectedStatus = status },
                            )
                            Text(
                                text = status.label,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedStatus?.let { status ->
                            propertyViewModel.updatePropertyStatus(propertyId, status)
                            showStatusDialog = false
                        }
                    },
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

