package com.example.coffeeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Recommend(navController: NavHostController) {

    val bg = Color(0xFFF5ECE4)        // main background
    val itemBg = Color(0xFFFBF8F5)    // warm surface (not pure white)
    val brown = Color(0xFF5A3A26)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
    ) {

        Surface(color = itemBg) {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = bg),
                headlineContent = {
                    Text("Iced Latte", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                supportingContent = {
                    Text("3.21$", fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color(0xFFA4A9AD))
                },
                leadingContent = {
                    Image(
                        painter = painterResource(R.drawable.latte),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                trailingContent = {
                    Button(
                        onClick = { navController.navigate("cold") },
                        colors = ButtonDefaults.buttonColors(containerColor = brown)
                    ) {
                        Text("View More", color = Color.White)
                    }
                }
            )
        }

        Surface(color = itemBg) {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = bg),
                headlineContent = {
                    Text("Matcha Latte", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                supportingContent = {
                    Text("3.45$", fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color(0xFFA4A9AD))
                },
                leadingContent = {
                    Image(
                        painter = painterResource(R.drawable.icedmatchalatte),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                trailingContent = {
                    Button(
                        onClick = { navController.navigate("cold") },
                        colors = ButtonDefaults.buttonColors(containerColor = brown)
                    ) {
                        Text("View More", color = Color.White)
                    }
                }
            )
        }

        Surface(color = itemBg) {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = bg),
                headlineContent = {
                    Text("Matcha Tiramisu", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                supportingContent = {
                    Text("3.95$", fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color(0xFFA4A9AD))
                },
                leadingContent = {
                    Image(
                        painter = painterResource(R.drawable.matchatira),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                trailingContent = {
                    Button(
                        onClick = { navController.navigate("cake") },
                        colors = ButtonDefaults.buttonColors(containerColor = brown)
                    ) {
                        Text("View More", color = Color.White)
                    }
                }
            )
        }
    }
}
