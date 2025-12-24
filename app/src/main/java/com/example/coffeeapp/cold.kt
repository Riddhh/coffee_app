package com.example.coffeeapp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColdScreen(navController: NavHostController) {

    var coldDrinks by remember { mutableStateOf<List<Coffee>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val bg = Color(0xFFF5ECE4)
    val textBrown = Color(0xFF381D12)
    val subtxt = Color(0xFFA68C7E)
    val btn = Color(0xFF5C3321)

    LaunchedEffect(Unit) {
        try {
            val allCoffees = CoffeeRepository.fetchCoffees()
            coldDrinks = allCoffees.filter { it.category.equals("cold", ignoreCase = true) }
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

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
                    // Center title nicely even with nav icon
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Cold Drinks",
//                            modifier = Modifier.align(Alignment.Center),
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                .padding(10.dp)
                .background(bg)
        ) {
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
                    LazyColumn(modifier = Modifier.padding(10.dp).background(bg)) {
                        items(coldDrinks) { cafe ->
                            ListItem(
                                colors = ListItemDefaults.colors(containerColor = bg),
                                headlineContent = { Text(cafe.name, fontSize = 20.sp,fontWeight = FontWeight.Bold, color = textBrown,) },
                                supportingContent = {
                                    Text("$${cafe.price}", fontSize = 16.sp, color = subtxt,)
                                },
                                leadingContent = {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(cafe.image)
                                            .listener(
                                                onError = { _, result ->
                                                    Log.e("COIL_IMG", "Failed: ${cafe.image}", result.throwable)
                                                }
                                            )
                                            .build(),
                                        contentDescription = cafe.name,
                                        modifier = Modifier.size(69.dp),
                                        contentScale = ContentScale.Crop
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
                            HorizontalDivider(thickness = 1.2.dp, color = Color(0xFFC6B4AA))
                        }
                    }
                }
            }
        }
    }
}
