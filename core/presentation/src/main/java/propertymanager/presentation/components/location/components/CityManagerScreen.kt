package propertymanager.presentation.components.location.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import com.propertymanager.domain.model.location.City
import propertymanager.presentation.components.location.LocationEvent
import propertymanager.presentation.components.location.LocationManagerEvent
import propertymanager.presentation.components.location.LocationManagerViewModel
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.location.UiEvent
import propertymanager.presentation.screens.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityManagerScreen(
    stateId: Int,
    locationViewModel: LocationViewModel = hiltViewModel(),
    locationManagerViewModel: LocationManagerViewModel = hiltViewModel(),
    onNavigateToLocation: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var cityToEdit by remember { mutableStateOf<City?>(null) }
    val state by locationViewModel.state.collectAsState()
    val managerState by locationManagerViewModel.state.collectAsState()
    val context = LocalContext.current

    // UI events
    LaunchedEffect(true) {
        locationManagerViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Load cities for selected state
    LaunchedEffect(stateId) {
        locationViewModel.onEvent(LocationEvent.GetCitiesForState(stateId))
        state.states.find { it.id == stateId }?.let { selectedState ->
            locationViewModel.onEvent(LocationEvent.SelectState(selectedState))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("City Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Add City")
                    }
                }
            )
        }
    ) { padding ->
        if (managerState.isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                state.cities.forEach { city ->
                    CityItem(
                        city = city,
                        onEdit = { cityToEdit = city },
                        onDelete = { 
                            locationManagerViewModel.onEvent(
                                LocationManagerEvent.DeleteCity(city.id)
                            )
                        },
                        onClick = { onNavigateToLocation(city.id) }
                    )
                }
            }
        }

        // Add City Dialog
        if (showAddDialog) {
            AddCityDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { cityName ->
                    val newCity = City(
                        id = 0,
                        countryId = state.selectedCountry?.id ?: 0,
                        stateId = stateId,
                        name = cityName
                    )
                    locationManagerViewModel.onEvent(
                        LocationManagerEvent.AddCity(
                            countryId = state.selectedCountry?.id ?: 0,
                            stateId = stateId,
                            city = newCity
                        )
                    )
                    showAddDialog = false
                }
            )
        }

        // Edit City Dialog
        cityToEdit?.let { city ->
            EditCityDialog(
                city = city,
                onDismiss = { cityToEdit = null },
                onConfirm = { updatedCity ->
                    locationManagerViewModel.onEvent(
                        LocationManagerEvent.UpdateCity(
                            countryId = state.selectedCountry?.id ?: 0,
                            stateId = stateId,
                            city = updatedCity
                        )
                    )
                    cityToEdit = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCityDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add City") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("City Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditCityDialog(
    city: City,
    onDismiss: () -> Unit,
    onConfirm: (City) -> Unit
) {
    var name by remember { mutableStateOf(city.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit City") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("City Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    onConfirm(city.copy(name = name))
                },
                enabled = name.isNotBlank()
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CityItem(
    city: City,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = city.name)
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete")
                }
            }
        }
    }
}
