package com.example.coffeeapp

import androidx.compose.material3.*
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NaviBar(navController: NavHostController) {
    val items = listOf(
        NavItem("home", R.drawable.home, "Home"),
        NavItem("card", R.drawable.card, "Card"), // ✅ renamed route for consistency
        NavItem("scan", R.drawable.scanning, "Scan"),
        NavItem("topup", R.drawable.wallet, "Top Up"),
        NavItem("setting", R.drawable.user, "Setting")
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive=false
//                                saveState=true
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
                label = { Text(item.label) }
            )
        }
    }
}

data class NavItem(val route: String, val icon: Int, val label: String)
