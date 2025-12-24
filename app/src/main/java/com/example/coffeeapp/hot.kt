package com.example.coffeeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var hotDrinks by remember { mutableStateOf<List<Coffee>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val bg = Color(0xFFF5ECE4)
    val textBrown = Color(0xFF381D12)
    val subtxt = Color(0xFFA68C7E)
    val btn = Color(0xFF5C3321)

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val allCoffees = CoffeeRepository.fetchCoffees()
                hotDrinks = allCoffees.filter { it.category.lowercase() == "hot" }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = bg,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text(
                        text = "Hot Drinks",
                        fontWeight = FontWeight.Bold
                    )
                },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = bg,              // ✅ top bar background
                titleContentColor = textBrown     // ✅ title color
            )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(
                    10.dp
                )
                .background(bg)
        ) {

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    Text("Error: $errorMessage", color = Color.Red)
                }

                else -> {
                    LazyColumn(modifier = Modifier.padding(15.dp).background(bg)) {
                        items(hotDrinks) { cafe ->
                            ListItem(
                                colors = ListItemDefaults.colors(containerColor = bg),
                                headlineContent = {
                                    Text(
                                        cafe.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = textBrown
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        "$${cafe.price}",
                                        fontSize = 16.sp,
                                        color = subtxt,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingContent = {
                                    Image(
                                        painter = rememberAsyncImagePainter(cafe.image),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(69.dp)
                                    )
                                },
                                trailingContent = {
                                    Button(onClick = {
                                        navController.currentBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("coffee", cafe)
                                        navController.navigate("detail")
                                    },
                                            colors = ButtonDefaults.buttonColors(
                                            containerColor = btn ,          // ✅ button background
                                    ),
                                    ) {
                                        Text("Shop Now")
                                    }
                                }
                            )
                            HorizontalDivider(thickness = 1.2.dp,color = Color(0xFFC6B4AA))
                        }
                    }
                }
            }
        }
    }
}