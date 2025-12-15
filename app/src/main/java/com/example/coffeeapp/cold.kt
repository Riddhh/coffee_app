package com.example.coffeeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun ColdScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var coldDrinks by remember { mutableStateOf<List<Coffee>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val allCoffees = CoffeeRepository.fetchCoffees() // your Ktor fetch
                coldDrinks = allCoffees.filter { it.category.lowercase() == "cold" }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    Column(modifier = Modifier.padding(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
            }
            Text("Cold Drinks", fontSize = 22.sp, color = Color.Black)
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Text("Error: $errorMessage", color = Color.Red)
            }
            else -> {
                LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
                    items(coldDrinks) { cafe ->
                        ListItem(
                            headlineContent = {
                                Text(cafe.name, fontSize = 20.sp)
                            },
                            supportingContent = {
                                Text(
                                    "$${cafe.price}",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            },
                            leadingContent = {
//                                Image(
//                                    painter = rememberAsyncImagePainter(cafe.image),
//                                    contentDescription = null,
//                                    contentScale = ContentScale.Crop,
//                                    modifier = Modifier.size(69.dp)
//                                )
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = cafe.image,
                                        placeholder = painterResource(R.drawable.cold),
                                        error = painterResource(R.drawable.cold)
                                    ),
                                    contentDescription = cafe.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(69.dp)
                                )
                            },
                            trailingContent = {
                                Button(onClick = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set("coffee", cafe)
                                    navController.navigate("detail")
                                }) {
                                    Text("Shop Now")
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
