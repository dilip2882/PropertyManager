package com.propertymanager.navigation.graphs

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.propertymanager.bottomnav.tenant.TenantBottomNavItem
import com.propertymanager.bottomnav.tenant.TenantScreen
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import propertymanager.feature.tenant.home.TenantHomeScreen
import propertymanager.feature.tenant.support.MaintenanceCategoriesScreen
import propertymanager.feature.tenant.support.MaintenanceDetailsScreen
import propertymanager.feature.tenant.support.MaintenanceListScreen
import propertymanager.feature.tenant.support.MaintenanceRequestScreen
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.property.AddPropertyScreen
import propertymanager.presentation.components.property.SelectCityScreen
import propertymanager.presentation.components.property.SelectCountryScreen
import propertymanager.presentation.components.property.SelectFlatScreen
import propertymanager.presentation.components.property.SelectStateScreen

fun NavGraphBuilder.tenantNavGraph(
    navController: NavHostController,
) {
    navigation<SubGraph.Tenant>(startDestination = Dest.TenantScreen) {

        composable<Dest.TenantScreen> {
            TenantScreen()
        }

        composable<Dest.TenantHomeScreen> {
            TenantHomeScreen(
                propertyViewModel = hiltViewModel(),
                onNavigateToAddProperty = {
                    navController.navigate(Dest.SelectCountryScreen)
                },
            )
        }

        composable<Dest.SelectCountryScreen> {
            val sharedViewModel: LocationViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(TenantBottomNavItem.HOME.route) },
            )
            SelectCountryScreen(
                viewModel = sharedViewModel,
                onNavigateToState = {
                    navController.navigate(Dest.SelectStateScreen)
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Dest.SelectStateScreen> {
            val sharedViewModel: LocationViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(TenantBottomNavItem.HOME.route) },
            )
            SelectStateScreen(
                viewModel = sharedViewModel,
                onNavigateToCity = {
                    navController.navigate(Dest.SelectCityScreen)
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Dest.SelectCityScreen> {
            val sharedViewModel: LocationViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(TenantBottomNavItem.HOME.route) },
            )
            SelectCityScreen(
                viewModel = sharedViewModel,
                onNavigateToAddProperty = {
                    navController.navigate(Dest.AddPropertyScreen)
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Dest.SelectCityScreen> {
            val sharedViewModel: LocationViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(TenantBottomNavItem.HOME.route) },
            )
            SelectFlatScreen(
                locationViewModel = sharedViewModel,
                parentId = it.toRoute(),
                onFlatSelected = {
                    navController.navigate(Dest.AddPropertyScreen)
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }
    }

    composable<Dest.SelectFlatScreen> {
        val sharedViewModel: LocationViewModel = hiltViewModel(
            remember { navController.getBackStackEntry(Dest.PropertyManagerScreen) },
        )
        SelectFlatScreen(
            locationViewModel = sharedViewModel,
            onFlatSelected = {
                navController.navigate(Dest.AddPropertyScreen)
            },
            onNavigateBack = { navController.navigateUp() },
            parentId = it.toRoute(),
        )
    }

    composable<Dest.AddPropertyScreen> {
        val sharedViewModel: LocationViewModel = hiltViewModel(
            remember { navController.getBackStackEntry(TenantBottomNavItem.HOME.route) },
        )
        AddPropertyScreen(
            viewModel = hiltViewModel(),
            locationViewModel = sharedViewModel,
            onPropertyAdded = {
                navController.navigate(TenantBottomNavItem.HOME.route) {
                    popUpTo(TenantBottomNavItem.HOME.route) { inclusive = true }
                }
            },
            onNavigateToSelectSociety = {
                navController.navigate(Dest.SelectSocietyScreen)
            },
            onNavigateToSelectFlat = {
                navController.navigate(Dest.SelectFlatScreen)
            },
            onNavigateBack = { navController.navigateUp() },
        )
    }

    composable<Dest.MaintenanceListScreen> {
        MaintenanceListScreen(
            onNavigateToMaintenanceRequest = {
                navController.navigate(Dest.MaintenanceCategoriesScreen)
            },
            onNavigateToDetails = { requestId ->
                navController.navigate(Dest.MaintenanceDetailsScreen(requestId))
            },
        )
    }


    composable<Dest.MaintenanceDetailsScreen> {
        val args = it.toRoute<Dest.MaintenanceDetailsScreen>()
        MaintenanceDetailsScreen(
            requestId = args.requestId,
            onNavigateUp = { navController.navigateUp() },
        )
    }

    composable<Dest.MaintenanceCategoriesScreen> {
        MaintenanceCategoriesScreen(
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
            onSubmitSuccess = {
                navController.navigate(TenantBottomNavItem.SUPPORT.route) {
                    popUpTo(Dest.MaintenanceCategoriesScreen) { inclusive = true } // Clear the previous screens
                    launchSingleTop = true
                }
            },
        )
    }
}
