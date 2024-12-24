package com.propertymanager.bottomnav.tenant

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import propertymanager.presentation.theme.PropertyManagerIcons

sealed class TenantBottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
) {
    data object Home : TenantBottomNavItem(
        route = "tenant_home",
        selectedIcon = PropertyManagerIcons.HomeBorder,
        unselectedIcon = PropertyManagerIcons.Home,
        label = "Home",
    )

    data object Settings : TenantBottomNavItem(
        route = "tenant_profile",
        selectedIcon = PropertyManagerIcons.PersonBorder,
        unselectedIcon = PropertyManagerIcons.Person,
        label = "Profile",
    )

    companion object {
        fun getAllItems() = listOf(Home, Settings)
    }
}

@Composable
fun TenantNavBar(
    currentDestination: String?,
    onNavigate: (String) -> Unit,
) {
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
            TenantBottomNavItem.getAllItems().forEach { item ->
                val selected = currentDestination == item.route

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable { onNavigate(item.route) },
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
                                imageVector = item.selectedIcon,
                                contentDescription = item.label,
                                modifier = if (selected) Modifier.size(50.dp) else Modifier.size(24.dp),
                                tint = if (selected) Color.Black else Color.White,
                            )
                        }

                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            color = if (selected) Color.White else Color.White,
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

