package com.propertymanager.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.propertymanager.bottomnav.tenant.TenantScreen
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import propertymanager.feature.tenant.home.MaintenanceListScreen
import propertymanager.feature.tenant.home.MaintenanceRequestScreen
import propertymanager.feature.tenant.home.components.MaintenanceCategoriesScreen
import propertymanager.feature.tenant.profile.TenantProfileScreen

fun NavGraphBuilder.tenantNavGraph(
    navController: NavHostController
) {

    navigation<SubGraph.Tenant>(startDestination = Dest.TenantScreen) {

        composable<Dest.TenantScreen> {
            TenantScreen()
        }

        composable<Dest.MaintenanceListScreen> {
            MaintenanceListScreen(
                onNavigateToMaintenanceRequest = {
                    navController.navigate(Dest.MaintenanceCategoriesScreen)
                }
            )
        }

        composable<Dest.MaintenanceCategoriesScreen> {
            MaintenanceCategoriesScreen(
                onCategorySelected = {
                    navController.navigate(Dest.MaintenanceRequestScreen)
                }
            )
        }

        composable<Dest.MaintenanceRequestScreen> {
            MaintenanceRequestScreen(
                selectedCategory = it.arguments?.getString("category") ?: "",
                onSubmit = { request ->
                    navController.navigate(Dest.MaintenanceListScreen)
                }
            )
        }

        composable<Dest.TenantSettingsScreen> {
            TenantProfileScreen(
                onNavigateToEditProfile = {
                    navController.navigate(Dest.OnboardingFormScreen)
                }
            )
        }
    }
}
