package com.propertymanager.bottomnav.tenant

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.propertymanager.domain.model.Category
import com.propertymanager.navigation.Dest
import propertymanager.feature.onboarding.OnboardingFormScreen
import propertymanager.feature.onboarding.OnboardingViewModel
import propertymanager.feature.tenant.home.MaintenanceListScreen
import propertymanager.feature.tenant.home.MaintenanceRequestScreen
import propertymanager.feature.tenant.home.MaintenanceCategoriesScreen
import propertymanager.feature.tenant.profile.TenantProfileScreen

@Composable
fun TenantScreen(
    navController: NavHostController = rememberNavController(),
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                TenantBottomNavItem.getAllItems().forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.route == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                    )
                }
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
                    onNavigateToMaintenanceRequest = { requestId ->
                        navController.navigate(Dest.MaintenanceCategoriesScreen)
                    },
                )
            }

            composable(TenantBottomNavItem.Profile.route) {
                TenantProfileScreen(
                    onNavigateToEditProfile = {
                        navController.navigate(Dest.OnboardingFormScreen)
                    }
                )
            }

            composable<Dest.MaintenanceCategoriesScreen> {
                MaintenanceCategoriesScreen(
                    category = Category(),
                    onNavigateUp = { navController.navigateUp() },
                    onCategorySelected = { category, subCategory ->
                        navController.navigate(Dest.MaintenanceRequestScreen(category, subCategory))
                    },
                )
            }

            composable<Dest.MaintenanceRequestScreen> {
                val args = it.toRoute<Dest.MaintenanceRequestScreen>()

                MaintenanceRequestScreen(
                    selectedCategory = args.category,
                    selectedSubcategory = args.subcategory,
                    onNavigateUp = { navController.navigateUp() },
                    onSubmit = {
                        navController.navigate(Dest.MaintenanceListScreen)
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
        }
    }
}


