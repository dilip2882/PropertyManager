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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.propertymanager.domain.model.Property
import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.Tower
import propertymanager.presentation.components.location.LocationDropdown
import propertymanager.presentation.components.location.LocationEvent
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.location.UiEvent
import propertymanager.presentation.components.user.UserViewModel
import propertymanager.presentation.components.user.UserEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    propertyViewModel: PropertyViewModel,
    userViewModel: UserViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel,
    onPropertyAdded: () -> Unit,
    onNavigateToSelectCountry: () -> Unit,
    onNavigateToSelectState: () -> Unit,
    onNavigateToSelectCity: () -> Unit,
    onNavigateToSelectSociety: () -> Unit,
    onNavigateToSelectFlat: (Int, Property.Building) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val locationState by locationViewModel.state.collectAsState()
    val propertyState by propertyViewModel.state.collectAsState()
    val userState by userViewModel.state.collectAsState()
    var showBuildingDialog by remember { mutableStateOf(false) }
    var selectedUserType by remember { mutableStateOf<UserType?>(null) }

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
            // Country Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSelectCountry() }
                    .padding(vertical = 4.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Country",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = locationState.selectedCountry?.name ?: "Select Country",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Select Country")
                }
            }

            // State Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = locationState.selectedCountry != null) {
                        onNavigateToSelectState()
                    }
                    .padding(vertical = 4.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "State",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = locationState.selectedState?.name ?: "Select State",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Select State")
                }
            }

            // City Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = locationState.selectedState != null) {
                        onNavigateToSelectCity()
                    }
                    .padding(vertical = 4.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "City",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = locationState.selectedCity?.name ?: "Select City",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Select City")
                }
            }

            // Society Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = locationState.selectedCity != null) {
                        onNavigateToSelectSociety()
                    }
                    .padding(vertical = 4.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Society",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = locationState.selectedSociety?.name ?: "Select Society",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Select Society")
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
                                    is Block -> onNavigateToSelectFlat(item.id, Property.Building.BLOCK)
                                    is Tower -> onNavigateToSelectFlat(item.id, Property.Building.TOWER)
                                }
                            },
                        )
                    } else {
                        // Show building list
                        locationState.blocks.forEach { block ->
                            BuildingItem(
                                name = block.name,
                                icon = Icons.Default.Business,
                                onClick = { onNavigateToSelectFlat(block.id, Property.Building.BLOCK) },
                            )
                        }
                        locationState.towers.forEach { tower ->
                            BuildingItem(
                                name = tower.name,
                                icon = Icons.Default.LocationCity,
                                onClick = { onNavigateToSelectFlat(tower.id, Property.Building.TOWER) },
                            )
                        }
                    }
                }
            }

            // Building Type Dialog
            if (showBuildingDialog) {
                AlertDialog(
                    onDismissRequest = { showBuildingDialog = false },
                    title = { Text("Select Building Type") },
                    text = {
                        Column {
                            Property.Building.values().forEach { building ->
                                TextButton(
                                    onClick = {
                                        showBuildingDialog = false
                                        locationState.selectedSociety?.id?.let { societyId ->
                                            onNavigateToSelectFlat(societyId, building)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(building.name)
                                }
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { showBuildingDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Flat Selection
            locationState.selectedFlat?.let { flat ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Selected Flat",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "Flat ${flat.number}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            // User Type Selection
            if (locationState.selectedFlat != null) {
                Text(
                    text = "Select User Type",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                UserType.entries.forEach { userType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedUserType = userType },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedUserType == userType,
                            onClick = { selectedUserType = userType }
                        )
                        Text(
                            text = userType.name,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Add Property Button
            if (locationState.selectedFlat != null) {
                Button(
                    onClick = {
                        val property = Property(
                            id = "", // Will be set by Firestore
                            address = Property.Address(
                                country = locationState.selectedCountry?.name ?: "",
                                state = locationState.selectedState?.name ?: "",
                                city = locationState.selectedCity?.name ?: "",
                                society = locationState.selectedSociety?.name ?: "",
                                flatNo = locationState.selectedFlat?.number ?: "",
                                building = when {
                                    locationState.selectedBlock != null -> Property.Building.BLOCK
                                    locationState.selectedTower != null -> Property.Building.TOWER
                                    else -> Property.Building.FLAT
                                }
                            ),
                            ownerId = Firebase.auth.currentUser?.uid ?: "",
                            createdAt = Timestamp.now()
                        )

                        propertyViewModel.onEvent(PropertyEvent.AddProperty(property))
                        onPropertyAdded()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = locationState.selectedFlat != null && !propertyState.isLoading
                ) {
                    if (propertyState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Add Property")
                    }
                }
            }
        }
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
