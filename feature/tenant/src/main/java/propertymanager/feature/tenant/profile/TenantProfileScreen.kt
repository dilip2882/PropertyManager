package propertymanager.feature.tenant.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Property
import com.propertymanager.domain.model.User
import propertymanager.i18n.MR
import propertymanager.presentation.components.TextPreferenceWidget
import propertymanager.presentation.components.property.PropertyViewModel
import propertymanager.presentation.components.user.ProfileScreen
import propertymanager.presentation.components.user.UserViewModel
import propertymanager.presentation.i18n.stringResource
import propertymanager.presentation.screens.LoadingScreen
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TenantProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPropertyManager: () -> Unit,
    propertyViewModel: PropertyViewModel = hiltViewModel(),
) {
    val state by propertyViewModel.state.collectAsState()
    var selectedProperty by remember { mutableStateOf<Property?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black,
                ),
                title = { Text("Profile") },
            )
        },
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            item {
                ProfileScreen(
                    onNavigateToEditProfile = {
                        onNavigateToEditProfile()
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextPreferenceWidget(
                    title = stringResource(MR.strings.staff_property),
                    icon = Icons.Default.LocationCity,
                    onPreferenceClick = {
                        onNavigateToPropertyManager()
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                ProfileBody(user = User())
            }

        }
    }
}

@Composable
fun ProfileBody(user: User) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val userViewModel = hiltViewModel<UserViewModel>()

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    LaunchedEffect(key1 = true) {
        userViewModel.getUserInfo()
    }

    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let {
            val imageUrl = try {
                // Upload image and get the URL
                userViewModel.uploadImageToFirebase(it, user.userId!!)
            } catch (e: Exception) {
                Log.e("Profile", "Error uploading image: ${e.message}")
                null
            }
            if (imageUrl != null) {
                userViewModel.setUserInfo(user.copy(profileImage = imageUrl))
            }
        }
    }

    val userDataState by userViewModel.getUserData.collectAsState()

    when (val response = userDataState) {
        is Response.Loading -> {
            LoadingScreen()
        }

        is Response.Error -> {
            Text(text = "Error: ${response.message}")
        }

        is Response.Success -> {
            val fetchedUser = response.data ?: user
            Log.d("Profile", "Fetched User: $fetchedUser")

            // Member Since Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        RoundedCornerShape(8.dp),
                    )
                    .padding(16.dp),
            ) {
                Text(
                    "Member Since",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = user.createdAt?.let { timestamp ->
                            val timestampInMillis = timestamp.seconds

                            val instant = Instant.ofEpochMilli(timestampInMillis)

                            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

                            val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")
                            localDateTime.format(formatter)
                        } ?: "Invalid Date",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                }
            }
        }
    }

}


