package propertymanager.presentation.components.property

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.Tower
import propertymanager.presentation.components.location.LocationDropdown
import propertymanager.presentation.components.location.LocationEvent
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.location.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    viewModel: PropertyViewModel,
    locationViewModel: LocationViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSelectFlat: (Int) -> Unit,
    onNavigateToSelectSociety: () -> Unit,
    onPropertyAdded: () -> Unit,
) {
    val locationState by locationViewModel.state.collectAsState()
    val uiState by locationViewModel.uiEvent.collectAsState(initial = null)
    var selectedUserType by remember { mutableStateOf<UserType?>(null) }

    uiState?.let { event ->
        when (event) {
            is UiEvent.Error -> {
                // error message
                Toast.makeText(LocalContext.current, event.message, Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                title = { Text("Add Home") },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Country",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp),
            )

            LocationDropdown(
                label = "Select Country",
                items = locationState.countries,
                selectedItem = locationState.selectedCountry,
                onItemSelected = { country ->
                    locationViewModel.onEvent(LocationEvent.SelectCountry(country))
                },
            )

            // State Dropdown
            AnimatedVisibility(visible = locationState.selectedCountry != null) {
                Column {
                    if (locationState.isLoading) {
                        // Show loading indicator
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else if (locationState.states.isEmpty()) {
                        // Show empty state
                        Text(
                            text = "No states found for selected country",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp),
                        )
                    } else {
                        LocationDropdown(
                            label = "Select State",
                            items = locationState.states,
                            selectedItem = locationState.selectedState,
                            onItemSelected = { state ->
                                locationViewModel.onEvent(LocationEvent.SelectState(state))
                            },
                        )
                    }
                }
            }


            AnimatedVisibility(visible = locationState.selectedCountry != null) {
                Column {
                    Text(
                        text = "City",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                    LocationDropdown(
                        label = "Select City",
                        items = locationState.cities,
                        selectedItem = locationState.selectedCity,
                        onItemSelected = { city ->
                            locationViewModel.onEvent(LocationEvent.SelectCity(city))
                        },
                    )
                }
            }

            // Society Selection
            AnimatedVisibility(visible = locationState.selectedCity != null) {
                Column {
                    Text(
                        text = "Society",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                    Surface(
                        onClick = { onNavigateToSelectSociety() },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        OutlinedTextField(
                            value = locationState.selectedSociety?.name ?: "",
                            onValueChange = { },
                            readOnly = true,
                            enabled = false,
                            label = { Text("Select Society") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Select Society",
                                )
                            },
                        )
                    }
                }
            }

            // Building Selection
            AnimatedVisibility(visible = locationState.selectedSociety != null) {
                Column {
                    Text(
                        text = "Building",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                    if (locationState.selectedBlock != null || locationState.selectedTower != null) {
                        // Show selected building in dropdown style
                        LocationDropdown(
                            label = locationState.selectedBlock?.name
                                ?: locationState.selectedTower?.name
                                ?: "Select Building",
                            items = locationState.blocks + locationState.towers,  // Combine blocks and towers
                            selectedItem = locationState.selectedBlock ?: locationState.selectedTower,
                            onItemSelected = { item ->
                                when (item) {
                                    is Block -> onNavigateToSelectFlat(item.id)
                                    is Tower -> onNavigateToSelectFlat(item.id)
                                }
                            },
                        )
                    } else {
                        // Show building list
                        locationState.blocks.forEach { block ->
                            BuildingItem(
                                name = block.name,
                                icon = Icons.Default.Business,
                                onClick = { onNavigateToSelectFlat(block.id) },
                            )
                        }
                        locationState.towers.forEach { tower ->
                            BuildingItem(
                                name = tower.name,
                                icon = Icons.Default.LocationCity,
                                onClick = { onNavigateToSelectFlat(tower.id) },
                            )
                        }
                    }
                }
            }

            // Flat Selection
            AnimatedVisibility(visible = locationState.selectedFlat != null) {
                Column {
                    Text(
                        text = "Flat No.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                    LocationDropdown(
                        label = locationState.selectedFlat?.number ?: "Select Flat",
                        items = locationState.flats,
                        selectedItem = locationState.selectedFlat,
                        onItemSelected = { flat ->
                            locationViewModel.onEvent(LocationEvent.SelectFlat(flat))
                        },
                    )
                }
            }

            // User Type Selection
            AnimatedVisibility(visible = locationState.selectedFlat != null) {
                Column {
                    Text(
                        text = "You are",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                    UserTypeRadioGroup(
                        selectedType = selectedUserType,
                        onTypeSelected = { selectedUserType = it },
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onPropertyAdded,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = locationState.selectedFlat != null && selectedUserType != null,
            ) {
                Text("Add Flat/Villa")
            }
        }
    }
}

enum class UserType {
    FLAT_OWNER,
    RENTING_WITH_FAMILY,
    RENTING_WITH_FLATMATES
}

@Composable
private fun UserTypeRadioGroup(
    selectedType: UserType?,
    onTypeSelected: (UserType) -> Unit,
) {
    Column {
        RadioOption(
            text = "Flat Owner",
            selected = selectedType == UserType.FLAT_OWNER,
            onClick = { onTypeSelected(UserType.FLAT_OWNER) },
        )
        RadioOption(
            text = "Renting with family",
            selected = selectedType == UserType.RENTING_WITH_FAMILY,
            onClick = { onTypeSelected(UserType.RENTING_WITH_FAMILY) },
        )
        RadioOption(
            text = "Renting with other flatmates",
            selected = selectedType == UserType.RENTING_WITH_FLATMATES,
            onClick = { onTypeSelected(UserType.RENTING_WITH_FLATMATES) },
        )
    }
}

@Composable
private fun RadioOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

@Composable
private fun BuildingItem(
    name: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = name)
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Select",
            )
        }
    }
}
