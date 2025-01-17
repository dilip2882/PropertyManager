package propertymanager.presentation.components.location.components

import android.widget.Toast
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.location.Flat
import propertymanager.presentation.components.location.LocationEvent
import propertymanager.presentation.components.location.LocationManagerEvent
import propertymanager.presentation.components.location.LocationManagerViewModel
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.location.UiEvent
import propertymanager.presentation.screens.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlatManagerScreen(
    parentId: Int, // This can be blockId or towerId
    isBlock: Boolean, // True if managing flats for a block, false for a tower
    locationViewModel: LocationViewModel = hiltViewModel(),
    locationManagerViewModel: LocationManagerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var flatToEdit by remember { mutableStateOf<Flat?>(null) }
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

    // Load flats for selected block or tower
    LaunchedEffect(parentId) {
        if (isBlock) {
            locationViewModel.onEvent(LocationEvent.GetFlatsForBlock(parentId))
        } else {
            locationViewModel.onEvent(LocationEvent.GetFlatsForTower(parentId))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flat Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Add Flat")
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
                state.flats.forEach { flat ->
                    FlatItem(
                        flat = flat,
                        onEdit = { flatToEdit = flat },
                        onDelete = {
                            locationManagerViewModel.onEvent(
                                LocationManagerEvent.DeleteFlat(flat.id)
                            )
                        },
                        onClick = {

                        }
                    )
                }
            }
        }

        // Add Flat Dialog
        if (showAddDialog) {
            AddFlatDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { flatName ->
                    val newFlat = Flat(
                        id = 0,
                        blockId = if (isBlock) parentId else null,
                        towerId = if (!isBlock) parentId else null,
                        societyId = if (isBlock) state.selectedBlock?.societyId ?: 0 else state.selectedTower?.societyId ?: 0,
                        number = flatName
                    )
                    locationManagerViewModel.onEvent(
                        LocationManagerEvent.AddFlat(
                            flat = newFlat
                        )
                    )
                    showAddDialog = false
                }
            )
        }

        // Edit Flat Dialog
        flatToEdit?.let { flat ->
            EditFlatDialog(
                flat = flat,
                onDismiss = { flatToEdit = null },
                onConfirm = { updatedFlat ->
                    locationManagerViewModel.onEvent(
                        LocationManagerEvent.UpdateFlat(
                            updatedFlat
                        )
                    )
                    flatToEdit = null
                }
            )
        }
    }
}

@Composable
fun AddFlatDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Flat") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Flat Name") },
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
fun EditFlatDialog(
    flat: Flat,
    onDismiss: () -> Unit,
    onConfirm: (Flat) -> Unit
) {
    var number by remember { mutableStateOf(flat.number) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Flat") },
        text = {
            OutlinedTextField(
                value = number,
                onValueChange = { number = it },
                label = { Text("Flat Number") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(flat.copy(number = number)) },
                enabled = number.isNotBlank()
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
fun FlatItem(
    flat: Flat,
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
            Text(text = flat.number)
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
