package propertymanager.feature.onboarding

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import com.propertymanager.domain.model.User
import propertymanager.feature.onboarding.mvi.OnboardingContract
import java.util.Locale

@Composable
fun OnboardingFormScreen(
    state: OnboardingContract.OnboardingState,
    effect: OnboardingContract.OnboardingEffect? = null,
    dispatch: (OnboardingContract.OnboardingEvent) -> Unit,
    onComplete: () -> Unit,
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }

    val viewModel = viewModel<OnboardingViewModel>()
    val existingRole by viewModel.existingRole.collectAsState()

    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(GeoPoint(0.0, 0.0)) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var isEmailValid by remember { mutableStateOf(true) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    LaunchedEffect(effect) {
        when (effect) {
            is OnboardingContract.OnboardingEffect.NavigateToHome -> onComplete()
            is OnboardingContract.OnboardingEffect.ShowToast -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Complete Your Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center,
        ) {
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Profile Icon",
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
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
                modifier = Modifier.align(Alignment.Start),
            )
        }

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
                    },
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Get Location")
                }
            },
        )

        Button(
            onClick = {
                if (isEmailValid) {
                    val user = User(
                        username = username,
                        name = name,
                        email = email,
                        address = address,
                        role = existingRole ?: "TENANT"
                    )
                    dispatch(OnboardingContract.OnboardingEvent.SubmitUserDetails(user, selectedImageUri))
                } else {
                    Toast.makeText(context, "Please provide a valid email address", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Complete Profile")
        }

        if (state is OnboardingContract.OnboardingState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        if (state is OnboardingContract.OnboardingState.Error) {
            Text(
                text = state.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingFormScreenPreview() {
    OnboardingFormScreen(
        state = OnboardingContract.OnboardingState.Idle,
        effect = null,
        dispatch = {},
        onComplete = {},
    )
}
