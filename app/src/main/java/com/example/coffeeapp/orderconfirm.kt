// OrderConfirmationScreen.kt
package com.example.coffeeapp

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderConfirmationScreen(
    navController: NavHostController
) {
    val currency = remember { NumberFormat.getCurrencyInstance() }

    val orderItems = cartManager.items

    val total by remember {
        derivedStateOf { orderItems.sumOf { it.price * it.quantity } }
    }

    val balance by RealmBlockchainRepository.balanceFlow.collectAsState(initial = 0.0)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }

    // ✅ Coffee theme colors
    val bg = Color(0xFFF5ECE4)
    val surface = Color(0xFFFBF8F5)
    val brown = Color(0xFF5A3A26)
    val textBrown = Color(0xFF381D12)
    val muted = Color(0xFF8C735A)
    val cardd = Color(0xFFFFF7EE)
    val border_color = Color(0xFFE3CDB8)

    if (isProcessing) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            title = { Text("Processing…", color = textBrown) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = brown)
                    Spacer(Modifier.width(12.dp))
                    Text("Recording order and updating balance", color = textBrown)
                }
            },
            containerColor = surface
        )
    }

    Scaffold(
        containerColor = bg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        "Order Confirmation",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bg,
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
                .padding(16.dp)
        ) {

            // ✅ Keep the Balance row (just recolor)
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
                        border = BorderStroke(2.2.dp, border_color ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = cardd // ✅ was gray, now coffee surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                Text(
                                    item.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = textBrown
                                )
                                Text(
                                    "Size: ${item.size}  x${item.quantity}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = muted
                                )
                            }
                            Text(
                                currency.format(item.price * item.quantity),
                                style = MaterialTheme.typography.titleMedium,
                                color = textBrown
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
                Text("Total", style = MaterialTheme.typography.titleMedium, color = textBrown)
                Text(currency.format(total), style = MaterialTheme.typography.titleMedium, color = textBrown)
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
                            val result = withContext(Dispatchers.Default) {
                                RealmBlockchainRepository.purchase(order)
                            }

                            when (result) {
                                is RealmBlockchainRepository.PurchaseResult.Success -> {
                                    val ctx = navController.context
                                    val token = TokenStore.get(ctx)

                                    if (token.isNullOrBlank()) {
                                        isProcessing = false
                                        snackbarHostState.showSnackbar("Missing token. Please login again.")
                                        return@launch
                                    }

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
                                        snackbarHostState.showSnackbar("Server order failed: ${e.message}")
                                    }

                                    isProcessing = false
                                    cartManager.clearCart()

                                    snackbarHostState.showSnackbar("Payment successful")

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
                                                    cartManager.clearCart()
                                                    snackbarHostState.showSnackbar("Payment successful")
                                                    navController.navigate("home") { launchSingleTop = true }
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
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = brown,
                    contentColor = Color.White,
                    disabledContainerColor = brown.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    if (canConfirm) "Confirm & Finish" else "Insufficient Balance",
                    fontSize = 18.sp
                )
            }
        }
    }
}
