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
import com.propertymanager.navigation.graphs.authNavGraph
import com.propertymanager.navigation.graphs.staffNavGraph
import propertymanager.feature.onboarding.OnboardingFormScreen
import propertymanager.feature.onboarding.OnboardingViewModel
import propertymanager.feature.staff.StaffFlatScreen
import propertymanager.feature.staff.settings.StaffSettingsScreen
import propertymanager.presentation.components.user.EditProfileScreen
import propertymanager.presentation.components.user.ProfileScreen
import propertymanager.presentation.components.user.UserViewModel

@Composable
fun StaffScreen(
    navController: NavHostController = rememberNavController(),
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    val isBottomBarVisible = remember(currentDestination) {
        when (currentDestination?.route) {
            StaffBottomNavItem.HOME.route,
            StaffBottomNavItem.SETTINGS.route,
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
            startDestination = StaffBottomNavItem.HOME.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(StaffBottomNavItem.HOME.route) {
                StaffFlatScreen(
                    propertyViewModel = hiltViewModel(),
                    locationViewModel = hiltViewModel(),
                    onNavigateToHome = { property ->
                        navController.navigate(Dest.StaffHomeScreen(property.id))
                    },
                )
            }

            composable(StaffBottomNavItem.SETTINGS.route) {
                StaffSettingsScreen(
                    onNavigateToEditProfile = {
                        navController.navigate(Dest.EditProfileScreen)
                    },
                    onNavigateToRoles = {
                        navController.navigate(Dest.StaffFlatScreen)
                    },
                    onNavigateToCategoryManager = {
                        navController.navigate(Dest.CategoryManagerScreen)
                    },
                    onNavigateToPropertyManager = {
                        navController.navigate(Dest.PropertyApproveScreen)
                    },
                    onNavigateToLocationManager = {
                        navController.navigate(Dest.CountryManagerScreen)
                    },
                    onNavigateToPhoneScreen = {
                        navController.navigate(Dest.PhoneScreen)
                    },
                )
            }

            composable<Dest.ProfileScreen> {
                ProfileScreen(
                    onNavigateToEditProfile = {
                        navController.navigate(Dest.EditProfileScreen)
                    },
                    viewModel = hiltViewModel<UserViewModel>(),
                    modifier = Modifier,
                )
            }

            composable<Dest.EditProfileScreen> {
                EditProfileScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = {
                        navController.navigateUp()
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
            authNavGraph(navController)
        }
    }
}
