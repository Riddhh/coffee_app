// OrderConfirmationScreen.kt
package com.example.coffeeapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.coffeeapp.blockchain.RealmBlockchainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.UUID
import com.example.coffeeapp.auth.TokenStore
import com.example.coffeeapp.order.CreateOrderRequest
import com.example.coffeeapp.order.OrderHttp
import com.example.coffeeapp.order.OrderItemDto


@Composable
fun OrderConfirmationScreen(
    navController: NavHostController
) {
    val currency = remember { NumberFormat.getCurrencyInstance() }

    // 🔴 Use the LIVE cart
    val orderItems = cartManager.items

    // Total derived from live cart; recomputes automatically
    val total by remember {
        derivedStateOf { orderItems.sumOf { it.price * it.quantity } }
    }

    // Live balance
    val balance by RealmBlockchainRepository.balanceFlow.collectAsState(initial = 0.0)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }

    if (isProcessing) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            title = { Text("Processing…") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Recording order and updating balance")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                }
                Spacer(Modifier.width(4.dp))
                Text("Order Confirmation", fontSize = 26.sp, style = MaterialTheme.typography.titleLarge)
            }

            // Balance row
            Spacer(Modifier.height(8.dp))
            Text(
                "Wallet Balance: ${currency.format(balance)}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF2E7D32),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(
                    items = orderItems,
                    key = { item -> item.name + item.size }
                ) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            AsyncImage(
                                model = item.img,
                                contentDescription = item.name,
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(end = 12.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Size: ${item.size}  x${item.quantity}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                currency.format(item.price * item.quantity),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", style = MaterialTheme.typography.titleMedium)
                Text(currency.format(total), style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(8.dp))
            if (balance < total && orderItems.isNotEmpty()) {
                Text(
                    "Insufficient balance. You need ${currency.format(total - balance)} more.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(20.dp))

            val canConfirm = orderItems.isNotEmpty() && balance >= total

            Button(
                enabled = canConfirm,
                onClick = {
                    val itemsSummary = orderItems.joinToString(", ") {
                        "${it.name} (${it.size}) x${it.quantity}"
                    }
                    val order = com.example.coffeeapp.model.Order(
                        id = UUID.randomUUID().toString().take(8),
                        timestamp = System.currentTimeMillis(),
                        itemsSummary = itemsSummary,
                        total = total
                    )

                    scope.launch {
                        isProcessing = true
                        try {
                            // Attempt the purchase (mines block + deducts balance)
                            val result = withContext(Dispatchers.Default) {
                                RealmBlockchainRepository.purchase(order)
                            }

                            when (result) {
                                is RealmBlockchainRepository.PurchaseResult.Success -> {
                                    // 🔹 STEP A: Get JWT token
                                    val ctx = navController.context
                                    val token = TokenStore.get(ctx)

                                    if (token.isNullOrBlank()) {
                                        isProcessing = false
                                        snackbarHostState.showSnackbar("Missing token. Please login again.")
                                        return@launch
                                    }

                                    // 🔹 STEP B: Send order to Order microservice (3002)
                                    val req = CreateOrderRequest(
                                        items = orderItems.map {
                                            OrderItemDto(
                                                name = it.name,
                                                price = it.price,
                                                quantity = it.quantity,
                                                imageUrl = it.img
                                            )
                                        },
                                        total = total
                                    )

                                    runCatching {
                                        OrderHttp.api.createOrder(
                                            authorization = "Bearer $token",
                                            req = req
                                        )
                                    }.onFailure { e ->
                                        snackbarHostState.showSnackbar(
                                            "Server order failed: ${e.message}"
                                        )
                                    }

                                    // 🔹 STEP C: Keep your existing logic
                                    isProcessing = false
                                    cartManager.clearCart()

                                    val paidAmount = total
                                    snackbarHostState.showSnackbar(
                                        "Payment successful • ${currency.format(paidAmount)} deducted"
                                    )

                                    navController.navigate("home") {
                                        launchSingleTop = true
                                    }
                                }

                                is RealmBlockchainRepository.PurchaseResult.InsufficientBalance -> {
                                    isProcessing = false
                                    snackbarHostState.showSnackbar(
                                        "Not enough balance. Need ${currency.format(result.needed)} more."
                                    )
                                }

                                is RealmBlockchainRepository.PurchaseResult.Error -> {
                                    // If genesis missing, create and retry once
                                    if (result.cause is IllegalStateException) {
                                        runCatching {
                                            withContext(Dispatchers.Default) {
                                                RealmBlockchainRepository.ensureGenesis()
                                                RealmBlockchainRepository.purchase(order)
                                            }
                                        }.onSuccess { retry ->
                                            when (retry) {
                                                is RealmBlockchainRepository.PurchaseResult.Success -> {
                                                    isProcessing = false
                                                    val paidAmount = total
                                                    cartManager.clearCart() // ✅ also clear after retry success
                                                    snackbarHostState.showSnackbar(
                                                        "Payment successful"
                                                    )
                                                    navController.navigate("home") {
                                                        launchSingleTop = true
                                                    }
                                                }
                                                is RealmBlockchainRepository.PurchaseResult.InsufficientBalance -> {
                                                    isProcessing = false
                                                    snackbarHostState.showSnackbar(
                                                        "Not enough balance. Need ${currency.format(retry.needed)} more."
                                                    )
                                                }
                                                is RealmBlockchainRepository.PurchaseResult.Error -> {
                                                    isProcessing = false
                                                    snackbarHostState.showSnackbar("Purchase failed: ${retry.message}")
                                                }
                                            }
                                        }.onFailure {
                                            isProcessing = false
                                            snackbarHostState.showSnackbar("Purchase failed: ${it.message}")
                                        }
                                    } else {
                                        isProcessing = false
                                        snackbarHostState.showSnackbar("Purchase failed: ${result.message}")
                                    }
                                }
                            }
                        } catch (e: Throwable) {
                            isProcessing = false
                            snackbarHostState.showSnackbar("Unexpected error: ${e.message}")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    if (canConfirm) "Confirm & Finish" else "Insufficient Balance",
                    fontSize = 18.sp
                )
            }
        }
    }
}
