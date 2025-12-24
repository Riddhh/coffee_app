package com.example.coffeeapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPage(navController: NavHostController) {
    val cartItems = cartManager.items

    val total by remember {
        derivedStateOf { cartItems.sumOf { it.price * it.quantity } }
    }

    val bg = Color(0xFFFBF3EA)
    val textt = Color(0xFF5A3A26)
    val textBrown = Color(0xFF381D12)
    val subtxt = Color(0xFFA68C7E)
    val btn = Color(0xFF4e2b1e)
    val ibtn = Color(0xFF5A3A26)

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                title = {
                    Text("Cart Page", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bg,              // ✅ top bar background
                    titleContentColor = textt     // ✅ title color
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp)
        ) {

            LazyColumn(modifier = Modifier.background(bg)) {
                items(cartItems, key = { it.name + it.size }) { item ->
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = bg),
                        headlineContent = {
                            Text("${item.name} (${item.size})", fontWeight = FontWeight.Bold,color = textBrown)
                        },
                        supportingContent = {
                            Text("$${"%.2f".format(item.price)}",color = subtxt,)
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
                                    if (item.quantity > 1)
                                        cartManager.updateQuantity(item, item.quantity - 1)
                                }) {
                                    Icon(Icons.Default.RemoveCircle, contentDescription = null, tint = ibtn)
                                }
                                Text("${item.quantity}", fontSize = 18.sp, color = textt)
                                IconButton(

                                    onClick = {
                                    cartManager.updateQuantity(item, item.quantity + 1)
                                }) {
                                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = ibtn)
                                }
                                IconButton(onClick = { cartManager.removeItem(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = btn)
                                }
                            }
                        }
                    )
                    HorizontalDivider(thickness = 1.5.dp, color = Color(0xFFD1BFAE))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = textBrown)
                Text(
                    "$${"%.2f".format(total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = textt
                )
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = btn ,          // ✅ button background
                ),
                onClick = {
                    if (cartItems.isEmpty()) {
                        Toast.makeText(navController.context, "Cart is empty!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val parcelableItems = cartItems.map {
                        cartItem(it.name, it.img, it.price, it.size, it.quantity)
                    }

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
                Text("Checkout", fontSize = 19.sp, fontWeight = FontWeight.Bold,)
            }
        }
    }
}
