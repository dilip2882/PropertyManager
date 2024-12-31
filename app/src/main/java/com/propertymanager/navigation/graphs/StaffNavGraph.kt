package com.propertymanager.navigation.graphs

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.bottomnav.staff.StaffScreen
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import propertymanager.feature.staff.StaffHomeScreen
import propertymanager.feature.staff.settings.StaffSettingsScreen
import propertymanager.feature.staff.settings.category.CategoryManagerScreen
import propertymanager.feature.staff.settings.location.LocationManagerScreen
import propertymanager.feature.staff.settings.location.LocationScreen
import propertymanager.feature.staff.settings.property.AddPropertyScreen
import propertymanager.feature.staff.settings.property.AddressScreen
import propertymanager.feature.staff.settings.property.LocationViewModel
import propertymanager.feature.staff.settings.property.PropertyManagerScreen
import propertymanager.feature.staff.settings.property.PropertyViewModel
import propertymanager.feature.staff.settings.property.componenets.SelectCityScreen
import propertymanager.feature.staff.settings.property.componenets.SelectCountryScreen
import propertymanager.feature.staff.settings.property.componenets.SelectStateScreen

fun NavGraphBuilder.staffNavGraph(
    navController: NavHostController,
) {
    navigation<SubGraph.Staff>(startDestination = Dest.StaffScreen) {

        composable<Dest.StaffScreen> {
            StaffScreen()
        }

        composable<Dest.StaffHomeScreen> {
            StaffHomeScreen(
                staffId = "",
            )
        }

        composable<Dest.StaffSettingsScreen> {
            StaffSettingsScreen(
                onNavigateToRoles = {},
                onNavigateToCategoryManager = {
                    navController.navigate(Dest.CategoryManagerScreen)
                },
                onNavigateToPropertyManager = {
                    navController.navigate(Dest.PropertyManagerScreen)
                },
                onNavigateToLocationManager = {
                    navController.navigate(Dest.LocationManagerScreen)
                }
            )
        }

        composable<Dest.CategoryManagerScreen> {
            CategoryManagerScreen(
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable<Dest.PropertyManagerScreen> {
            PropertyManagerScreen(
                onNavigateToAddProperty = {
                    navController.navigate(Dest.SelectCountryScreen)
                },
                onNavigateBack = {
                    navController.navigateUp()
                },
                viewModel = hiltViewModel(),
            )
        }

        composable<Dest.LocationManagerScreen> {
            LocationManagerScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Dest.LocationScreen> {
            LocationScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Dest.SelectCountryScreen> {
            val sharedViewModel: LocationViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(Dest.PropertyManagerScreen) }
            )
            SelectCountryScreen(
                viewModel = sharedViewModel,
                onCountrySelected = {
                    navController.navigate(Dest.SelectStateScreen)
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Dest.SelectStateScreen> {
            val sharedViewModel: LocationViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(Dest.PropertyManagerScreen) }
            )
            SelectStateScreen(
                viewModel = sharedViewModel,
                onStateSelected = {
                    navController.navigate(Dest.SelectCityScreen)
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Dest.SelectCityScreen> {
            val sharedViewModel: LocationViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(Dest.PropertyManagerScreen) }
            )
            SelectCityScreen(
                viewModel = sharedViewModel,
                onCitySelected = {
                    navController.navigate(Dest.AddPropertyScreen)
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Dest.AddPropertyScreen> {
            val sharedViewModel: LocationViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(Dest.PropertyManagerScreen) }
            )
            AddPropertyScreen(
                viewModel = hiltViewModel(),
                locationViewModel = sharedViewModel,
                onPropertyAdded = {
                    navController.navigate(Dest.PropertyManagerScreen) {
                        popUpTo(Dest.PropertyManagerScreen) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Dest.EditPropertyScreen> { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId")
        }
    }
}

/*
fun NavController.navigateToStaffDestination(
    destination: Dest,
    builder: NavOptionsBuilder.() -> Unit = {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    },
) {
    val route = when (destination) {
        is Dest.StaffHomeScreen -> destination::class.qualifiedName!!
        is Dest.StaffSettingsScreen -> destination::class.qualifiedName!!
        else -> throw IllegalArgumentException("Invalid staff destination: $destination")
    }
    navigate(route) { builder() }
}


sealed class StaffNavigationEvent {
    data object NavigateToHome : StaffNavigationEvent()
    data object NavigateToSettings : StaffNavigationEvent()
    data object NavigateBack : StaffNavigationEvent()
}

fun NavController.handleStaffNavigationEvent(event: StaffNavigationEvent) {
    when (event) {
        is StaffNavigationEvent.NavigateToHome -> {
            navigateToStaffDestination(Dest.StaffHomeScreen)
        }

        is StaffNavigationEvent.NavigateToSettings -> {
            navigateToStaffDestination(Dest.StaffSettingsScreen)
        }

        is StaffNavigationEvent.NavigateBack -> {
            popBackStack()
        }
    }
}
*/
