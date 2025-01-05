package propertymanager.feature.staff.settings

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.User
import com.propertymanager.domain.model.biometrics.BiometricAuthState
import com.propertymanager.domain.model.biometrics.BiometricCheckResult
import propertymanager.i18n.MR
import propertymanager.presentation.components.ImageWrapper
import propertymanager.presentation.components.LocalPreferenceHighlighted
import propertymanager.presentation.components.LocalPreferenceMinHeight
import propertymanager.presentation.components.TextPreferenceWidget
import propertymanager.presentation.i18n.stringResource
import propertymanager.presentation.screens.LoadingScreen
import propertymanager.presentation.user.ProfileScreen
import propertymanager.presentation.user.UserViewModel
import java.util.Locale

@Composable
fun StaffSettingsScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToCategoryManager: () -> Unit,
    onNavigateToPropertyManager: () -> Unit,
    onNavigateToLocationManager: () -> Unit,
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

    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }

    val languageOptions = listOf("English", "Hindi", "Spanish", "French")

    fun changeLanguage(language: String) {
        val locale = when (language) {
            "Hindi" -> Locale("hi", "IN")
            "Spanish" -> Locale("es", "ES")
            "French" -> Locale("fr", "FR")
            else -> Locale("en", "US")
        }
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        val sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("language", language)
            apply()
        }
    }

    LaunchedEffect(key1 = Unit) {
        val sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val savedLanguage = sharedPref.getString("language", "English") ?: "English"
        selectedLanguage = savedLanguage
        changeLanguage(savedLanguage)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(stringResource(MR.strings.staff_settings_title)) },

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
//                Profile(user = User())
            }

            // Language Dropdown
            item {
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        languageOptions.forEach { language ->
                            LanguageDropdownItem(
                                language = language,
                                selectedLanguage = selectedLanguage,
                                onClick = {
                                    selectedLanguage = language
                                    changeLanguage(language)
                                    expanded = false
                                },
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clickable { expanded = !expanded }
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(15.dp))
                        .padding(16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(8.dp),
                        ),
                    ) {
                        Text(
                            text = selectedLanguage,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Arrow",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SettingsItem(
                    optionName = stringResource(MR.strings.staff_dark_mode),
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
                TextPreferenceWidget(
                    title = stringResource(MR.strings.staff_location),
                    icon = Icons.Default.LocationOn,
                    onPreferenceClick = {
                        onNavigateToLocationManager()
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextPreferenceWidget(
                    title = stringResource(MR.strings.staff_category),
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
fun Profile(user: User) {
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                // Profile Header
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
                                Brush.linearGradient(
                                    listOf(Color(0xFF5CD6FF), Color(0xFF5CD6FF)),
                                ),
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
                            // Profile Image
                            if (fetchedUser.profileImage.isNullOrEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Default Profile Icon",
                                    modifier = Modifier.size(50.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                )
                            } else {
                                Image(
                                    painter = rememberAsyncImagePainter(fetchedUser.profileImage),
                                    contentDescription = "Profile Image",
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }

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

                // Name
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = fetchedUser.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = fetchedUser.role,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp),
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
            .padding(start = 10.dp, end = 5.dp)
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

@Composable
fun LanguageDropdownItem(
    language: String,
    selectedLanguage: String,
    onClick: () -> Unit,
) {
    val backgroundColor = if (language == selectedLanguage) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(backgroundColor)
            .padding(16.dp),
    ) {
        Text(
            text = language,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
