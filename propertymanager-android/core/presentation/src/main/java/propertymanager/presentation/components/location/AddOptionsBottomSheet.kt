import propertymanager.presentation.components.location.LocationManagerState

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOptionsBottomSheet(
    state: LocationManagerState,
    onDismiss: () -> Unit,
    onOptionSelected: (DialogType) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Add New",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                state.selectedTower != null -> {
                    // Can only add flats to a tower
                    AddOption(
                        text = "Add Flat",
                        icon = Icons.Default.Home,
                        onClick = { onOptionSelected(DialogType.ADD_FLAT) }
                    )
                }
                state.selectedBlock != null -> {
                    // Can only add flats to a block
                    AddOption(
                        text = "Add Flat",
                        icon = Icons.Default.Home,
                        onClick = { onOptionSelected(DialogType.ADD_FLAT) }
                    )
                }
                state.selectedSociety != null -> {
                    // Can add blocks, towers, or flats to a society
                    AddOption(
                        text = "Add Block",
                        icon = Icons.Default.Business,
                        onClick = { onOptionSelected(DialogType.ADD_BLOCK) }
                    )
                    AddOption(
                        text = "Add Tower",
                        icon = Icons.Default.LocationCity,
                        onClick = { onOptionSelected(DialogType.ADD_TOWER) }
                    )
                    AddOption(
                        text = "Add Flat",
                        icon = Icons.Default.Home,
                        onClick = { onOptionSelected(DialogType.ADD_FLAT) }
                    )
                }
                state.selectedCity != null -> {
                    AddOption(
                        text = "Add Society",
                        icon = Icons.Default.LocationOn,
                        onClick = { onOptionSelected(DialogType.ADD_SOCIETY) }
                    )
                }
                state.selectedState != null -> {
                    AddOption(
                        text = "Add City",
                        icon = Icons.Default.LocationCity,
                        onClick = { onOptionSelected(DialogType.ADD_CITY) }
                    )
                }
                state.selectedCountry != null -> {
                    AddOption(
                        text = "Add State",
                        icon = Icons.Default.Map,
                        onClick = { onOptionSelected(DialogType.ADD_STATE) }
                    )
                }
                else -> {
                    AddOption(
                        text = "Add Country",
                        icon = Icons.Default.Public,
                        onClick = { onOptionSelected(DialogType.ADD_COUNTRY) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddOption(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text)
        }
    }
} 
