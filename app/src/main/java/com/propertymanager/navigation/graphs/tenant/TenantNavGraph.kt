package com.propertymanager.navigation.graphs.tenant

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.propertymanager.bottomnav.TenantScreen
import com.propertymanager.bottomnav.tenant.homeSection
import com.propertymanager.bottomnav.tenant.settingsSection
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import propertymanager.feature.tenant.presentation.MaintenanceListScreen
import propertymanager.feature.tenant.presentation.MaintenanceRequestScreen
import propertymanager.feature.tenant.presentation.TenantSettingsScreen

fun NavGraphBuilder.tenantNavGraph(
    navController: NavHostController,
    navOptions: NavOptions
) {

    navigation<SubGraph.Tenant>(startDestination = Dest.TenantScreen) {

        homeSection(navController = navController)
        settingsSection(navController = navController)

        composable<Dest.TenantScreen> {
            TenantScreen(
                navController = navController,
                topLevelNavOptions = navOptions
   /*            onNavigateToMaintenanceList = {
                    navController.navigate(Dest.MaintenanceListScreen)
                }*/

            )
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
                navController = navController
            )
        }

        composable<Dest.TenantSettingsScreen> {
            TenantSettingsScreen()
        }
    }
}
