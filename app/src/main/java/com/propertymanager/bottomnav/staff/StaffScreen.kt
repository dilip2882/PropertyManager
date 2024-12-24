package com.propertymanager.bottomnav.staff

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.graphs.staffNavGraph
import propertymanager.feature.onboarding.OnboardingFormScreen
import propertymanager.feature.onboarding.OnboardingViewModel
import propertymanager.feature.staff.StaffHomeScreen
import propertymanager.feature.staff.settings.StaffSettingsScreen
import propertymanager.feature.tenant.home.MaintenanceCategoriesScreen

@Composable
fun StaffScreen(
    navController: NavHostController = rememberNavController(),
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    val isBottomBarVisible = remember(currentDestination) {
        when (currentDestination?.route) {
            StaffBottomNavItem.Home.route,
            StaffBottomNavItem.Settings.route,
                -> true

            else -> false
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                StaffNavBar(
                    currentDestination = currentDestination?.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = StaffBottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(StaffBottomNavItem.Home.route) {
                StaffHomeScreen(
                    staffId = "",
                )

            }

            composable(StaffBottomNavItem.Settings.route) {
                StaffSettingsScreen(
                    onNavigateToRoles = {},
                    onNavigateToCategoryManager = {
                        navController.navigate(Dest.CategoryManagerScreen)
                    },
                    onNavigateToPropertyManager = {
                        navController.navigate(Dest.PropertyManagerScreen)
                    },
                )
            }

            composable<Dest.OnboardingFormScreen> {
                val viewModel: OnboardingViewModel = hiltViewModel()
                val state by viewModel.state.collectAsState()
                val effect by viewModel.effect.collectAsState(initial = null)

                OnboardingFormScreen(
                    state = state,
                    effect = effect,
                    dispatch = { event -> viewModel.event(event) },
                    onComplete = { navController.navigate(Dest.HomeScreen) },
                )
            }

            staffNavGraph(navController)
        }
    }

}

