package propertymanager.presentation.user

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import com.propertymanager.common.utils.Response
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditProfileScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBannerUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBannerColor by remember { mutableStateOf(Color(0xFF5CD6FF)) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showImagePickerSheet by remember { mutableStateOf(false) }
    var showBannerPickerSheet by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var bannerPhotoUri by remember { mutableStateOf<Uri?>(null) }

    var isEmailValid by remember { mutableStateOf(true) }

    val context = LocalContext.current
    var location by remember { mutableStateOf(GeoPoint(0.0, 0.0)) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val contentResolver = context.contentResolver
    val bottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    // Create a temporary file for storing camera photos
    fun createImageFile(isProfile: Boolean): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = if (isProfile) "PROFILE_${timeStamp}_" else "BANNER_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }

    // Gallery launchers
    val profileGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { selectedImageUri = it } }

    val bannerGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { selectedBannerUri = it } }

    // Camera launchers
    val profileCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let { uri -> selectedImageUri = uri }
        }
    }

    val bannerCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            bannerPhotoUri?.let { uri -> selectedBannerUri = uri }
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.getUserInfo()
    }

    val userDataState by viewModel.getUserData.collectAsState()

    // Handle profile image upload
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            try {
                val userId = viewModel.auth.currentUser?.uid ?: return@let
                val imageUrl = viewModel.uploadImageToFirebase(uri, userId)

                // Update user profile with new image URL
                (userDataState as? Response.Success)?.data?.let { currentUser ->
                    viewModel.setUserInfo(currentUser.copy(profileImage = imageUrl))
                }
            } catch (e: Exception) {
                Log.e("EditProfile", "Error uploading profile image: ${e.message}")
            }
        }
    }

    // Handle banner image upload
    LaunchedEffect(selectedBannerUri) {
        selectedBannerUri?.let { uri ->
            try {
                val userId = viewModel.auth.currentUser?.uid ?: return@let
                val imageUrl = viewModel.uploadImageToFirebase(uri, userId)

                // Update user profile with new banner URL
                (userDataState as? Response.Success)?.data?.let { currentUser ->
                    viewModel.setUserInfo(currentUser.copy(bannerImage = imageUrl))
                }
            } catch (e: Exception) {
                Log.e("EditProfile", "Error uploading banner image: ${e.message}")
            }
        }
    }

    LaunchedEffect(userDataState) {
        when (val response = userDataState) {
            is Response.Success -> {
                response.data?.let { user ->
                    name = user.name
                    bio = user.bio
                    email = user.email
                    phone = user.phone
                    address = user.address
                }
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("Edit Profile") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        val currentUser = (userDataState as? Response.Success)?.data
                        currentUser?.let { user ->
                            val updatedUser = user.copy(
                                name = name,
                                bio = bio,
                                email = email,
                                phone = phone,
                                address = address,
                            )
                            viewModel.setUserInfo(updatedUser)
                        }
                        onNavigateBack()
                    }
                ) {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = "Save",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )

        // Profile Header with Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(selectedBannerColor, selectedBannerColor.copy(alpha = 0.7f))
                        )
                    )
                    .clickable { showBannerPickerSheet = true }
            ) {
                when {
                    selectedBannerUri != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(selectedBannerUri),
                            contentDescription = "Selected Banner Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    (userDataState as? Response.Success)?.data?.bannerImage != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(
                                (userDataState as Response.Success).data?.bannerImage
                            ),
                            contentDescription = "Banner Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF5CD6FF), Color(0xFF5CD6FF).copy(alpha = 0.7f))
                                    )
                                )
                        )
                    }
                }
                Icon(
                    Icons.Default.Camera,
                    contentDescription = "Change Banner",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clickable { showColorPicker = true },
                    tint = Color.White
                )
            }

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = 30.dp, y = (75).dp)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                    )
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable { showImagePickerSheet = true }
            ) {
                when {
                    selectedImageUri != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Selected Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    (userDataState as? Response.Success)?.data?.profileImage != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(
                                (userDataState as Response.Success).data?.profileImage
                            ),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            tint = Color.White,
                        )
                    }
                }
            }
        }

        // Edit Fields
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = !isEmailValid,
            )

            if (!isEmailValid) {
                Text(
                    text = "Invalid email address",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(vertical = 8.dp),
                )
            }

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                ) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                ActivityCompat.requestPermissions(
                                    context as Activity,
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                    ),
                                    100,
                                )
                            } else {
                                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                    loc?.let {
                                        location = GeoPoint(it.latitude, it.longitude)
                                        val addresses =
                                            geocoder.getFromLocation(it.latitude, it.longitude, 1)
                                        if (!addresses.isNullOrEmpty()) {
                                            address = addresses[0].getAddressLine(0) ?: ""
                                        }
                                    }
                                }
                            }
                        },
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Get Location")
                    }
                },
            )
        }
    }

    // Profile Image Picker Bottom Sheet
    if (showImagePickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showImagePickerSheet = false },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Choose Profile Picture",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ListItem(
                    headlineContent = { Text("Take Photo") },
                    leadingContent = {
                        Icon(Icons.Default.Camera, contentDescription = null)
                    },
                    modifier = Modifier.clickable {
                        cameraPermissionState.launchPermissionRequest()
                        if (cameraPermissionState.status.isGranted) {
                            try {
                                photoUri = createImageFile(true)
                                profileCameraLauncher.launch(photoUri!!)
                            } catch (e: Exception) {
                                Log.e("EditProfile", "Error creating image file: ${e.message}")
                            }
                        }
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) showImagePickerSheet = false
                        }
                    }
                )

                ListItem(
                    headlineContent = { Text("Choose from Gallery") },
                    leadingContent = {
                        Icon(Icons.Default.Image, contentDescription = null)
                    },
                    modifier = Modifier.clickable {
                        profileGalleryLauncher.launch("image/*")
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) showImagePickerSheet = false
                        }
                    }
                )
            }
        }
    }

    // Banner Image Picker Bottom Sheet
    if (showBannerPickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBannerPickerSheet = false },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Choose Banner Image",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ListItem(
                    headlineContent = { Text("Take Photo") },
                    leadingContent = {
                        Icon(Icons.Default.Camera, contentDescription = null)
                    },
                    modifier = Modifier.clickable {
                        cameraPermissionState.launchPermissionRequest()
                        if (cameraPermissionState.status.isGranted) {
                            try {
                                bannerPhotoUri = createImageFile(false)
                                bannerCameraLauncher.launch(bannerPhotoUri!!)
                            } catch (e: Exception) {
                                Log.e("EditProfile", "Error creating banner image file: ${e.message}")
                            }
                        }
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) showBannerPickerSheet = false
                        }
                    }
                )

                ListItem(
                    headlineContent = { Text("Choose from Gallery") },
                    leadingContent = {
                        Icon(Icons.Default.Image, contentDescription = null)
                    },
                    modifier = Modifier.clickable {
                        bannerGalleryLauncher.launch("image/*")
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) showBannerPickerSheet = false
                        }
                    }
                )
            }
        }
    }

    // Color Picker Dialog
    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text("Choose Banner Color") },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        Color(0xFF5CD6FF),
                        Color.Red,
                        Color.Green,
                        Color.Blue,
                        Color.Yellow
                    ).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(color, CircleShape)
                                .clickable {
                                    selectedBannerColor = color
                                    showColorPicker = false
                                }
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showColorPicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
