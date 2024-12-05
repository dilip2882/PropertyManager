package com.propertymanager.presentation.ui.userprofile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import com.propertymanager.domain.model.User
import com.propertymanager.utils.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun OnboardingFormScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val userViewModel = hiltViewModel<UserViewModel>()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }

    val userResponse = userViewModel.getUserData.value

    var username by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(GeoPoint(0.0, 0.0)) }
    var loading by remember { mutableStateOf(false) }

    // State for image picker
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }

    val imagePickerResult =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
            // Perform conversion asynchronously after selecting an image
            if (uri != null) {
                convertUriToBase64(uri, context) { base64 ->
                    base64Image = base64
                }
            }
        }

    LaunchedEffect(Unit) {
        userViewModel.getUserInfo()
    }

    when (userResponse) {
        is Response.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Response.Success -> {
            val user = userResponse.data
            if (user != null) {
                if (username.isEmpty()) username = user.username.orEmpty()
                if (imageUrl.isEmpty()) imageUrl = user.imageUrl.orEmpty()
                if (bio.isEmpty()) bio = user.bio.orEmpty()
                if (email.isEmpty()) email = user.email.orEmpty()
                if (address.isEmpty()) address = user.address.orEmpty()
            }
        }

        is Response.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error loading user data.", color = Color.Red)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Complete Your Profile",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 5.dp, bottom = 16.dp)
        )

        // Username, Image URL, Bio, Email fields
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Image URL") },
            modifier = Modifier.fillMaxWidth()
        )

        // Image Picker Button
        Button(
            onClick = {
                imagePickerResult.launch("image/*") // Launch the image picker
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Image")
        }

        // Display the selected image if any
        selectedImageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Selected Image",
                modifier = Modifier.size(100.dp)
            )
        }

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        // Address and Location field with GPS icon
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier
                .fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                context as Activity,
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ),
                                100
                            )
                        } else {
                            // Fetch the location
                            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                loc?.let {
                                    location = GeoPoint(it.latitude, it.longitude)
                                    // Reverse geocode the address
                                    val addresses =
                                        geocoder.getFromLocation(it.latitude, it.longitude, 1)
                                    if (!addresses.isNullOrEmpty()) {
                                        address = addresses[0].getAddressLine(0) ?: ""
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Get Location")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                loading = true
                val updatedUser = User(
                    userId = userViewModel.auth.currentUser?.uid.orEmpty(),
                    name = username,
                    username = username,
                    imageUrl = base64Image ?: imageUrl, // Using the base64 string if available
                    bio = bio,
                    email = email,
                    address = address,
                    location = location,
                    phone = userResponse.run { (this as? Response.Success<User?>)?.data?.phone }
                        ?: "",
                    properties = userResponse.let { (it as? Response.Success<User?>)?.data?.properties }
                        ?: emptyList()
                )
                userViewModel.setUserInfo(updatedUser)

                // Navigate to the Home Screen
                navController.navigate("home_screen") {
                    popUpTo("onboarding_screen") { inclusive = true }
                }
                loading = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text(text = "Save")
            }
        }
    }
}

// convert the Uri to Base64 asynchronously
fun convertUriToBase64(uri: Uri, context: Context, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val byteArray = inputStream?.readBytes()
            val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            withContext(Dispatchers.Main) {
                onResult(base64String)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
