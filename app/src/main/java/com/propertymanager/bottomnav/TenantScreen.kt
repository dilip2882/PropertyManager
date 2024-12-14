package com.propertymanager.bottomnav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.propertymanager.bottomnav.tenant.TenantTopLevelDestination
import com.propertymanager.bottomnav.tenant.navigateToHome
import com.propertymanager.bottomnav.tenant.navigateToSettings
import com.propertymanager.ui.theme.PropertyManagerTheme
import propertymanager.presentation.components.PropertyManagerIcons

@Composable
fun TenantScreen(
    navController: NavHostController, // pass NavHostController as a parameter for navigation
    topLevelNavOptions: NavOptions, // navigation options
) {
    Scaffold(
        bottomBar = {
            TenantNavigationBar(
                selectedItem = navController.currentBackStackEntry?.destination?.route ?: "",
                onItemSelected = { selectedItem ->
                    when (selectedItem) {
                        TenantTopLevelDestination.HOME.route.simpleName -> {
                            navController.navigateToHome(topLevelNavOptions)
                        }
                        TenantTopLevelDestination.SETTINGS.route.simpleName -> {
                            navController.navigateToSettings(topLevelNavOptions)
                        }
                    }
                }
            )
        },
        content = { paddingValues ->
            val contentPadding = WindowInsets
                .systemBars
                .add(WindowInsets(left = 16.dp, top = 16.dp, right = 16.dp, bottom = 16.dp))
                .asPaddingValues()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item { Text("Navigation", Modifier.padding(top = 16.dp)) }
                item {
                    Text("Tenant Dashboard or any other content", Modifier.padding(top = 16.dp))
                }
            }
        }
    )
}


@Composable
fun TenantNavigationBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        TenantTopLevelDestination.HOME.route.simpleName,
        TenantTopLevelDestination.SETTINGS.route.simpleName
    )

    NavigationBar(modifier = modifier) {
        items.forEachIndexed { index, item ->
            TenantNavigationBarItem(
                selected = selectedItem == item,
                onClick = {
                    if (item != null) {
                        onItemSelected(item)
                    }
                },
                label = { item?.let { Text(it) } },
                icon = {
                    val icon = if (item == TenantTopLevelDestination.HOME.route.simpleName) {
                        PropertyManagerIcons.Home
                    } else {
                        PropertyManagerIcons.Settings
                    }
                    Icon(imageVector = icon, contentDescription = item)
                },
            )
        }
    }
}

@Composable
fun RowScope.TenantNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        label = label,
        modifier = modifier,
        alwaysShowLabel = true,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        )
    )
}


object NiaNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}
