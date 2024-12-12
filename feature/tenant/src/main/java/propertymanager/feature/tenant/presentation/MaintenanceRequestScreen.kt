package propertymanager.feature.tenant.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.propertymanager.common.utils.Response
import propertymanager.feature.tenant.domian.model.MaintenanceRequest
import propertymanager.feature.tenant.domian.model.PriorityLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceRequestScreen(
    navController: NavController,
    requestId: String? = null,
) {
    val maintenanceRequestViewModel = hiltViewModel<MaintenanceRequestViewModel>()

    var issueDescription by remember { mutableStateOf("") }
    var issueCategory by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(PriorityLevel.LOW.label) }
    var photos by remember { mutableStateOf<List<String>>(emptyList()) }
    var videos by remember { mutableStateOf<List<String>>(emptyList()) }

    val categories = listOf("Plumbing", "Electrical", "Cleaning", "General", "Other")
    val priorities = PriorityLevel.getAllPriorities()
    var categoryExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    val existingRequest = maintenanceRequestViewModel.maintenanceRequest.value
    val createRequestResponse = maintenanceRequestViewModel.createRequestResponse.value

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            maintenanceRequestViewModel.uploadMedia(uri, MediaType.IMAGE) { mediaUrl -> photos = photos + mediaUrl }
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            maintenanceRequestViewModel.uploadMedia(uri, MediaType.VIDEO) { mediaUrl -> videos = videos + mediaUrl }
        }
    }

    // If editing an existing request, load its data
    if (requestId != null) {
        LaunchedEffect(requestId) {
            maintenanceRequestViewModel.getMaintenanceRequestById(requestId)
        }
    }

    if (existingRequest is Response.Success && requestId != null) {
        val request = existingRequest.data
        issueDescription = request.issueDescription
        issueCategory = request.issueCategory
        priority = request.priority
        photos = request.photos
        videos = request.videos
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TopAppBar(
            title = { Text("Submit Maintenance Request", fontWeight = FontWeight.Bold, fontSize = 24.sp) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )

        TextField(
            value = issueDescription,
            onValueChange = { issueDescription = it },
            label = { Text("Describe the issue") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            maxLines = 4,
        )

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded },
        ) {
            OutlinedTextField(
                value = issueCategory,
                onValueChange = { issueCategory = it },
                label = { Text("Issue Category") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                readOnly = true,
                trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Category") },
            )
            ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                categories.forEach { category ->
                    DropdownMenuItem(text = { Text(category) }, onClick = {
                        issueCategory = category
                        categoryExpanded = false
                    })
                }
            }
        }

        FileUploadButton(onUploadClick = { imagePickerLauncher.launch("image/*") })

        Button(
            onClick = {
                val request = MaintenanceRequest(
                    id = requestId,
                    propertyId = "", tenantId = "", issueDescription = issueDescription,
                    issueCategory = issueCategory, priority = priority, photos = photos, videos = videos
                )
                if (requestId != null) {
                    maintenanceRequestViewModel.updateMaintenanceRequest(request)
                } else {
                    maintenanceRequestViewModel.createMaintenanceRequest(request)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            Text(if (requestId != null) "Update Request" else "Submit Request")
        }

        when (createRequestResponse) {
            is Response.Loading -> {
/*
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp))
*/
            }
            is Response.Success -> {
                Text("Request created successfully", color = Color.Green, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp))
                navController.popBackStack()
            }
            is Response.Error -> Text("Error: ${createRequestResponse.message}", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp))
        }
    }
}


@Composable
fun FileUploadButton(onUploadClick: () -> Unit) {
    Button(
        onClick = onUploadClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
    ) {
        Text("Upload Photos/Videos")
    }
}
