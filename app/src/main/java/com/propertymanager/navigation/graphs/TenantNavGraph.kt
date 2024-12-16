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
                    navController.navigate(Dest.MaintenanceRequestScreen)
                }
            )
        }

        composable<Dest.MaintenanceRequestScreen> {
            MaintenanceRequestScreen(
                navController = navController,
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
