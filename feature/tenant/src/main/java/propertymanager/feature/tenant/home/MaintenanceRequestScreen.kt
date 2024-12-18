@file:Suppress("NAME_SHADOWING")

package propertymanager.feature.tenant.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.propertymanager.domain.model.MaintenanceRequest

@Composable
fun MaintenanceRequestScreen(
    selectedCategory: String,
    selectedSubcategory: String,
    onNavigateUp: () -> Unit,
    onSubmit: () -> Unit,
    viewModel: MaintenanceRequestViewModel = hiltViewModel(),
) {
    val createRequestState by viewModel.createRequestState.collectAsState()

    var selectedCategory by remember { mutableStateOf(selectedCategory) }
    var selectedSubcategory by remember { mutableStateOf(selectedSubcategory) }
    var issueDescription by remember { mutableStateOf("") }
    var isUrgent by remember { mutableStateOf(false) }
    val photoUriList = remember { mutableStateListOf<Uri>() }

    var availableSubcategories: List<String> = emptyList()
    var availableCategories: List<String> = emptyList()

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let { photoUriList.add(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Raise Complaint", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Category Dropdown
            var categoryExpanded by remember { mutableStateOf(false) }
            Text(
                text = "Category",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(start = 16.dp),
            )
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it },
            ) {
                TextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Select Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                ) {
                    availableCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                selectedSubcategory = "" // Reset subcategory
                                categoryExpanded = false
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subcategory Dropdown
            var subcategoryExpanded by remember { mutableStateOf(false) }
            Text(
                text = "Subcategory",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp),
            )
            ExposedDropdownMenuBox(
                expanded = subcategoryExpanded,
                onExpandedChange = { subcategoryExpanded = it },
            ) {
                TextField(
                    value = selectedSubcategory,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Select Subcategory") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subcategoryExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
                ExposedDropdownMenu(
                    expanded = subcategoryExpanded,
                    onDismissRequest = { subcategoryExpanded = false },
                ) {
                    availableSubcategories.forEach { subcategory ->
                        DropdownMenuItem(
                            text = { Text(subcategory) },
                            onClick = {
                                selectedSubcategory = subcategory
                                subcategoryExpanded = false
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Is Urgent
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Is it Urgent?", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = isUrgent,
                    onCheckedChange = { isUrgent = it },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = "Request Description",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp),
            )
            OutlinedTextField(
                value = issueDescription,
                onValueChange = { issueDescription = it },
                label = { Text("Describe your issue") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(150.dp),
                maxLines = 6,
                singleLine = false,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Attach Photo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start,
            ) {
                photoUriList.forEach { uri ->
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                        )
                    }
                }

                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Submit
            Button(
                onClick = {
                    val request = MaintenanceRequest(
                        issueDescription = issueDescription,
                        isUrgent = isUrgent,
                        issueCategory = selectedCategory,
                        issueSubcategory = selectedSubcategory,
                        photos = photoUriList.map { it.toString() },
                    )
                    viewModel.createMaintenanceRequest(request)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
            ) {
                Text("Submit Complaint", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

