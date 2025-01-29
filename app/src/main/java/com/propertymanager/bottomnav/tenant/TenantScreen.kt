package com.propertymanager.bottomnav.tenant

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import com.propertymanager.navigation.graphs.authNavGraph
import com.propertymanager.navigation.graphs.tenantNavGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import propertymanager.feature.onboarding.OnboardingFormScreen
import propertymanager.feature.onboarding.OnboardingViewModel
import propertymanager.feature.tenant.profile.TenantProfileScreen
import propertymanager.feature.tenant.support.MaintenanceListScreen
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.property.AddPropertyScreen
import propertymanager.presentation.components.user.EditProfileScreen


@Composable
fun TenantScreen(
    navController: NavHostController = rememberNavController(),
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val scope: CoroutineScope = rememberCoroutineScope()
    val viewModel: TenantScreenViewModel = hiltViewModel()

    val isBottomBarVisible = remember(currentDestination) {
        when (currentDestination?.route) {
            TenantBottomNavItem.HOME.route,
            TenantBottomNavItem.SETTINGS.route,
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
                TenantNavBar(
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
            startDestination = TenantBottomNavItem.HOME.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(TenantBottomNavItem.HOME.route) {
                MaintenanceListScreen(
                    onNavigateToMaintenanceRequest = {
                        navController.navigate(Dest.MaintenanceCategoriesScreen)
                    },
                    onNavigateToDetails = { requestId ->
                        navController.navigate(Dest.MaintenanceDetailsScreen(requestId))
                    },
                    onNavigateToAddProperty = {
                        navController.navigate(Dest.SelectCountryScreen)
                    },
                    propertyViewModel = hiltViewModel(),
                    userViewModel = hiltViewModel(),
                )
            }

            composable(TenantBottomNavItem.SETTINGS.route) {
                TenantProfileScreen(
                    onNavigateToEditProfile = {
                        navController.navigate(Dest.EditProfileScreen)
                    },
                    onNavigateToPropertyManager = {
                        navController.navigate(Dest.PropertyManagerScreen)
                    },
                    onNavigateToPhoneScreen = {
                        scope.launch {
                            viewModel.clearAuthToken()
                            navController.navigate(Dest.PhoneScreen) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    },
                )
            }

            composable<Dest.AddPropertyScreen> {
                val sharedViewModel: LocationViewModel = hiltViewModel(
                    remember { navController.getBackStackEntry(TenantBottomNavItem.HOME.route) },
                )
                AddPropertyScreen(
                    propertyViewModel = hiltViewModel(),
                    locationViewModel = sharedViewModel,
                    onPropertyAdded = {
                        navController.navigate(Dest.PropertyManagerScreen) {
                            popUpTo(Dest.PropertyManagerScreen) { inclusive = true }
                        }
                    },
                    onNavigateToSelectCountry = {
                        navController.navigate(Dest.SelectCountryScreen)
                    },
                    onNavigateToSelectState = {
                        navController.navigate(Dest.SelectStateScreen)
                    },
                    onNavigateToSelectCity = {
                        navController.navigate(Dest.SelectCityScreen)
                    },
                    onNavigateToSelectFlat = { parentId, buildingType ->
                        navController.navigate(Dest.SelectFlatScreen(parentId, buildingType = buildingType))
                    },
                    onNavigateToSelectSociety = {
                        navController.navigate(Dest.SelectSocietyScreen)
                    },
                    onNavigateBack = { navController.navigateUp() },
                )
            }

            composable<Dest.ProfileScreen> {
                TenantProfileScreen(
                    onNavigateToEditProfile = {
                        navController.navigate(Dest.EditProfileScreen)
                    },
                    onNavigateToPropertyManager = {
                        navController.navigate(Dest.PropertyManagerScreen)
                    },
                    onNavigateToPhoneScreen = {
                        navController.navigate(Dest.PhoneScreen)
                    },
                )
            }

            composable<Dest.EditProfileScreen> {
                EditProfileScreen(
                    onNavigateBack = { navController.popBackStack() },
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

            tenantNavGraph(navController)
            authNavGraph(navController)
        }
    }

}
