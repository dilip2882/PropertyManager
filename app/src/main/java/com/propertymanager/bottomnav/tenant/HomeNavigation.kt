/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.propertymanager.bottomnav.tenant

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.propertymanager.navigation.Dest
import kotlinx.serialization.Serializable
import propertymanager.feature.tenant.presentation.MaintenanceListScreen

fun NavController.navigateToHome
        (navOptions: NavOptions) = navigate(route = Dest.MaintenanceListScreen, navOptions)

fun NavGraphBuilder.homeSection(navController: NavController) {
    navigation<Dest.TenantScreen>(startDestination = Dest.MaintenanceListScreen) {
        composable<Dest.MaintenanceListScreen>() {
            MaintenanceListScreen(
                onNavigateToMaintenanceRequest = {
                    navController.navigate(Dest.MaintenanceRequestScreen)
                }
            )
        }
    }
}
