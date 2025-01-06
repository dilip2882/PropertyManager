package propertymanager.feature.tenant.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Property
import propertymanager.presentation.components.property.PropertyViewModel
import propertymanager.presentation.screens.LoadingScreen
import java.util.UUID

@Composable
fun TenantHomeScreen(
    propertyViewModel: PropertyViewModel,
    onNavigateToAddProperty: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDropdown by remember { mutableStateOf(false) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    val propertiesResponse by propertyViewModel.propertiesResponse.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location Selector
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showDropdown = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        selectedProperty != null -> with(selectedProperty!!.address) {
                            if (flatNo.isNotEmpty() && building.isNotEmpty()) {
                                "$flatNo, $building, $society"
                            } else {
                                society
                            }
                        }
                        else -> "Select Location"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (showDropdown)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle dropdown"
                )
            }

            // Action Icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Search action */ }) {
                    Icon(Icons.Default.Search, "Search")
                }
                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "N",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        // Property Selection Dropdown
        if (showDropdown) {
            PropertySelectionDropdown(
                propertiesResponse = propertiesResponse,
                onPropertySelected = { property ->
                    selectedProperty = property
                    showDropdown = false
                },
                onDismiss = { showDropdown = false },
                onAddNewProperty = {
                    showDropdown = false
                    onNavigateToAddProperty()
                }
            )
        }
    }
}

@Composable
private fun PropertySelectionDropdown(
    propertiesResponse: Response<List<Property>>,
    onPropertySelected: (Property) -> Unit,
    onDismiss: () -> Unit,
    onAddNewProperty: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            when (propertiesResponse) {
                is Response.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingScreen()
                    }
                }

                is Response.Error -> {
                    Text(
                        text = propertiesResponse.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is Response.Success -> {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(propertiesResponse.data) { property ->
                            PropertyListItem(
                                property = property,
                                onClick = { onPropertySelected(property) }
                            )
                        }
                        item {
                            AddPropertyButton(onClick = onAddNewProperty)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertyListItem(
    property: Property,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = with(property.address) {
                    if (flatNo.isNotEmpty() && building.isNotEmpty()) {
                        "$flatNo, $building, $society"
                    } else {
                        society
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = "${property.address.city}, ${property.address.state}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider()
}

@Composable
private fun AddPropertyButton(
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text("Add Flat/Villa/Office") },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add property"
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
