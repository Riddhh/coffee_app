// CartPage.kt
package com.example.coffeeapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

@Composable
fun CartPage(navController: NavHostController) {
    val cartItems = cartManager.items

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
            }
            Text("Cart Page", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        LazyColumn {
            items(cartItems) { item ->
                ListItem(
                    headlineContent = {
                        Text("${item.name} (${item.size})", fontWeight = FontWeight.Bold)
                    },
                    supportingContent = {
                        Text("$${"%.2f".format(item.price)}")
                    },
                    leadingContent = {
                        Image(
                            painter = rememberAsyncImagePainter(item.img),
                            contentDescription = item.name,
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                    },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (item.quantity > 1) item.quantity--
                            }) {
                                Icon(Icons.Default.RemoveCircle, contentDescription = null)
                            }
                            Text("${item.quantity}", fontSize = 18.sp)
                            IconButton(onClick = { item.quantity++ }) {
                                Icon(Icons.Default.AddCircle, contentDescription = null)
                            }
                            IconButton(onClick = { cartManager.removeItem(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("$${"%.2f".format(cartManager.totalPrice())}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Button(
            onClick = {
                if (cartItems.isEmpty()) {
                    Toast.makeText(navController.context, "Cart is empty!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Convert to Parcelable list
                val parcelableItems = cartItems.map {
                    cartItem(it.name, it.img, it.price, it.size, it.quantity)
                }

                // Pass list to OrderConfirmation screen
                navController.currentBackStackEntry?.arguments?.putParcelableArrayList(
                    "orderItems",
                    ArrayList(parcelableItems)
                )
                navController.navigate("order_confirmation")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(55.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
