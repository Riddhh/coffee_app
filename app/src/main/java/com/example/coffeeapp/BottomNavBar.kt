package com.example.coffeeapp

import androidx.compose.material3.*
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NaviBar(navController: NavHostController) {

    // ✅ ONLY CHANGE: use correct home route string (with fromLogin param)
    val items = listOf(
        NavItem("home?fromLogin=false", R.drawable.home, "Home"),
        NavItem("card", R.drawable.card, "Card"),
        NavItem("scan", R.drawable.scanning, "Scan"),
        NavItem("topup", R.drawable.wallet, "Top Up"),
        NavItem("setting", R.drawable.user, "Setting")
    )

    NavigationBar(
        containerColor = Color(0xFFEDE2D6),
        tonalElevation = 5.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->

            // ✅ ONLY CHANGE: handle home selection because its route includes query params
            val isSelected = if (item.label == "Home") {
                currentRoute?.startsWith("home") == true
            } else {
                currentRoute == item.route
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // ✅ ONLY CHANGE: avoid duplicate navigation for Home when already in any "home..."
                    val alreadyOnTarget = if (item.label == "Home") {
                        currentRoute?.startsWith("home") == true
                    } else {
                        currentRoute == item.route
                    }

                    if (!alreadyOnTarget) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(28.dp)
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF805D37),
                    unselectedIconColor = Color(0xFF512F16),
                    selectedTextColor = Color(0xFF805D37),
                    unselectedTextColor = Color(0xFF512F16),
                    indicatorColor = Color(0xFFD8C6B4)
                )
            )
        }
    }
}

data class NavItem(val route: String, val icon: Int, val label: String)
