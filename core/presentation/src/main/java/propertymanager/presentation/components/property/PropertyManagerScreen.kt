package propertymanager.presentation.components.property

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.Property
import com.propertymanager.domain.model.PropertyStatus
import propertymanager.presentation.components.property.components.PropertyList
import propertymanager.presentation.theme.PropertyManagerIcons

@Composable
fun PropertyManagerScreen(
    onNavigateToAddProperty: () -> Unit,
    onNavigateToEditProperty: (Property) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PropertyViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<PropertyStatus?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Property Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(PropertyManagerIcons.ArrowBack, "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddProperty,
                icon = {
                    Icon(PropertyManagerIcons.Add, "Add Property")
                },
                text = { Text("Add Property") },

                )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Filter and Search Surface
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 0.dp,
            ) {
                Column {
                    // Status Filter Chips
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            selected = selectedStatus == null,
                            onClick = {
                                selectedStatus = null
                                viewModel.onEvent(PropertyEvent.StatusFilterChanged(null))
                            },
                            label = { Text("All") },
                            leadingIcon = if (selectedStatus == null) {
                                {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                                    )
                                }
                            } else null,
                        )

                        PropertyStatus.entries.forEach { status ->
                            FilterChip(
                                selected = selectedStatus == status,
                                onClick = {
                                    selectedStatus = status
                                    viewModel.onEvent(PropertyEvent.StatusFilterChanged(status))
                                },
                                label = { Text(status.label) },
                                leadingIcon = if (selectedStatus == status) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        )
                                    }
                                } else null,
                            )
                        }
                    }

                    // Search TextField
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.onEvent(PropertyEvent.SearchQueryChanged(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Search properties...") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(percent = 50),
                    )
                }
            }

            // Property List
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                PropertyList(
                    properties = state.properties,
                    onEditProperty = onNavigateToEditProperty,
                    onDeleteProperty = { property ->
                        viewModel.onEvent(PropertyEvent.DeleteProperty(property))
                    },
                )
            }
        }

        // Error Handling
        state.error?.let { error ->
            LaunchedEffect(error) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                viewModel.onEvent(PropertyEvent.ClearError)
            }
        }
    }
}
