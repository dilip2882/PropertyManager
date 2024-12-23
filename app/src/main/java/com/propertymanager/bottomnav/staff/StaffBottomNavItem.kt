package com.propertymanager.bottomnav.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.propertymanager.navigation.Dest
import propertymanager.presentation.theme.PropertyManagerIcons

enum class StaffBottomNavItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val destination: Dest,
) {
    HOME(
        selectedIcon = PropertyManagerIcons.HomeBorder,
        unselectedIcon = PropertyManagerIcons.Home,
        label = "Home",
        destination = Dest.StaffHomeScreen,
    ),
    SETTINGS(
        selectedIcon = PropertyManagerIcons.SettingsBorder,
        unselectedIcon = PropertyManagerIcons.Settings,
        label = "Settings",
        destination = Dest.StaffSettingsScreen,
    );

    companion object {
        fun fromDestination(destination: Dest): StaffBottomNavItem? {
            return entries.find { it.destination == destination }
        }
    }
}

@Composable
fun StaffNavBar(
    navController: NavHostController,
    onNavigate: (Dest) -> Unit,
) {
    val currentBackStackEntry = navController
        .currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)
        .value

    val currentDestination = currentBackStackEntry?.destination?.route?.let { route ->
        when (route) {
            Dest.StaffHomeScreen::class.qualifiedName -> Dest.StaffHomeScreen
            Dest.StaffSettingsScreen::class.qualifiedName -> Dest.StaffSettingsScreen
            else -> null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StaffBottomNavItem.entries.forEach { item ->
                val selected = currentDestination == item.destination

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable { onNavigate(item.destination) },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(if (selected) 0.1f else 1f)
                                .size(50.dp)
                                .background(
                                    color = if (selected) Color(0xFFE7EF9F) else Color.Transparent,
                                    shape = RoundedCornerShape(20.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = if (selected) Modifier.size(50.dp) else Modifier.size(24.dp),
                                tint = if (selected) Color.Black else Color.White,
                            )
                        }

                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}
