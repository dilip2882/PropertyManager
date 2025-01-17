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
import com.propertymanager.domain.model.location.Tower
import propertymanager.presentation.components.location.LocationEvent
import propertymanager.presentation.components.location.LocationManagerEvent
import propertymanager.presentation.components.location.LocationManagerViewModel
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.location.UiEvent
import propertymanager.presentation.screens.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TowerManagerScreen(
    societyId: Int,
    locationViewModel: LocationViewModel = hiltViewModel(),
    locationManagerViewModel: LocationManagerViewModel = hiltViewModel(),
    onNavigateToFlat: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var towerToEdit by remember { mutableStateOf<Tower?>(null) }
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

    // Load towers for selected society
    LaunchedEffect(societyId) {
        locationViewModel.onEvent(LocationEvent.GetTowersForSociety(societyId))
        state.societies.find { it.id == societyId }?.let { selectedSociety ->
            locationViewModel.onEvent(LocationEvent.SelectSociety(selectedSociety))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tower Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Add Tower")
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
                state.towers.forEach { tower ->
                    TowerItem(
                        tower = tower,
                        onEdit = { towerToEdit = tower },
                        onDelete = {
                            locationManagerViewModel.onEvent(
                                LocationManagerEvent.DeleteTower(tower.id)
                            )
                        },
                        onClick = { onNavigateToFlat(tower.id) }
                    )
                }
            }
        }

        // Add Tower Dialog
        if (showAddDialog) {
            AddTowerDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { towerName ->
                    val newTower = Tower(
                        id = 0,
                        societyId = societyId,
                        blockId = 0,
                        name = towerName
                    )
                    locationManagerViewModel.onEvent(
                        LocationManagerEvent.AddTower(
                            tower = newTower)
                    )
                    showAddDialog = false
                }
            )
        }

        // Edit Tower Dialog
        towerToEdit?.let { tower ->
            EditTowerDialog(
                tower = tower,
                onDismiss = { towerToEdit = null },
                onConfirm = { updatedTower ->
                    locationManagerViewModel.onEvent(
                        LocationManagerEvent.UpdateTower(
                            updatedTower)
                    )
                    towerToEdit = null
                }
            )
        }
    }
}

@Composable
fun AddTowerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Tower") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tower Name") },
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
fun EditTowerDialog(
    tower: Tower,
    onDismiss: () -> Unit,
    onConfirm: (Tower) -> Unit
) {
    var name by remember { mutableStateOf(tower.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Tower") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tower Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(tower.copy(name = name)) },
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
fun TowerItem(
    tower: Tower,
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
            Text(text = tower.name)
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
