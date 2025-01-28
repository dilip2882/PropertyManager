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
import com.propertymanager.domain.model.location.Society
import propertymanager.presentation.components.location.LocationEvent
import propertymanager.presentation.components.location.LocationManagerEvent
import propertymanager.presentation.components.location.LocationManagerViewModel
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.location.UiEvent
import propertymanager.presentation.screens.LoadingScreen


@Composable
fun SocietyManagerScreen(
    cityId: Int,
    locationViewModel: LocationViewModel = hiltViewModel(),
    locationManagerViewModel: LocationManagerViewModel = hiltViewModel(),
    onNavigateToBlock: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var societyToEdit by remember { mutableStateOf<Society?>(null) }
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

    // Load societies for selected city
    LaunchedEffect(cityId) {
        locationViewModel.onEvent(LocationEvent.GetSocietiesForCity(cityId))
        state.cities.find { it.id == cityId }?.let { selectedCity ->
            locationViewModel.onEvent(LocationEvent.SelectCity(selectedCity))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Society Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Add Society")
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
                state.societies.forEach { society ->
                    SocietyItem(
                        society = society,
                        onEdit = { societyToEdit = society },
                        onDelete = { 
                            locationManagerViewModel.onEvent(
                                LocationManagerEvent.DeleteSociety(society.id)
                            )
                        },
                        onClick = { onNavigateToBlock(society.id) }
                    )
                }
            }
        }

        // Add Society Dialog
        if (showAddDialog) {
            AddSocietyDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { societyName ->
                    val newSociety = Society(
                        id = 0,
                        cityId = cityId,
                        countryId = 0,
                        stateId = 0,
                        name = societyName
                    )
                    locationManagerViewModel.onEvent(
                        LocationManagerEvent.AddSociety(
                            cityId = cityId,
                            society = newSociety)
                    )
                    showAddDialog = false
                }
            )
        }

        // Edit Society Dialog
        societyToEdit?.let { society ->
            EditSocietyDialog(
                society = society,
                onDismiss = { societyToEdit = null },
                onConfirm = { updatedSociety ->
                    locationManagerViewModel.onEvent(
                        LocationManagerEvent.UpdateSociety(updatedSociety)
                    )
                    societyToEdit = null
                }
            )
        }
    }
}

@Composable
fun SocietyItem(
    society: Society,
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
            Text(text = society.name)
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

@Composable
private fun AddSocietyDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Society") },
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
fun EditSocietyDialog(
    society: Society,
    onDismiss: () -> Unit,
    onConfirm: (Society) -> Unit
) {
    var name by remember { mutableStateOf(society.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Society") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Society Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(society.copy(name = name)) },
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
