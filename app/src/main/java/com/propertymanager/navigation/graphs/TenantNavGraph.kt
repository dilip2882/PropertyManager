package com.propertymanager.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.propertymanager.bottomnav.tenant.TenantBottomNavItem
import com.propertymanager.bottomnav.tenant.TenantScreen
import com.propertymanager.domain.model.Category
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import propertymanager.feature.tenant.home.MaintenanceListScreen
import propertymanager.feature.tenant.home.MaintenanceRequestScreen
import propertymanager.feature.tenant.home.MaintenanceCategoriesScreen
import propertymanager.feature.tenant.home.MaintenanceDetailsScreen
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
                },
                onNavigateToDetails = { requestId ->
                    navController.navigate(Dest.MaintenanceDetailsScreen(requestId))
                }
            )
        }


        composable<Dest.MaintenanceDetailsScreen> {
            val args = it.toRoute<Dest.MaintenanceDetailsScreen>()
            MaintenanceDetailsScreen(
                requestId = args.requestId,
                onNavigateUp = { navController.navigateUp() }
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
                    navController.navigate(TenantBottomNavItem.Home.route) {
                        popUpTo(Dest.MaintenanceCategoriesScreen) { inclusive = true } // Clear the previous screens
                        launchSingleTop = true
                    }
                },
            )
        }


    }
}
