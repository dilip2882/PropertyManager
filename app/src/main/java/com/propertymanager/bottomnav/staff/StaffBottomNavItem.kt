package com.propertymanager.bottomnav.staff

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import propertymanager.presentation.theme.PropertyManagerIcons

sealed class StaffBottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
) {

    data object Home : StaffBottomNavItem(
        route = "staff_home",
        selectedIcon = PropertyManagerIcons.Home,
        unselectedIcon = PropertyManagerIcons.HomeBorder,
        label = "Home",
    )

    data object Settings : StaffBottomNavItem(
        route = "staff_settings",
        selectedIcon = PropertyManagerIcons.Settings,
        unselectedIcon = PropertyManagerIcons.SettingsBorder,
        label = "Settings",
    )

    companion object {
        fun getAllItems() = listOf(Home, Settings)
    }

}


@Composable
fun StaffNavBar(
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
            StaffBottomNavItem.getAllItems().forEach { item ->
                val selected = currentDestination == item.route

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable { onNavigate(item.route) },
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        val iconSize by animateDpAsState(
                            targetValue = if (selected) 30.dp else 30.dp,
                            animationSpec = tween(durationMillis = 300),
                            label = "Icon Size"
                        )

                        val iconShape by animateFloatAsState(
                            targetValue = if (selected) 35f else 20f,
                            animationSpec = tween(durationMillis = 300),
                            label = "Icon Shape"
                        )

                        val backgroundWidth by animateDpAsState(
                            targetValue = if (selected) 60.dp else 40.dp,
                            animationSpec = tween(durationMillis = 300),
                            label = "Background Width"
                        )

                        val rotation by animateFloatAsState(
                            targetValue = if (selected && item is StaffBottomNavItem.Settings) 90f else 0f,
                            animationSpec = tween(durationMillis = 300),
                            label = "Rotation"
                        )

                        Box(
                            modifier = Modifier
                                .width(backgroundWidth)
                                .size(iconSize)
                                .background(
                                    color = if (selected) Color(0xFFE7EF9F) else Color.Transparent,
                                    shape = RoundedCornerShape(iconShape),
                                )
                                .graphicsLayer(rotationZ = rotation),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(iconSize),
                                tint = if (selected) Color.Black else Color.White,
                            )
                        }

                        val labelColor by animateColorAsState(
                            targetValue = if (selected) Color(0xFFE7EF9F) else Color.White,
                            animationSpec = tween(durationMillis = 300),
                            label = "Label Color"
                        )

                        val labelFontSize by animateFloatAsState(
                            targetValue = if (selected) 14f else 12f,
                            animationSpec = tween(durationMillis = 300),
                            label = "Label Font Size"
                        )

                        Text(
                            text = item.label,
                            fontSize = labelFontSize.sp,
                            color = labelColor,
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

