package propertymanager.feature.staff.settings

import android.content.Context
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.biometrics.BiometricAuthState
import com.propertymanager.domain.model.biometrics.BiometricCheckResult
import propertymanager.feature.auth.presentation.AuthViewModel
import propertymanager.feature.auth.presentation.mvi.AuthContract
import propertymanager.i18n.MR
import propertymanager.presentation.components.ImageWrapper
import propertymanager.presentation.components.LocalPreferenceHighlighted
import propertymanager.presentation.components.LocalPreferenceMinHeight
import propertymanager.presentation.components.TextPreferenceWidget
import propertymanager.presentation.components.user.ProfileScreen
import propertymanager.presentation.i18n.stringResource
import java.util.Locale

@Composable
fun StaffSettingsScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToCategoryManager: () -> Unit,
    onNavigateToPropertyManager: () -> Unit,
    onNavigateToLocationManager: () -> Unit,
    onNavigateToRoles: () -> Unit,
    onNavigateToPhoneScreen: () -> Unit,
) {
    val viewModel = hiltViewModel<ThemeViewModel>()
    val settingsViewModel = hiltViewModel<StaffSettingsViewModel>()
    val biometricAuthViewModel = hiltViewModel<BiometricViewModel>()
    val authViewModel = hiltViewModel<AuthViewModel>()
    var showLogoutDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dynamicColor by viewModel.dynamicColor.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    val biometricAvailability by biometricAuthViewModel.biometricAvailability.collectAsState()
    val biometricAuth by biometricAuthViewModel.biometricAuthState.collectAsState()
    val currentLanguage by settingsViewModel.language.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }
    val languageOptions = listOf("English", "हिंदी", "Español", "Français")


    fun changeLanguage(language: String) {
        val locale = when (language) {
            "Hindi" -> Locale("hi", "IN")
            "Spanish" -> Locale("es", "ES")
            "French" -> Locale("fr", "FR")
            else -> Locale("en", "US")
        }
        Locale.setDefault(locale)

        val config = context.resources.configuration
//        config.setLocale(locale)
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

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.event(AuthContract.AuthEvent.SignOut)
                        onNavigateToPhoneScreen()
                    },
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            },
        )
    }

    val scope = rememberCoroutineScope()

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
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(stringResource(MR.strings.staff_settings_title)) },

                )
        },
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            item {
                ProfileScreen(
                    onNavigateToEditProfile = {
                        onNavigateToEditProfile()
                    },
                    modifier = Modifier,
                )
            }

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

            /*            item {
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
                        }*/

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextPreferenceWidget(
                    title = "Logout",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    onPreferenceClick = { showLogoutDialog = true },
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LanguageDropdownItem(
    language: String,
    selectedLanguage: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = language,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (language == selectedLanguage) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
            )
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
    optionValue: String? = null,
    showArrow: Boolean = false,
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
            if (optionValue != null) {
                Text(
                    text = optionValue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
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
            if (showArrow) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
