package propertymanager.feature.tenant.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import propertymanager.feature.auth.presentation.AuthViewModel
import propertymanager.feature.auth.presentation.mvi.AuthContract
import propertymanager.presentation.components.user.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPropertyManager: () -> Unit,
    onNavigateToPhoneScreen: () -> Unit,
    themeViewModel: TenantThemeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val darkMode by themeViewModel.darkMode.collectAsState()
    val dynamicColor by themeViewModel.dynamicColor.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.effect.collect { effect ->
            when (effect) {
                is AuthContract.AuthEffect.NNavigateToPhoneScreen -> onNavigateToPhoneScreen()
                else -> {}
            }
        }
    }

    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
//                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
//                modifier = Modifier.height(128.dp),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
        ) {
            item {
                ProfileScreen(
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    modifier = Modifier,
                )
            }

            item {
                SettingsSection(
                    title = "Property Manager",
                    icon = Icons.Default.LocationCity,
                    subtitle = "Manage your property details",
                    onClick = onNavigateToPropertyManager,
                )
            }

            item {
                AppearanceSection(
                    darkMode = darkMode,
                    dynamicColor = dynamicColor,
                    onDarkModeChange = { enabled ->
                        themeViewModel.setDarkMode(enabled)
                    },
                    onDynamicColorChange = { enabled ->
                        themeViewModel.setDynamicColor(enabled)
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                LogoutSection(onLogout = { authViewModel.event(AuthContract.AuthEvent.SignOut) })
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun AppearanceSection(
    darkMode: Boolean,
    dynamicColor: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Appearance Header
        /*        SettingsSection(
                    title = "Appearance",
                    subtitle = "Theme, language & animations",
                    icon = Icons.Default.Palette,
                    onClick = { }
                )
                */
        // Theme Options
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column {
                PreferenceToggleItem(
                    title = "Use dark theme",
                    subtitle = "Toggle between light and dark theme",
                    icon = Icons.Default.DarkMode,
                    checked = darkMode,
                    onCheckedChange = onDarkModeChange,
                    showDivider = true,
                )

                PreferenceToggleItem(
                    title = "Dynamic color",
                    subtitle = "Use system accent colors",
                    icon = Icons.Default.ColorLens,
                    checked = dynamicColor,
                    onCheckedChange = onDynamicColorChange,
                    showDivider = false,
                )
            }
        }
    }
}

@Composable
private fun PreferenceToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean = false,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 64.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
            )
        }
    }
}

@Composable
private fun LogoutSection(onLogout: () -> Unit) {
    val authViewModel = hiltViewModel<AuthViewModel>()
    val context = LocalContext.current

    SettingsSection(
        title = "Logout",
        subtitle = "Sign out from your account",
        icon = Icons.AutoMirrored.Filled.Logout,
        onClick = {
            authViewModel.event(AuthContract.AuthEvent.SignOut)
            onLogout()
        },
    )
}
