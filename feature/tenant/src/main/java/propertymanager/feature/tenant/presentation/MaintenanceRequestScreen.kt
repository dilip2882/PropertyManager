package propertymanager.feature.tenant.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.model.MediaType
import com.propertymanager.domain.model.PriorityLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MaintenanceRequestScreen(
    navController: NavController,
    requestId: String? = null,
) {
    val viewModel = hiltViewModel<MaintenanceRequestViewModel>()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var issueDescription by remember { mutableStateOf("") }
    var issueCategory by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(PriorityLevel.LOW.label) }
    var photos by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var videos by remember { mutableStateOf<List<Uri>>(emptyList()) }


    val photoUrls = remember { mutableStateOf<List<String>>(emptyList()) }
    val videoUrls = remember { mutableStateOf<List<String>>(emptyList()) }

    val createRequestState by viewModel.createRequestState.collectAsState()
    val currentRequest by viewModel.currentRequest.collectAsState()
    val mediaUploadState by viewModel.mediaUploadState.collectAsState()

    // Load existing request details when editing
    LaunchedEffect(requestId) {
        requestId?.let { viewModel.fetchMaintenanceRequestById(it) }
    }

    val categories = listOf("Plumbing", "Electrical", "Cleaning", "General", "Other")
    val priorities = PriorityLevel.getAllPriorities()

    LaunchedEffect(currentRequest) {
        when (val request = currentRequest) {
            is Response.Success -> {
                issueDescription = request.data.issueDescription
                issueCategory = request.data.issueCategory
                priority = request.data.priority
            }

            else -> {}
        }
    }

    LaunchedEffect(createRequestState) {
        when (val state = createRequestState) {
            is Response.Success -> {
                navController.popBackStack()
            }

            is Response.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }


    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            photos = uris
        },
    )

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { videos = listOf(it) }
        },
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Maintenance Post") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // Issue Description
            TextField(
                value = issueDescription,
                onValueChange = { issueDescription = it },
                placeholder = { Text("Describe your maintenance issue...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Media Upload Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    onClick = {
                        multiplePhotoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Photos")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Photos")
                }

                Button(
                    onClick = {
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly),
                        )
                    },
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = "Video")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Video")
                }
            }

            // Preview Selected Media
            if (photos.isNotEmpty() || videos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Selected Media", style = MaterialTheme.typography.titleMedium)

                LazyRow {
                    items(photos) { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    items(videos) { uri ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Video",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {},
            ) {
                OutlinedTextField(
                    value = issueCategory,
                    onValueChange = {},
                    label = { Text("Category") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        // Upload photos
                        photos.forEach { uri ->
                            viewModel.uploadMedia(uri, MediaType.IMAGE)
                        }

                        videos.forEach { uri ->
                            viewModel.uploadMedia(uri, MediaType.VIDEO)
                        }

                        delay(500)

                        val photoUrls = photos.mapNotNull { uri ->
                            (mediaUploadState[uri] as? Response.Success)?.data
                        }

                        val videoUrls = videos.mapNotNull { uri ->
                            (mediaUploadState[uri] as? Response.Success)?.data
                        }

                        val request = MaintenanceRequest(
                            maintenanceRequestsId = requestId,
                            issueDescription = issueDescription,
                            issueCategory = issueCategory,
                            priority = priority,
                            photos = photoUrls,
                            videos = videoUrls
                        )

                        if (requestId == null) {
                            viewModel.createMaintenanceRequest(request)
                        } else {
                            viewModel.updateMaintenanceRequest(request)
                        }
                    }
                }
            ) {
                Text(if (requestId == null) "Create Request" else "Update Request")
            }
        }
    }
}
