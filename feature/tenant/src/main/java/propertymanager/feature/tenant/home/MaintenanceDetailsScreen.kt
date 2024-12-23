package propertymanager.feature.tenant.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.model.RequestStatus
import com.propertymanager.domain.model.WorkerDetails
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MaintenanceDetailsScreen(
    requestId: String,
    onNavigateUp: () -> Unit,
) {
    val viewModel = hiltViewModel<MaintenanceRequestViewModel>()
    val currentRequest by viewModel.currentRequest.collectAsState()
    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(requestId) {
        viewModel.fetchMaintenanceRequestById(requestId)
    }

    if (showImageDialog && currentRequest is Response.Success) {
        val photos = (currentRequest as Response.Success<MaintenanceRequest>).data.photos
        if (photos.isNotEmpty()) {
            Dialog(
                onDismissRequest = { showImageDialog = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    usePlatformDefaultWidth = false,
                ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                ) {
                    // Close button
                    IconButton(
                        onClick = { showImageDialog = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                        )
                    }

                    val pagerState = rememberPagerState(
                        initialPage = selectedImageIndex,
                        pageCount = { photos.size },
                    )

                    // Full screen image
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                    ) { page ->
                        var isLoading by remember { mutableStateOf(true) }
                        var error by remember { mutableStateOf<String?>(null) }

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(photos[page])
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                contentScale = ContentScale.Fit,
                                onLoading = { isLoading = true },
                                onSuccess = { isLoading = false },
                                onError = {
                                    isLoading = false
                                    error = it.result.throwable.localizedMessage
                                },
                            )

                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(48.dp),
                                )
                            }

                            error?.let { errorMessage ->
                                Text(
                                    text = errorMessage,
                                    color = Color.White,
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                        }
                    }

                    // Page indicator
                    if (photos.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            repeat(photos.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (pagerState.currentPage == index)
                                                Color.White
                                            else
                                                Color.White.copy(alpha = 0.5f),
                                        ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { padding ->
        when (val state = currentRequest) {
            is Response.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is Response.Success -> {
                val request = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState()),
                ) {
                    StatusCard(request)
                    MainContent(
                        request = request,
                        onImageClick = { index ->
                            selectedImageIndex = index
                            showImageDialog = true
                        },
                    )
                }
            }

            is Response.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Error loading request: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusCard(request: MaintenanceRequest) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (request.status) {
                RequestStatus.PENDING.label -> Color(0xFFFFF3E0)
                RequestStatus.IN_PROGRESS.label -> Color(0xFFE3F2FD)
                RequestStatus.COMPLETED.label -> Color(0xFFE8F5E9)
                else -> MaterialTheme.colorScheme.surface
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    "Status",
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    request.status,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (request.isUrgent) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                ) {
                    Text(
                        "URGENT",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    request: MaintenanceRequest,
    onImageClick: (Int) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            DetailSection(
                title = "Category",
                content = request.issueCategory +
                    if (request.issueSubcategory.isNotEmpty())
                        " > ${request.issueSubcategory}"
                    else "",
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            DetailSection(
                title = "Description",
                content = request.issueDescription,
            )

            if (request.photos.isNotEmpty()) {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                Text(
                    "Photos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(request.photos.size) { index ->
                        var isLoading by remember { mutableStateOf(true) }
                        var error by remember { mutableStateOf<String?>(null) }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onImageClick(index) },
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(request.photos[index])
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                onLoading = { isLoading = true },
                                onSuccess = { isLoading = false },
                                onError = {
                                    isLoading = false
                                    error = it.result.throwable.localizedMessage
                                },
                            )

                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                )
                            }

                            error?.let { errorMessage ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Red.copy(alpha = 0.6f))
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "Error loading image",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (request.workerDetails.name.isNotEmpty()) {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                Text(
                    "Assigned Worker",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))

                WorkerDetailsCard(request.workerDetails)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )
            TimestampSection(
                created = request.createdAt.toDate(),
                updated = request.updatedAt.toDate(),
            )
        }
    }
}

@Composable
private fun WorkerDetailsCard(workerDetails: WorkerDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    workerDetails.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    workerDetails.trade,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            IconButton(
                onClick = {
                    // Handle phone call
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Icon(Icons.Default.Phone, "Call worker")
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: String,
) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            content,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun TimestampSection(
    created: Date,
    updated: Date,
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                "Created",
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                dateFormat.format(created),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Column {
            Text(
                "Last Updated",
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                dateFormat.format(updated),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
