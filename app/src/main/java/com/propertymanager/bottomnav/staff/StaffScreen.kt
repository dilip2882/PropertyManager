package com.propertymanager.bottomnav.staff

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.propertymanager.navigation.Dest
import com.propertymanager.navigation.graphs.staffNavGraph
import propertymanager.feature.staff.StaffHomeScreen
import propertymanager.feature.staff.settings.StaffSettingsScreen

@Composable
fun StaffScreen() {
    val navController = rememberNavController()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            NavHost(
                navController = navController,
                startDestination = Dest.StaffHomeScreen::class.qualifiedName!!,
            ) {
                composable(Dest.StaffHomeScreen::class.qualifiedName!!) {
                    StaffHomeScreen(
                        staffId = "",
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Dest.StaffSettingsScreen::class.qualifiedName!!) {
                    StaffSettingsScreen()
                }
            }
        }

        StaffNavBar(
            navController = navController,
            onNavigate = { destination ->
                navController.navigate(destination::class.qualifiedName!!) {
                    popUpTo(Dest.StaffHomeScreen::class.qualifiedName!!) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        )
    }
}
