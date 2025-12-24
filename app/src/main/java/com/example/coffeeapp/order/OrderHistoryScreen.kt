package com.example.coffeeapp.order

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.coffeeapp.auth.TokenStore
import com.example.coffeeapp.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavHostController) {
    val ctx = navController.context

    // 🎨 color palette (ONLY colors)
    val bg = Color(0xFFF5ECE4)
    val coffeeBrown = Color(0xFF381D12)
    val softBrown = Color(0xFFA68C7E)
    val cardBg = Color(0xFFEBE0DA)

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var orders by remember { mutableStateOf<List<ServerOrder>>(emptyList()) }

    LaunchedEffect(Unit) {
        val token = TokenStore.get(ctx)
        if (token.isNullOrBlank()) {
            error = "Please login again (missing token)."
            loading = false
            return@LaunchedEffect
        }

        runCatching {
            val api = com.example.coffeeapp.micro.NetworkClient.orderApi {
                TokenStore.get(ctx)
            }
            api.getOrders()
        }.onSuccess {
            orders = it
        }.onFailure {
            error = it.message
        }
        loading = false
    }

    Scaffold(
        containerColor = bg, // 🎨 background
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = coffeeBrown
                        )
                    }
                },
                title = {
                    Text(
                        "Order History",
                        fontSize = 22.sp,
                        color = coffeeBrown
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bg
                )
            )
        }
    ) { paddingValues ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp)
        ) {

            when {
                loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = coffeeBrown)
                }

                error != null -> Text(
                    "Error: $error",
                    color = MaterialTheme.colorScheme.error
                )

                orders.isEmpty() -> Text(
                    "No orders yet.",
                    color = softBrown
                )

                else -> LazyColumn {
                    items(orders, key = { it._id ?: "" }) { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardBg
                            )
                        ) {
                            Column(Modifier.padding(12.dp)) {

                                Text(
                                    "Total: $${String.format("%.2f", order.total ?: 0.0)}",
                                    color = coffeeBrown
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "Date: ${DateUtils.format(order.date)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = softBrown
                                )

                                Text(
                                    text = "Status: ${order.status ?: "PAID"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when (order.status) {
                                        "PAID" -> Color(0xFF2E7D32)
                                        "PREPARING" -> Color(0xFFF9A825)
                                        "COMPLETED" -> Color(0xFF1565C0)
                                        else -> Color.Gray
                                    }
                                )

                                Spacer(Modifier.height(8.dp))

                                order.items.forEach { itx ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(
                                            model = itx.imageUrl,
                                            contentDescription = itx.name,
                                            modifier = Modifier.size(44.dp)
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            "${itx.name} x${itx.quantity ?: 1}",
                                            color = coffeeBrown
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
