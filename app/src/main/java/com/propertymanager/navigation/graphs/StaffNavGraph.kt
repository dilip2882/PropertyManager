package com.propertymanager.navigation.graphs

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.propertymanager.bottomnav.staff.StaffScreen
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import propertymanager.feature.staff.StaffHomeScreen
import propertymanager.feature.staff.settings.StaffSettingsScreen
import propertymanager.feature.staff.settings.category.CategoryManagerScreen
import propertymanager.feature.staff.settings.property.AddPropertyScreen
import propertymanager.feature.staff.settings.property.PropertyManagerScreen
import propertymanager.feature.staff.settings.property.SelectCityScreen
import propertymanager.feature.staff.settings.property.SelectCountryScreen

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
                    navController.navigate(Dest.MaintenanceCategoriesScreen)
                },
                onNavigateToPropertyManager = {
                    navController.navigate(Dest.PropertyManagerScreen)
                },
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
                viewModel = hiltViewModel(),
            )
        }

        composable<Dest.SelectCountryScreen> {
            SelectCountryScreen(
                onCountrySelected = {
                    navController.navigate(Dest.SelectCityScreen)
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Dest.SelectCityScreen> {
            SelectCityScreen(
                selectedCountry = "NA",
                onCitySelected = {
                    navController.navigate(Dest.AddPropertyScreen)
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Dest.AddPropertyScreen> {
            AddPropertyScreen(
                viewModel = hiltViewModel(),
                selectedCountry = "",
                selectedCity = "",
                onPropertyAdded = {},
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
