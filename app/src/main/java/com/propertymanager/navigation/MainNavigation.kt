package com.propertymanager.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.propertymanager.navigation.graphs.authNavGraph
import com.propertymanager.navigation.graphs.homeNavGraph
import com.propertymanager.navigation.graphs.landlord.landlordNavGraph
import com.propertymanager.navigation.graphs.staff.staffNavGraph
import com.propertymanager.navigation.graphs.tenant.tenantNavGraph

@Composable
fun MainNavigation(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = SubGraph.Auth){

        authNavGraph(navController)
        homeNavGraph(navController)

        // Role-based
        tenantNavGraph(navController)
        landlordNavGraph(navController)
        staffNavGraph(navController)
    }
}
