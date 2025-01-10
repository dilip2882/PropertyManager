package com.propertymanager.navigation.graphs

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import com.propertymanager.ui.PropertyManagerApp
import propertymanager.presentation.home.HomeScreen

import androidx.activity.compose.BackHandler

fun NavGraphBuilder.homeNavGraph(navController: NavController) {

    navigation<SubGraph.Home>(startDestination = Dest.HomeScreen) {
        composable<Dest.HomeScreen> {
            BackHandler {
                navController.popBackStack()
            }

            HomeScreen(
                onNavigateToTenantScreen = {
                    navController.navigate(Dest.TenantScreen) {
                        popUpTo(Dest.HomeScreen) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToLandlordScreen = {
                    navController.navigate(Dest.LandlordScreen) {
                        popUpTo(Dest.HomeScreen) { inclusive = true }
                    }
                },
                onNavigateToManagerScreen = {
                    navController.navigate(Dest.StaffScreen) {
                        popUpTo(Dest.HomeScreen) { inclusive = true }
                    }
                },
            )
        }
    }
}
