package propertymanager.presentation.components.property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.propertymanager.domain.model.location.Flat
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.location.LocationEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectFlatScreen(
    parentId: Int, // Block or Tower ID
    locationViewModel: LocationViewModel,
    onNavigateBack: () -> Unit,
    onFlatSelected: (Flat) -> Unit
) {
    val locationState by locationViewModel.state.collectAsState()

    LaunchedEffect(parentId) {
        // Check if parent is in blocks or towers
        val isBlock = locationState.blocks.any { it.id == parentId }
        if (isBlock) {
            locationViewModel.loadFlatsForBlock(parentId)
        } else {
            locationViewModel.loadFlatsForTower(parentId)
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
                title = { Text("Select Flat") },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            locationState.flats.forEach { flat ->
                FlatItem(
                    flat = flat,
                    onClick = { 
                        locationViewModel.onEvent(LocationEvent.SelectFlat(flat))
                        onFlatSelected(flat)
                    }
                )
            }
        }
    }
}

@Composable
private fun FlatItem(
    flat: Flat,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Flat ${flat.number}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Floor ${flat.floor}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Select"
            )
        }
    }
} 
