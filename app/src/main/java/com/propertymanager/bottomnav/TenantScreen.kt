package com.propertymanager.bottomnav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.propertymanager.R
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.MainNavigation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import propertymanager.presentation.components.NavBar
import propertymanager.presentation.components.NavigationItem

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun TenantScreen(
    homeNavController: NavHostController = rememberNavController(),
) {
    val showBottomNavEvent = Channel<Boolean>()

    val homeAnimatedIcon = AnimatedImageVector.animatedVectorResource(R.drawable.ic_home)
    val settingsAnimatedIcon = AnimatedImageVector.animatedVectorResource(R.drawable.ic_settings)
    val navigationItem = remember {
        listOf(
            NavigationItem(homeAnimatedIcon, text = "Home"),
            NavigationItem(settingsAnimatedIcon, text = "Settings"),
        )
    }

    val backStackState = homeNavController.currentBackStackEntryAsState().value
    var selectedItem by remember {
        mutableIntStateOf(0)
    }

    selectedItem = when (backStackState?.destination?.route) {
        BottomScreens.Home.name -> 0
        BottomScreens.Settings.name -> 1
        else -> 0
    }

    // Hide the bottom navigation when the user is in the details screen
    val isBarVisible = remember(key1 = backStackState) {
        backStackState?.destination?.route == BottomScreens.Home.name ||
            backStackState?.destination?.route == BottomScreens.Settings.name
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isBarVisible) {
                val bottomNavVisible by produceState(initialValue = true) {
                    showBottomNavEvent.receiveAsFlow().collectLatest { value = it }
                }
                AnimatedVisibility(
                    visible = bottomNavVisible,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                ) {
                    NavBar(
                        items = navigationItem,
                        selectedItem = selectedItem,
                        onItemClick = { index ->
                            when (index) {
                                0 -> navigateToTab(
                                    navController = homeNavController,
                                    screen = Dest.MaintenanceListScreen
                                )

                                1 -> navigateToTab(
                                    navController = homeNavController,
                                    screen = Dest.TenantSettingsScreen
                                )
                            }
                        },
                    )
                }
            }
        },
    ) { innerPadding ->

    }
}

private fun navigateToTab(navController: NavController, screen: Dest) {
    navController.navigate(screen) {
        navController.graph.startDestinationRoute?.let { screenRoute ->
            popUpTo(screenRoute) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}

