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
import propertymanager.presentation.components.property.PropertyManagerScreen
import propertymanager.feature.tenant.home.TenantHomeScreen
import propertymanager.feature.tenant.support.MaintenanceCategoriesScreen
import propertymanager.feature.tenant.support.MaintenanceDetailsScreen
import propertymanager.feature.tenant.support.MaintenanceListScreen
import propertymanager.feature.tenant.support.MaintenanceRequestScreen
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.property.AddPropertyScreen
import propertymanager.presentation.components.property.components.SelectCityScreen
import propertymanager.presentation.components.property.components.SelectCountryScreen
import propertymanager.presentation.components.property.components.SelectFlatScreen
import propertymanager.presentation.components.property.components.SelectSocietyScreen
import propertymanager.presentation.components.property.components.SelectStateScreen

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
            SelectCityScreen(
                viewModel = sharedViewModel,
                onNavigateToAddProperty = {
                    navController.navigate(Dest.AddPropertyScreen)
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }
    }

    composable<Dest.SelectSocietyScreen> {
        val sharedViewModel: LocationViewModel = hiltViewModel(
            remember { navController.getBackStackEntry(TenantBottomNavItem.HOME.route) }
        )
        SelectSocietyScreen(
            locationViewModel = sharedViewModel,
            onSocietySelected = {
                navController.navigate(Dest.AddPropertyScreen)
            },
            onNavigateBack = { navController.navigateUp() }
        )
    }

    composable<Dest.SelectFlatScreen> { backStackEntry ->
        val sharedViewModel: LocationViewModel = hiltViewModel(
            remember { navController.getBackStackEntry(TenantBottomNavItem.HOME.route) },
        )
        val args = backStackEntry.toRoute<Dest.SelectFlatScreen>()
        SelectFlatScreen(
            locationViewModel = sharedViewModel,
            propertyViewModel = hiltViewModel(),
            buildingType = args.buildingType,
            parentId = args.parentId,
            onNavigateBack = { navController.navigateUp() }
        )
    }

    composable<Dest.PropertyManagerScreen> {
        PropertyManagerScreen(
            onNavigateToAddProperty = {
                navController.navigate(Dest.SelectCountryScreen)
            },
            onNavigateToEditProperty = {

            },
            onNavigateBack = {
                navController.navigateUp()
            },
            viewModel = hiltViewModel(),
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
                navController.navigate(TenantBottomNavItem.HOME.route) {
                    popUpTo(TenantBottomNavItem.HOME.route) { inclusive = true }
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
                navController.navigate(Dest.SelectFlatScreen(parentId, buildingType))
            },
            onNavigateToSelectSociety = {
                navController.navigate(Dest.SelectSocietyScreen)
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
            onNavigateToAddProperty = {
                navController.navigate(Dest.SelectCountryScreen)
            },
            propertyViewModel = hiltViewModel(),
            userViewModel = hiltViewModel()
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
                navController.navigate(TenantBottomNavItem.HOME.route) {
                    popUpTo(Dest.MaintenanceCategoriesScreen) { inclusive = true } // Clear the previous screens
                    launchSingleTop = true
                }
            },
        )
    }
}
