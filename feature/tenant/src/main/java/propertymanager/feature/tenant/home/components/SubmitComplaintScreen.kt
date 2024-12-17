package propertymanager.feature.tenant.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.MaintenanceRequest
import propertymanager.feature.tenant.home.MaintenanceRequestViewModel

@Composable
fun SubmitComplaintScreen(
    selectedCategory: String,
    onSubmit: (MaintenanceRequest) -> Unit
) {
    val viewModel: MaintenanceRequestViewModel = hiltViewModel()
    val priorities by viewModel.priorityLevels.collectAsState(initial = listOf())
    val statuses by viewModel.requestStatuses.collectAsState(initial = listOf())

    var issueDescription by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(priorities.firstOrNull() ?: "") }
    var status by remember { mutableStateOf(statuses.firstOrNull() ?: "") }
    var isUrgent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submit Complaint") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = issueDescription,
                onValueChange = { issueDescription = it },
                label = { Text("Issue Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spinner(
                selectedValue = priority,
                options = priorities,
                onValueChange = { priority = it },
                label = "Priority"
            )
            Spinner(
                selectedValue = status,
                options = statuses,
                onValueChange = { status = it },
                label = "Status"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isUrgent,
                    onCheckedChange = { isUrgent = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Is it Urgent?")
            }
            Button(
                onClick = {
                    val request = MaintenanceRequest(
                        issueDescription = issueDescription,
                        priority = priority,
                        status = status,
                        isUrgent = isUrgent,
                        issueCategory = selectedCategory
                    )
                    onSubmit(request)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Complaint")
            }
        }
    }
}

@Composable
fun Spinner(
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    val icon: ImageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("$label: $selectedValue")
            Icon(imageVector = icon, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    text = {
                        Text(option)
                    }
                )
            }
        }
    }
}
