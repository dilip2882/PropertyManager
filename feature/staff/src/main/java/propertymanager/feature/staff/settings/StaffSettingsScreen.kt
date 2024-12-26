package propertymanager.feature.staff.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.propertymanager.domain.model.biometrics.BiometricAuthState
import com.propertymanager.domain.model.biometrics.BiometricCheckResult
import propertymanager.presentation.components.ImageWrapper
import propertymanager.presentation.components.LocalPreferenceHighlighted
import propertymanager.presentation.components.LocalPreferenceMinHeight
import propertymanager.presentation.components.TextPreferenceWidget

@Composable
fun StaffSettingsScreen(
    onNavigateToCategoryManager: () -> Unit,
    onNavigateToPropertyManager: () -> Unit,
    onNavigateToRoles: () -> Unit,
) {
    val viewModel = hiltViewModel<ThemeViewModel>()
    val biometricAuthViewModel = hiltViewModel<BiometricViewModel>()

    val context = LocalContext.current
    val dynamicColor by viewModel.dynamicColor.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    val biometricAvailability by biometricAuthViewModel.biometricAvailability.collectAsState()
    val biometricAuth by biometricAuthViewModel.biometricAuthState.collectAsState()

    val darkModeChange: (Boolean) -> Unit = remember(viewModel) {
        {
            viewModel.setDarkMode(it)
        }
    }

    val dynamicColorChange: (Boolean) -> Unit = remember(viewModel) {
        {
            viewModel.setDynamicColor(it)
        }
    }

    val biometricAuthChange: (Boolean) -> Unit = remember(biometricAuthViewModel) {

        {
            biometricAuthViewModel.setBiometricAuth(it)
        }

    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black,
                ),
                title = { Text("Settings") },
                actions = {
                    IconButton(
                        onClick = {

                        },
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Edit Profile",
                            tint = Color.Black,
                        )
                    }
                },
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
                Profile()
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SettingsItem(
                    optionName = "Dark Mode",
                    isSwitch = true,
                    switchValue = darkMode,
                    onSwitchChanged = darkModeChange,
                )
                Spacer(modifier = Modifier.height(16.dp))

            }

            item {
                SettingsItem(
                    optionName = "Dynamic Color",
                    isSwitch = true,
                    switchValue = dynamicColor,
                    onSwitchChanged = dynamicColorChange,
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                if (biometricAvailability is BiometricCheckResult.Available) {
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsItem(
                        optionName = "Biometric Authentication",
                        isSwitch = true,
                        switchValue = biometricAuth == BiometricAuthState.ENABLED,
                        onSwitchChanged = biometricAuthChange,
                    )
                }

            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextPreferenceWidget(
                    title = "Add Property",
                    icon = Icons.Default.LocationOn,
                    onPreferenceClick = {
                        onNavigateToPropertyManager()
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextPreferenceWidget(
                    title = "Add Category",
                    icon = Icons.Filled.Category,
                    onPreferenceClick = {
                        onNavigateToCategoryManager()
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextPreferenceWidget(
                    title = "Roles",
                    icon = Icons.Filled.Person,
                    onPreferenceClick = {
                        onNavigateToRoles()
                    },
                )
            }

        }

    }
}

@Composable
fun Profile() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),

        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(15.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.run {
                            linearGradient(
                                listOf(Color(0xFF5CD6FF), Color(0xFF5CD6FF)),
                            )
                        },
                    ),

                )

            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Camera Icon",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(28.dp),
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, top = 50.dp)
                    .offset(y = 15.dp),

                ) {

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF556E8D)),
                    contentAlignment = Alignment.Center,
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile Icon",
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )

                }

                // Edit Icon
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(Alignment.BottomEnd)
                        .offset(y = 0.dp, x = (-2).dp)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        modifier = Modifier.size(20.dp),
                        contentDescription = "Edit Icon",
                        tint = Color.Black,
                    )
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Dilip",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black,
            )
            Text(
                text = "6-152    Residing Owner",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bio Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Bio",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp),
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = "Add bio",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                    )
                    Text(
                        text = "Tell your neighbours about yourself",
                        fontSize = 12.sp,
                        color = Color.Gray,
                    )
                }
            }
        }

    }
}

@Composable
fun SettingsItem(
    optionName: String,
    optionIcon: ImageVector? = null,
    optionDrawable: Int? = null,
    isSwitch: Boolean = false,
    switchValue: Boolean = false,
    onSwitchChanged: (Boolean) -> Unit = {},
    onOptionClick: () -> Unit = {},
) {
    val highlighted = LocalPreferenceHighlighted.current
    val minHeight = LocalPreferenceMinHeight.current

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(enabled = !isSwitch) {
                if (!isSwitch) {
                    onOptionClick()
                }
            }
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = minHeight)
                .height(20.dp)
                .padding(horizontal = 16.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                optionName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            if (isSwitch) {
                Switch(
                    checked = switchValue,
                    onCheckedChange = { onSwitchChanged.invoke(it) },
                    modifier = Modifier.height(20.dp),
                )
            } else {
                if (optionIcon != null) {
                    Image(
                        imageVector = optionIcon,
                        contentDescription = "icon",
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer),
                    )
                } else {
                    ImageWrapper(
                        resource = optionDrawable!!,
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer),
                    )
                }
            }
        }
    }
}

