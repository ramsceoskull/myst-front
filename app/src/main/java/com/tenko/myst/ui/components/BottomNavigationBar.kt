package com.tenko.myst.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tenko.myst.ui.components.BottomNavItem
import com.tenko.myst.ui.theme.PompAndPower
import com.tenko.myst.ui.theme.Tekhelet
import com.tenko.myst.ui.theme.White

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Calendar,
        BottomNavItem.Chat,
        BottomNavItem.Profile
    )

    Surface(
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            modifier = Modifier.shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp
                ),
                ambientColor = Color.Black,
                spotColor = Color.Black,
//                clip = false
            ),
            containerColor = White,
//            tonalElevation = 30.dp
        ) {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = item.label,
                            tint = if (currentRoute == item.route) Tekhelet else PompAndPower.copy(alpha = 0.7f),
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            color = if (currentRoute == item.route) Tekhelet else PompAndPower.copy(alpha = 0.7f),
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}