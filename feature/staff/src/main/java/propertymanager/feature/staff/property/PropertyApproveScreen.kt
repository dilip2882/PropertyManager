package propertymanager.feature.staff.property

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.Property
import com.propertymanager.domain.model.PropertyStatus
import propertymanager.presentation.components.property.PropertyViewModel
import propertymanager.presentation.components.user.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyApproveScreen(
    onNavigateBack: () -> Unit,
    viewModel: PropertyViewModel = hiltViewModel(),
) {
    val properties by viewModel.getAllProperties().collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var showStatusSheet by remember { mutableStateOf<Property?>(null) }
    val bottomSheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Property Approvals") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search properties") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                singleLine = true,
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    properties.filter {
                        val searchText = with(it.address) {
                            "${flatNo}, ${society}"
                        }
                        searchText.contains(searchQuery, ignoreCase = true)
                    },
                ) { property ->
                    PropertyApprovalItem(
                        property = property,
                        onStatusChange = { showStatusSheet = property },
                    )
                    HorizontalDivider()
                }
            }
        }

        // Bottom Sheet
        showStatusSheet?.let { property ->
            ModalBottomSheet(
                onDismissRequest = { showStatusSheet = null },
                sheetState = bottomSheetState,
            ) {
                PropertyStatusBottomSheet(
                    currentStatus = property.status,
                    onStatusSelected = { newStatus ->
                        viewModel.updatePropertyStatus(property.id, newStatus)
                        showStatusSheet = null
                    },
                )
            }
        }
    }
}

@Composable
private fun PropertyApprovalItem(
    property: Property,
    onStatusChange: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val ownerState by userViewModel.getUserById(property.ownerId).collectAsState(initial = null)
    val tenantState by userViewModel.getUserById(property.currentTenantId).collectAsState(initial = null)

    ListItem(
        headlineContent = {
            Text(
                text = with(property.address) {
                    buildString {
                        append(flatNo)
                        if (building != null) {
                            append(", $building")
                        }
                    }
                },
                style = MaterialTheme.typography.titleMedium,
            )
        },
        supportingContent = {
            Column {
                Text(
                    text = property.address.society,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Owner name
                ownerState?.let { owner ->
                    Text(
                        text = "Tenant: ${owner.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                // Current Tenant name
                tenantState?.let { tenant ->
                    Text(
                        text = "Current Tenant: ${tenant.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatusChip(status = property.status)
                }
            }
        },
        trailingContent = {
            IconButton(onClick = onStatusChange) {
                Icon(Icons.Default.Edit, "Change Status")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onStatusChange),
    )
}

@Composable
private fun StatusChip(status: PropertyStatus) {
    val (backgroundColor, contentColor) = when (status) {
        PropertyStatus.PENDING_APPROVAL -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        PropertyStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        PropertyStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        PropertyStatus.EXPIRED -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(end = 8.dp),
    ) {
        Text(
            text = status.name,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun PropertyStatusBottomSheet(
    currentStatus: PropertyStatus,
    onStatusSelected: (PropertyStatus) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = "Change Status",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        PropertyStatus.entries.forEach { status ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStatusSelected(status) }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = status == currentStatus,
                    onClick = { onStatusSelected(status) },
                )

                Column {
                    Text(
                        text = status.name,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = getStatusDescription(status),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (status != PropertyStatus.entries.last()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

private fun getStatusDescription(status: PropertyStatus): String {
    return when (status) {
        PropertyStatus.PENDING_APPROVAL -> "Property is waiting for approval"
        PropertyStatus.ACTIVE -> "Property will be approved"
        PropertyStatus.REJECTED -> "Property will be rejected"
        PropertyStatus.EXPIRED -> "Property will expire"
    }
}

