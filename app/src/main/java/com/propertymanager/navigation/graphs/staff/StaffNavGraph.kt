package com.propertymanager.navigation.graphs.staff

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import propertymanager.feature.staff.presentation.StaffScreen

fun NavGraphBuilder.staffNavGraph(navController: NavController) {

    navigation<SubGraph.Staff>(startDestination = Dest.StaffScreen) {
        composable<Dest.StaffScreen> {
            StaffScreen(
                staffId = "",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
