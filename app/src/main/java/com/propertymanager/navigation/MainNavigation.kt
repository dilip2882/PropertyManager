package com.propertymanager.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.propertymanager.navigation.graphs.authNavGraph
import com.propertymanager.navigation.graphs.homeNavGraph
import com.propertymanager.navigation.graphs.landlordNavGraph
import com.propertymanager.navigation.graphs.staffNavGraph
import com.propertymanager.navigation.graphs.tenantNavGraph
import propertymanager.feature.auth.presentation.AuthViewModel
import propertymanager.feature.auth.presentation.OtpScreen
import propertymanager.feature.auth.presentation.PhoneScreen
import propertymanager.feature.landlord.presentation.LandlordScreen
import propertymanager.feature.staff.presentation.StaffScreen
import propertymanager.feature.tenant.presentation.MaintenanceListScreen
import propertymanager.feature.tenant.presentation.MaintenanceRequestScreen
import propertymanager.feature.tenant.presentation.TenantScreen
import propertymanager.feature.tenant.presentation.TenantSettingsScreen
import propertymanager.presentation.home.HomeScreen
import propertymanager.presentation.onboarding.OnboardingFormScreen
import propertymanager.presentation.onboarding.OnboardingViewModel

@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = SubGraph.Auth){
        authNavGraph(navController)
        homeNavGraph(navController)

        // Role-based
        tenantNavGraph(navController)
        landlordNavGraph(navController)
        staffNavGraph(navController)
    }
}
