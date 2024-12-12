package propertymanager.feature.tenant.presentation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.ktx.Firebase
import com.propertymanager.common.utils.Response
import propertymanager.feature.tenant.domian.model.MaintenanceRequest
import propertymanager.feature.tenant.domian.model.PriorityLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceRequestScreen(
    navController: NavController,
) {
    val maintenanceRequestViewModel = hiltViewModel<MaintenanceRequestViewModel>()

    var issueDescription by remember { mutableStateOf("") }
    var issueCategory by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(PriorityLevel.LOW.label) }
    var photos by remember { mutableStateOf<List<String>>(emptyList()) }
    var videos by remember { mutableStateOf<List<String>>(emptyList()) }

    val categories = listOf("Plumbing", "Electrical", "Cleaning", "General", "Other")
    val priorities = PriorityLevel.getAllPriorities()

    // Dropdown expanded states
    var categoryExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    val createRequestResponse = maintenanceRequestViewModel.createRequestResponse.value

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        TopAppBar(
            title = { Text("Submit Maintenance Request", fontWeight = FontWeight.Bold, fontSize = 24.sp) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        // Issue Description
        TextField(
            value = issueDescription,
            onValueChange = { issueDescription = it },
            label = { Text("Describe the issue") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            maxLines = 4
        )

        // Issue Category Dropdown
        ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = !categoryExpanded }) {
            OutlinedTextField(
                value = issueCategory,
                onValueChange = { issueCategory = it },
                label = { Text("Issue Category") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                readOnly = true,
                trailingIcon = {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Category")
                }
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            issueCategory = category
                            categoryExpanded = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = null,
                        trailingIcon = null,
                        enabled = true,
                        colors = MenuDefaults.itemColors(),
                        contentPadding = PaddingValues(16.dp),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                }
            }
        }

        // Priority Dropdown
        ExposedDropdownMenuBox(expanded = priorityExpanded, onExpandedChange = { priorityExpanded = !priorityExpanded }) {
            OutlinedTextField(
                value = priority,
                onValueChange = { /* No-op */ },
                label = { Text("Priority Level") },
                readOnly = true,
                trailingIcon = {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Priority Level")
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
            ExposedDropdownMenu(
                expanded = priorityExpanded,
                onDismissRequest = { priorityExpanded = false }
            ) {
                priorities.forEach { priorityOption ->
                    DropdownMenuItem(
                        text = { Text(priorityOption) },
                        onClick = {
                            priority = priorityOption
                            priorityExpanded = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = null,
                        trailingIcon = null,
                        enabled = true,
                        colors = MenuDefaults.itemColors(),
                        contentPadding = PaddingValues(16.dp),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                }
            }
        }

        // Photo/Video Upload Button
        FileUploadButton(onUploadClick = {
            // Handle file picker for photos/videos
        })

        // Submit Request Button
        Button(
            onClick = {
                val request = MaintenanceRequest(
                    propertyId = "",
                    tenantId = "",
                    issueDescription = issueDescription,
                    issueCategory = issueCategory,
                    priority = priority,
                    photos = photos,
                    videos = videos
                )
                maintenanceRequestViewModel.createMaintenanceRequest(request)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Submit Request")
        }

        // Display status for request creation
        when (val response = createRequestResponse) {
            is Response.Loading -> {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp))
            }
            is Response.Success -> {
                Text("Request created successfully", modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp), color = Color.Green)
                navController.popBackStack()
            }
            is Response.Error -> {
                Text("Error: ${response.message}", modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp), color = Color.Red)
            }
        }
    }
}

@Composable
fun FileUploadButton(onUploadClick: () -> Unit) {
    Button(
        onClick = onUploadClick,
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        Text("Upload Photos/Videos")
    }
}

