package com.propertymanager.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import propertymanager.feature.tenant.presentation.MaintenanceListScreen
import propertymanager.feature.tenant.presentation.MaintenanceRequestScreen
import propertymanager.feature.tenant.presentation.TenantScreen
import propertymanager.feature.tenant.presentation.TenantSettingsScreen

fun NavGraphBuilder.tenantNavGraph(navController: NavController) {

    navigation<SubGraph.Tenant>(startDestination = Dest.TenantScreen) {
        composable<Dest.TenantScreen> {
            TenantScreen(
                onNavigateToMaintenanceList = {
                    navController.navigate(Dest.MaintenanceListScreen)
                },
                onNavigateToTenantSettings = {
                    navController.navigate(Dest.TenantSettingsScreen)
                },
                navController = navController
            )
        }

        composable<Dest.TenantScreen> {
            MaintenanceListScreen(
                onNavigateToMaintenanceRequest = {
                    navController.navigate(Dest.MaintenanceRequestScreen)
                }
            )
        }

        composable<Dest.MaintenanceRequestScreen> {
            MaintenanceRequestScreen(
                navController = navController
            )
        }

        composable<Dest.TenantSettingsScreen> {
            TenantSettingsScreen()
        }

    }
}
