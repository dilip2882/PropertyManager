package propertymanager.feature.staff.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LocationFilters(
    selectedCity: String?,
    onCitySelected: (String?) -> Unit,
    selectedSociety: String?,
    onSocietySelected: (String?) -> Unit,
    selectedBlock: String?,
    onBlockSelected: (String?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Location",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // City Filter
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { }
        ) {
            OutlinedTextField(
                value = selectedCity ?: "Select City",
                onValueChange = { },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                modifier = Modifier.menuAnchor()
            )
        }

        Spacer(Modifier.height(8.dp))

        // Society Filter
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { }
        ) {
            OutlinedTextField(
                value = selectedSociety ?: "Select Society",
                onValueChange = { },
                enabled = selectedCity != null,
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                modifier = Modifier.menuAnchor()
            )
        }

        Spacer(Modifier.height(8.dp))

        // Block Filter
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { }
        ) {
            OutlinedTextField(
                value = selectedBlock ?: "Select Block",
                onValueChange = { },
                enabled = selectedSociety != null,
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                modifier = Modifier.menuAnchor()
            )
        }
    }
} 
