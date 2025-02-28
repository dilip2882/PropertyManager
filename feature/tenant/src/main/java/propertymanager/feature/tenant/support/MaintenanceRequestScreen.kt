package propertymanager.feature.tenant.support

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Category
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.model.MediaType
import com.propertymanager.domain.model.RequestStatus
import kotlinx.coroutines.launch
import propertymanager.presentation.components.property.PropertyViewModel
import propertymanager.presentation.components.user.UserViewModel
import java.util.UUID

@Composable
fun MaintenanceRequestScreen(
    selectedCategory: String,
    selectedSubcategory: String,
    onNavigateUp: () -> Unit,
    onSubmitSuccess: () -> Unit,
    propertyViewModel: PropertyViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val viewModel = hiltViewModel<MaintenanceRequestViewModel>()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Get current property and user states
    val propertyState by propertyViewModel.state.collectAsState()
    val userState by userViewModel.state.collectAsState()

    val selectedProperty = remember(propertyState.properties, userState.user?.selectedPropertyId) {
        propertyState.properties.find { it.id == userState.user?.selectedPropertyId }
    }

    // States
    val categoriesResponse by viewModel.categoriesResponse.collectAsState()
    val createRequestState by viewModel.createRequestState.collectAsState()
    val mediaUploadState by viewModel.mediaUploadState.collectAsState()

    var currentCategory by remember { mutableStateOf(selectedCategory) }
    var currentSubcategory by remember { mutableStateOf(selectedSubcategory) }
    val maxDescriptionLength = 300
    var issueDescription by remember { mutableStateOf("") }
    var isUrgent by remember { mutableStateOf(false) }
    val photoUriList = remember { mutableStateListOf<Uri>() }
    var subcategories by remember { mutableStateOf(emptyList<String>()) }

    val uploadedPhotoUrls = remember { mutableStateListOf<String>() }

    var showErrors by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Validation states
    val categoryError = showErrors && currentCategory.isBlank()
    val descriptionError = showErrors && issueDescription.isBlank()
    val isUploading = mediaUploadState.values.any { it is Response.Loading }

    val remainingChars = maxDescriptionLength - issueDescription.length

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) { uris: List<Uri> ->
        // Check if adding these new images would exceed the limit
        val remainingSlots = 5 - photoUriList.size
        if (remainingSlots > 0) {
            val newUris = uris.take(remainingSlots)
            photoUriList.addAll(newUris)
        } else {
            Toast.makeText(context, "Maximum 5 images allowed", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(mediaUploadState) {
        mediaUploadState.forEach { (uri, response) ->
            when (response) {
                is Response.Success -> {
                    if (!uploadedPhotoUrls.contains(response.data)) {
                        uploadedPhotoUrls.add(response.data)

                        if (uploadedPhotoUrls.size == photoUriList.size && isSubmitting) {
                            val request = MaintenanceRequest(
                                issueDescription = issueDescription,
                                isUrgent = isUrgent,
                                issueCategory = currentCategory,
                                issueSubcategory = currentSubcategory,
                                photos = uploadedPhotoUrls.toList(),
                                status = RequestStatus.PENDING.label,
                                propertyId = selectedProperty?.id ?: "",
                                tenantId = userState.user?.userId ?: "",
                            )
                            viewModel.createMaintenanceRequestSafely(request)
                        }
                    }
                }

                is Response.Error -> {
                    Toast.makeText(context, "Failed to upload image: ${response.message}", Toast.LENGTH_SHORT).show()
                    isSubmitting = false
                }

                else -> {}
            }
        }
    }

    LaunchedEffect(createRequestState) {
        when (createRequestState) {
            is Response.Success -> {
                Toast.makeText(context, "Request created successfully", Toast.LENGTH_SHORT).show()
                isSubmitting = false
                onSubmitSuccess()
            }

            is Response.Error -> {
                Toast.makeText(context, (createRequestState as Response.Error).message, Toast.LENGTH_LONG).show()
                isSubmitting = false
                // Cleanup uploaded photos if request creation failed
                scope.launch {
                    uploadedPhotoUrls.forEach { url ->
                        viewModel.deleteUploadedFile(url)
                    }
                    uploadedPhotoUrls.clear()
                }
            }

            else -> {}
        }
    }

    LaunchedEffect(currentCategory, categoriesResponse) {
        when (categoriesResponse) {
            is Response.Success -> {
                val categories = (categoriesResponse as Response.Success<List<Category>>).data
                val selectedCategoryData = categories.find { it.name == currentCategory }
                subcategories = selectedCategoryData?.subcategories ?: emptyList()

                // If no subcategory is selected but we have subcategories available, then selecting the first one
                if (currentSubcategory.isEmpty() && subcategories.isNotEmpty()) {
                    currentSubcategory = subcategories.first()
                }
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Raise Complaint") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Category
                DropdownMenuField(
                    label = "Category",
                    selectedOption = currentCategory,
                    options = when (val response = categoriesResponse) {
                        is Response.Success -> response.data.map { it.name }
                        else -> emptyList()
                    },
                    onOptionSelected = { category ->
                        currentCategory = category
                        currentSubcategory = ""
                    },
                    isError = categoryError,
                    errorMessage = if (categoryError) "Please select a category" else null,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subcategory
                if (subcategories.isNotEmpty()) {
                    DropdownMenuField(
                        label = "Subcategory",
                        selectedOption = currentSubcategory,
                        options = subcategories,
                        onOptionSelected = { currentSubcategory = it },
                    )
                }

                // Description with character counter
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = issueDescription,
                        onValueChange = {
                            if (it.length <= maxDescriptionLength) {
                                issueDescription = it
                            }
                        },
                        label = { Text("Describe your issue") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(150.dp),
                        maxLines = 6,
                        singleLine = false,
                        isError = descriptionError,
                        supportingText = if (descriptionError) {
                            { Text("Please provide a description") }
                        } else null,
                    )

                    Text(
                        text = "$remainingChars",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (remainingChars <= 50) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 24.dp, bottom = 8.dp),
                    )
                }

                // Is Urgent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Is it Urgent?")
                    Switch(
                        checked = isUrgent,
                        onCheckedChange = { isUrgent = it },
                    )
                }

                // Photo Upload Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Attach Photos (${photoUriList.size}/5)",
                            style = MaterialTheme.typography.labelLarge,
                        )

                        if (photoUriList.size < 5) {
                            IconButton(
                                onClick = { launcher.launch("image/*") },
                                enabled = photoUriList.size < 5,
                            ) {
                                Icon(
                                    Icons.Default.AddAPhoto,
                                    contentDescription = "Add photos",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        photoUriList.forEachIndexed { index, uri ->
                            Box {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop,
                                )

                                // Delete button
                                IconButton(
                                    onClick = {
                                        photoUriList.removeAt(index)
                                        // If the photo was already uploaded, remove it from uploaded URLs
                                        mediaUploadState[uri]?.let {
                                            if (it is Response.Success) {
                                                uploadedPhotoUrls.remove(it.data)
                                                viewModel.deleteUploadedFile(it.data)
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                            shape = CircleShape,
                                        ),
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove photo",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }

                                when (val uploadState = mediaUploadState[uri]) {
                                    is Response.Loading -> {
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .background(
                                                    Color.Black.copy(alpha = 0.5f),
                                                    RoundedCornerShape(8.dp),
                                                ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = Color.White,
                                            )
                                        }
                                    }

                                    is Response.Error -> {
                                        Icon(
                                            Icons.Default.Error,
                                            contentDescription = "Upload failed",
                                            tint = Color.Red,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp),
                                        )
                                    }

                                    else -> {}
                                }
                            }
                        }
                    }
                }

                // Submit
                Button(
                    onClick = {
                        showErrors = true

                        if (currentCategory.isBlank() || issueDescription.isBlank()) {
                            return@Button
                        }

                        // Validate property and user
                        if (selectedProperty == null) {
                            Toast.makeText(context, "Please select a property first", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (userState.user?.userId == null) {
                            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isSubmitting = true

                        if (photoUriList.isEmpty()) {
                            val request = MaintenanceRequest(
                                issueDescription = issueDescription,
                                isUrgent = isUrgent,
                                issueCategory = currentCategory,
                                issueSubcategory = currentSubcategory,
                                photos = emptyList(),
                                status = RequestStatus.PENDING.label,
                                propertyId = selectedProperty.id,
                                tenantId = userState.user?.userId ?: "",
                            )
                            viewModel.createMaintenanceRequestSafely(request)
                        } else {
                            photoUriList.forEach { uri ->
                                viewModel.uploadMedia(uri, MediaType.IMAGE, UUID.randomUUID().toString())
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !isSubmitting && !isUploading,
                ) {
                    if (isSubmitting || isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text("Raise Complaint")
                    }
                }
            }

            if (isSubmitting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center,
                ) {
//                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun DropdownMenuField(
    label: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable)
                    .fillMaxWidth(),
                isError = isError,
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                    )
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
    }
}
