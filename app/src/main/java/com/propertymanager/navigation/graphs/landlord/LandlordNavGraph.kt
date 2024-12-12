package com.propertymanager.navigation.graphs.landlord

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.SubGraph
import propertymanager.feature.landlord.presentation.LandlordScreen

fun NavGraphBuilder.landlordNavGraph(navController: NavController) {

    navigation<SubGraph.Landlord>(startDestination = Dest.LandlordScreen) {
        composable<Dest.LandlordScreen> {
            LandlordScreen()
        }
    }
}
