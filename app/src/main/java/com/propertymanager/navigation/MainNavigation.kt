package com.propertymanager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.propertymanager.common.preferences.temp.AppPreferences
import com.propertymanager.navigation.graphs.authNavGraph
import com.propertymanager.navigation.graphs.landlordNavGraph
import com.propertymanager.navigation.graphs.staffNavGraph
import com.propertymanager.navigation.graphs.tenantNavGraph

@Composable
fun MainNavigation(
    navController: NavHostController,
    initialDestination: Dest
) {
    val startDestination = when (initialDestination) {
        is Dest.TenantScreen -> SubGraph.Tenant
        is Dest.LandlordScreen -> SubGraph.Landlord
        is Dest.StaffScreen -> SubGraph.Staff
        else -> SubGraph.Auth
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authNavGraph(navController)
        tenantNavGraph(navController)
        staffNavGraph(navController)
        landlordNavGraph(navController)
    }
}
