package com.propertymanager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.propertymanager.common.preferences.AppPreferences
import com.propertymanager.navigation.graphs.authNavGraph
import com.propertymanager.navigation.graphs.homeNavGraph
import com.propertymanager.navigation.graphs.landlord.landlordNavGraph
import com.propertymanager.navigation.graphs.staff.staffNavGraph
import com.propertymanager.navigation.graphs.tenant.tenantNavGraph

@Composable
fun MainNavigation(
    navController: NavHostController,
    appPreferences: AppPreferences
) {
    val isLoggedIn = appPreferences.getAuthToken().collectAsState(initial = null).value != null

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) SubGraph.Home else SubGraph.Auth
    ){
        authNavGraph(navController)
        homeNavGraph(navController)

        // Role-based
        tenantNavGraph(navController)
        landlordNavGraph(navController)
        staffNavGraph(navController)
    }
}
