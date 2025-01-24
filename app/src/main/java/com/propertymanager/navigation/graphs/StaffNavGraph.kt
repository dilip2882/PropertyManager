package com.propertymanager.navigation.graphs

import CityPropertiesScreen
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.propertymanager.bottomnav.staff.StaffBottomNavItem
import com.propertymanager.bottomnav.staff.StaffScreen
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import propertymanager.feature.staff.home.StaffHomeScreen
import propertymanager.feature.staff.property.PropertyApproveScreen
import propertymanager.feature.staff.settings.StaffSettingsScreen
import propertymanager.feature.staff.settings.category.CategoryManagerScreen
import propertymanager.presentation.components.property.PropertyManagerScreen
import propertymanager.presentation.components.location.LocationManagerScreen
import propertymanager.presentation.components.location.LocationScreen
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.property.AddPropertyScreen
import propertymanager.presentation.components.property.components.SelectCityScreen
import propertymanager.presentation.components.property.components.SelectCountryScreen
import propertymanager.presentation.components.property.components.SelectSocietyScreen
import propertymanager.presentation.components.property.components.SelectStateScreen
import propertymanager.presentation.components.user.EditProfileScreen
import propertymanager.presentation.components.user.ProfileScreen
import propertymanager.presentation.components.user.UserViewModel
import propertymanager.presentation.components.location.components.CityManagerScreen
import propertymanager.presentation.components.location.components.CountryManagerScreen
import propertymanager.presentation.components.location.LocationManagerViewModel
import propertymanager.presentation.components.location.components.StateManagerScreen
import propertymanager.presentation.components.property.components.SelectFlatScreen
import propertymanager.presentation.components.property.components.SelectCityMode
import propertymanager.presentation.components.user.UserEvent

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

        composable<Dest.ProfileScreen> {
            ProfileScreen(
                onNavigateToEditProfile = {
                    navController.navigate(Dest.EditProfileScreen)
                },
                viewModel = hiltViewModel<UserViewModel>()
            )
        }

        composable<Dest.EditProfileScreen> {
           EditProfileScreen(
               viewModel = hiltViewModel(),
               onNavigateBack = {
                   navController.navigateUp()
               }
           )
        }

        composable<Dest.StaffSettingsScreen> {
            StaffSettingsScreen(
                onNavigateToEditProfile = {
                    navController.navigate(Dest.EditProfileScreen)
                },
                onNavigateToRoles = {
                    navController.navigate(Dest.LocationManagerScreen)
                },
                onNavigateToCategoryManager = {
                    navController.navigate(Dest.CategoryManagerScreen)
                },
                onNavigateToPropertyManager = {
                    navController.navigate(Dest.PropertyApproveScreen)
                },
                onNavigateToLocationManager = {
                    navController.navigate(Dest.CountryManagerScreen)
                }
            )
        }

        composable<Dest.CategoryManagerScreen> {
            CategoryManagerScreen(
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable<Dest.PropertyApproveScreen> {
            PropertyApproveScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = {
                    navController.navigateUp()
                }
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

        composable<Dest.LocationScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<Dest.LocationManagerScreen>()
            LocationScreen(
                cityId = args.cityId,
                onNavigateBack = { navController.navigateUp() },
                viewModel = hiltViewModel()
            )
        }

        composable<Dest.SelectCountryScreen> {
            val sharedViewModel: LocationViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(StaffBottomNavItem.HOME.route) }
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
                remember { navController.getBackStackEntry(StaffBottomNavItem.HOME.route) }
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
                remember { navController.getBackStackEntry(StaffBottomNavItem.HOME.route) }
            )
            SelectCityScreen(
                viewModel = sharedViewModel,
                mode = SelectCityMode.SELECT_PROPERTY,
                onNavigateToAddProperty = {
                    navController.navigate(Dest.AddPropertyScreen)
                },
                onNavigateToPropertyList = { cityId ->
                    navController.navigate(Dest.CityPropertiesScreen(cityId.toInt()))
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Dest.SelectFlatScreen> { backStackEntry ->
            val sharedViewModel: LocationViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(Dest.StaffHomeScreen) }
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

        composable<Dest.AddPropertyScreen> {
            val sharedViewModel: LocationViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(Dest.PropertyManagerScreen) }
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
                    navController.navigate(Dest.SelectFlatScreen(parentId, buildingType))
                },
                onNavigateToSelectSociety = {
                    navController.navigate(Dest.SelectSocietyScreen)
                },
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Dest.EditPropertyScreen> { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId")
        }

        composable<Dest.SelectSocietyScreen> {
            val sharedViewModel: LocationViewModel = hiltViewModel(
                remember { navController.getBackStackEntry(Dest.PropertyManagerScreen) }
            )
            SelectSocietyScreen(
                locationViewModel = sharedViewModel,
                onSocietySelected = {
                    navController.navigate(Dest.AddPropertyScreen)
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<Dest.CountryManagerScreen> {
            val viewModel: LocationManagerViewModel = hiltViewModel()
            CountryManagerScreen(
                viewModel = viewModel,
                onNavigateToState = { countryId ->
                    navController.navigate(Dest.StateManagerScreen(countryId))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<Dest.StateManagerScreen> { backStackEntry ->
            val viewModel: LocationManagerViewModel = hiltViewModel()
            val args = backStackEntry.toRoute<Dest.StateManagerScreen>()
            StateManagerScreen(
                countryId = args.countryId,
                locationManagerViewModel = viewModel,
                locationViewModel = hiltViewModel<LocationViewModel>(),
                onNavigateToCity = { stateId ->
                    navController.navigate(Dest.CityManagerScreen(stateId))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<Dest.CityManagerScreen> { backStackEntry ->
            val viewModel: LocationManagerViewModel = hiltViewModel()
            val args = backStackEntry.toRoute<Dest.CityManagerScreen>()
            CityManagerScreen(
                stateId = args.stateId,
                locationManagerViewModel = viewModel,
                locationViewModel = hiltViewModel<LocationViewModel>(),
                onNavigateToLocation = { cityId ->
                    navController.navigate(Dest.LocationScreen(cityId))
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable<Dest.LocationManagerScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<Dest.LocationManagerScreen>()
            LocationManagerScreen(
                cityId = args.cityId,
                locationViewModel = hiltViewModel<LocationViewModel>(),
                locationManagerViewModel = hiltViewModel<LocationManagerViewModel>(),
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Dest.CityPropertiesScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<Dest.CityPropertiesScreen>()
            val userViewModel = hiltViewModel<UserViewModel>()
            println("DEBUG: Navigating to CityPropertiesScreen with cityId: ${args.cityId}")  // Debug log
            CityPropertiesScreen(
                cityId = args.cityId,
                onPropertySelected = { propertyId ->
                    userViewModel.onEvent(UserEvent.SelectProperty(propertyId))
                    navController.navigateUp()
                },
                onNavigateBack = { navController.navigateUp() }
            )
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
