package com.example.coffeeapp

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.coffeeapp.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Head(navController: NavHostController) {
    val cartCount by remember { derivedStateOf { cartManager.items.sumOf { it.quantity } } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.cafe),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = { navController.navigate("notification") }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFF5C3321), modifier = Modifier.size(32.dp),)
        }

        // 🛒 Cart icon with animated badge
        Box(
            modifier = Modifier
                .padding(end = 8.dp)
                .size(42.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(onClick = { navController.navigate("cart") }) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart",tint = Color(0xFF5C3321), modifier = Modifier.size(35.dp))
            }

            // fully qualified animation call avoids RowScope confusion
            androidx.compose.animation.AnimatedVisibility(
                visible = cartCount > 0,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Surface(
                    color = Color(0xFF6F4E37), // coffee brown
                    shape = CircleShape,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(if (cartCount > 9) 20.dp else 18.dp)
                        .offset(x = (-5).dp, y = 0.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (cartCount > 99) "99+" else cartCount.toString(),
                            color = Color(0xFFFFF8E1), // cream text
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
