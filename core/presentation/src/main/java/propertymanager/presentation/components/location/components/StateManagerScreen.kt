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
import com.propertymanager.domain.model.location.State
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import propertymanager.presentation.components.location.LocationEvent
import propertymanager.presentation.components.location.LocationManagerEvent
import propertymanager.presentation.components.location.LocationManagerViewModel
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.location.UiEvent
import propertymanager.presentation.screens.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateManagerScreen(
    countryId: Int,
    locationViewModel: LocationViewModel = hiltViewModel(),
    locationManagerViewModel: LocationManagerViewModel = hiltViewModel(),
    onNavigateToCity: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var stateToEdit by remember { mutableStateOf<State?>(null) }
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

    // Load states for selected country
    LaunchedEffect(countryId, state.countries) {
        val selectedCountry = state.countries.find { it.id == countryId }
        selectedCountry?.let { country ->
            locationViewModel.onEvent(LocationEvent.SelectCountry(country))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("State Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Add State")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                state.states.forEach { currentState ->
                    StateItem(
                        state = currentState,
                        onEdit = { stateToEdit = currentState },
                        onDelete = {
                            locationManagerViewModel.onEvent(
                                LocationManagerEvent.DeleteState(
                                    stateId = currentState.id,
                                )
                            )
                        },
                        onClick = { onNavigateToCity(currentState.id) }
                    )
                }
            }
        }

        // Add State Dialog
        if (showAddDialog) {
            AddStateDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name ->
                    val newState = State(
                        id = 0,
                        countryId = countryId,
                        name = name,
                        stateCode = ""
                    )
                    locationManagerViewModel.onEvent(
                        LocationManagerEvent.AddState(countryId = countryId, state = newState)
                    )
                    showAddDialog = false
                }
            )
        }

        // Edit State Dialog
        stateToEdit?.let { state ->
            EditStateDialog(
                state = state,
                onDismiss = { stateToEdit = null },
                onConfirm = { updatedState ->
                    locationManagerViewModel.onEvent(
                        LocationManagerEvent.UpdateState(countryId = countryId, state = updatedState)
                    )
                    stateToEdit = null
                }
            )
        }
    }
}

@Composable
private fun AddStateDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add State") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("State Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank(),
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}


@Composable
private fun EditStateDialog(
    state: State,
    onDismiss: () -> Unit,
    onConfirm: (State) -> Unit,
) {
    var name by remember { mutableStateOf(state.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit State") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("State Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(state.copy(name = name))
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
        },
    )
}



@Composable
private fun StateItem(
    state: State,
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
            Column {
                Text(
                    text = state.name,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
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
