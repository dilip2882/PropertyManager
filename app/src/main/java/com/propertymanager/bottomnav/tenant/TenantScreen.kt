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
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.graphs.tenantNavGraph
import propertymanager.feature.onboarding.OnboardingFormScreen
import propertymanager.feature.onboarding.OnboardingViewModel
import propertymanager.feature.tenant.home.MaintenanceListScreen
import propertymanager.feature.tenant.profile.TenantProfileScreen

@Composable
fun TenantScreen(
    navController: NavHostController = rememberNavController(),
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    val isBottomBarVisible = remember(currentDestination) {
        when (currentDestination?.route) {
            TenantBottomNavItem.Home.route,
            TenantBottomNavItem.Person.route,
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
            startDestination = TenantBottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(TenantBottomNavItem.Home.route) {
                MaintenanceListScreen(
                    onNavigateToMaintenanceRequest = {
                        navController.navigate(Dest.MaintenanceCategoriesScreen)
                    },
                    onNavigateToDetails = { requestId ->
                        navController.navigate(Dest.MaintenanceDetailsScreen(requestId))
                    },
                )
            }

            composable(TenantBottomNavItem.Person.route) {
                TenantProfileScreen(
                    onNavigateToEditProfile = {
                        navController.navigate(Dest.OnboardingFormScreen)
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

            tenantNavGraph(navController)
        }
    }

}
