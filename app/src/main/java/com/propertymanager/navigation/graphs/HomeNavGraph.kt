package com.propertymanager.navigation.graphs

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import propertymanager.presentation.home.HomeScreen

fun NavGraphBuilder.homeNavGraph(navController: NavController) {

    navigation<SubGraph.Home>(startDestination = Dest.HomeScreen) {
        // Home Screen
        composable<Dest.HomeScreen>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(700),
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(700),
                )
            },
        ) {
            HomeScreen(
                onNavigateToTenantScreen = {
                    navController.navigate(Dest.TenantScreen) {
                        launchSingleTop = true
                    }
                },
                onNavigateToLandlordScreen = {
                    navController.navigate(Dest.LandlordScreen)
                },
                onNavigateToManagerScreen = {
                    navController.navigate(Dest.StaffScreen)
                },
            )
        }
    }
}
