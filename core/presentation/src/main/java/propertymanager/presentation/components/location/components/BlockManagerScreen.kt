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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.propertymanager.domain.model.location.Block
import propertymanager.presentation.components.location.LocationEvent
import propertymanager.presentation.components.location.LocationManagerEvent
import propertymanager.presentation.components.location.LocationManagerViewModel
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.location.UiEvent
import propertymanager.presentation.screens.LoadingScreen

@Composable
fun BlockManagerScreen(
    societyId: Int,
    locationViewModel: LocationViewModel = hiltViewModel(),
    locationManagerViewModel: LocationManagerViewModel = hiltViewModel(),
    onNavigateToTower: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var blockToEdit by remember { mutableStateOf<Block?>(null) }
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

    // Load blocks for selected society
    LaunchedEffect(societyId) {
        locationViewModel.onEvent(LocationEvent.GetBlocksForSociety(societyId))
        state.societies.find { it.id == societyId }?.let { selectedSociety ->
            locationViewModel.onEvent(LocationEvent.SelectSociety(selectedSociety))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Block Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Add Block")
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
                state.blocks.forEach { block ->
                    BlockItem(
                        block = block,
                        onEdit = { blockToEdit = block },
                        onDelete = { 
                            locationManagerViewModel.onEvent(
                                LocationManagerEvent.DeleteBlock(block.id)
                            )
                        },
                        onClick = { onNavigateToTower(block.id) }
                    )
                }
            }
        }

        // Add Block Dialog
        if (showAddDialog) {
            AddBlockDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { blockName ->
                    val newBlock = Block(
                        id = 0,
                        societyId = societyId,
                        name = blockName
                    )
                    locationManagerViewModel.onEvent(
                        LocationManagerEvent.AddBlock(
                            block = newBlock
                        )
                    )
                    showAddDialog = false
                }
            )
        }

        // Edit Block Dialog
        blockToEdit?.let { block ->
            EditBlockDialog(
                block = block,
                onDismiss = { blockToEdit = null },
                onConfirm = { updatedBlock ->
                    locationManagerViewModel.onEvent(
                        LocationManagerEvent.UpdateBlock(
                            updatedBlock
                        )
                    )
                    blockToEdit = null
                }
            )
        }
    }
}

@Composable
fun BlockItem(
    block: Block,
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
            Text(text = block.name)
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
fun AddBlockDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Block") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Block Name") },
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
fun EditBlockDialog(
    block: Block,
    onDismiss: () -> Unit,
    onConfirm: (Block) -> Unit
) {
    var name by remember { mutableStateOf(block.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Block") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Block Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(block.copy(name = name)) },
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
