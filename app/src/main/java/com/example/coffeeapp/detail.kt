package com.example.coffeeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
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
fun DetailScreen(
    navController: NavHostController,
    coffee: Coffee
) {
    var selectedSize by remember { mutableStateOf("S") }
    var totalPrice by remember { mutableStateOf(coffee.price) }
    var showDialog by remember { mutableStateOf(false) }

    val sizePrices = mapOf(
        "S" to coffee.price,
        "M" to coffee.price + 0.5,
        "L" to coffee.price + 1.0
    )

    val btn = Color(0xFF4e2b1e)
    val textBrown = Color(0xFF381D12)
    val subtxt = Color(0xFFA68C7E)

    Scaffold(
        containerColor = Color(0xFFF5ECE4),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        text = coffee.name,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5ECE4),
                    titleContentColor = textBrown,
                    navigationIconContentColor = textBrown
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(15.dp)
        ) {
            // ✅ removed the old back IconButton (now in TopAppBar)

            Spacer(Modifier.height(15.dp))

            Image(
                painter = rememberAsyncImagePainter(coffee.image),
                contentDescription = coffee.name,
                modifier = Modifier
//                    .width(350.dp)
                    .fillMaxWidth()
                    .height(255.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(coffee.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textBrown)
                Row {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
                    Text("4.5")
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(thickness = 1.5.dp, color = Color(0xFFD1BFAE))
            Spacer(Modifier.height(15.dp))

            Text("Description", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = textBrown)
            Spacer(Modifier.height(10.dp))
            Text(coffee.description, color = subtxt)
            Spacer(Modifier.height(15.dp))

            Text("Size", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = textBrown)
            Spacer(Modifier.height(15.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                listOf("S", "M", "L").forEach { size ->
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(50.dp)
                            .background(
                                color = if (selectedSize == size) Color(0xFF795548) else Color(0xFFD7CCC8),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedSize = size
                                totalPrice = sizePrices[size] ?: coffee.price
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            size,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (selectedSize == size)
                                Color.White
                            else
                                Color(0xFF381D12)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = textBrown)
                Text("$${"%.2f".format(totalPrice)}", color = textBrown, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(35.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = btn
                ),
                onClick = {
                    val newItem = cartItem(
                        name = coffee.name,
                        img = coffee.image,
                        price = totalPrice,
                        size = selectedSize
                    )
                    cartManager.addItem(newItem)
                    showDialog = true
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Add To Cart", fontWeight = FontWeight.Bold, fontSize = 19.sp)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Success") },
            text = { Text("Item added to cart.") },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
