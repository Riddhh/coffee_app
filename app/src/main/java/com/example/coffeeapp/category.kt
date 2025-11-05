package com.example.coffeeapp

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun Cat(navController: NavHostController) {
    val typee = listOf(
        "Hot" to R.drawable.hot,
        "Cold" to R.drawable.cold,
        "Frappe" to R.drawable.frappe,
        "Cake" to R.drawable.cake,


        )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        items(typee) { types ->
            Card(
                modifier = Modifier
                    .width(85.dp)
                    .height(140.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            )

            {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            when (types.first) {
                                "Hot" -> navController.navigate("hot")
                                "Cold" -> navController.navigate("cold")
                                "Frappe" -> navController.navigate("frappe")
                                "Cake" -> navController.navigate("cake")
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = types.second),
                        contentDescription = types.first,
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp),
                        contentScale = ContentScale.Fit

                    )

                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = types.first, style = MaterialTheme.typography.bodyLarge)
                }
            }

        }
    }
}