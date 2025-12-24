package com.example.coffeeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun Cat(navController: NavHostController) {
    val typee = listOf(
        "Hot" to R.drawable.hott,
        "Cold" to R.drawable.coldd,
        "Frappe" to R.drawable.frappee,
        "Cake" to R.drawable.cakee,
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(typee) { types ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8EFE6) // ☕ cream background
                ),


                modifier = Modifier
                    .width(88.dp)
                    .height(135.dp)
                    .clickable {
                        when (types.first) {
                            "Hot" -> navController.navigate("hot")
                            "Cold" -> navController.navigate("cold")
                            "Frappe" -> navController.navigate("frappe")
                            "Cake" -> navController.navigate("cake")
                        }
                    },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = types.second),
                        contentDescription = types.first,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(95.dp),   // ✅ fits inside card
                        contentScale = ContentScale.Fit
                    )

//                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = types.first,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}