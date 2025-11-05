// OrderConfirmationScreen.kt
package com.example.coffeeapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.coffeeapp.blockchain.RealmBlockchainRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

// TODO: rename to CartItem in your model
@Composable
fun OrderConfirmationScreen(
    navController: NavHostController,
    orderItems: List<cartItem> // CartItem
) {
    val currency = remember { NumberFormat.getCurrencyInstance() }
    val total by remember(orderItems) {
        mutableStateOf(orderItems.sumOf { it.price * it.quantity })
    }

    // Snackbar + coroutine scope for async mining + feedback
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }

    // Optional: simple progress dialog during mining
    if (isProcessing) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            title = { Text("Recording order…") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Mining block on device, please wait")
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
                    Icon(
                        Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                Text(
                    "Order Confirmation",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(
                    items = orderItems,
                    key = { item -> item.name + item.size } // ideally use a real id
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
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(
                                    "Size: ${item.size} x${item.quantity}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                currency.format(item.price * item.quantity),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(currency.format(total), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            val canConfirm = orderItems.isNotEmpty()

            Button(
                enabled = canConfirm,
                onClick = {
                    val itemsSummary = orderItems.joinToString(", ") { "${it.name} (${it.size}) x${it.quantity}" }
                    val order = com.example.coffeeapp.model.Order(
                        id = java.util.UUID.randomUUID().toString().take(8),
                        timestamp = System.currentTimeMillis(),
                        itemsSummary = itemsSummary,
                        total = total
                    )

                    scope.launch {
                        isProcessing = true
                        var minedHash: String? = null

                        try {
                            // mine + save in background; return only a plain String
                            minedHash = withContext(Dispatchers.Default) {
                                val block = RealmBlockchainRepository.addOrder(order)
                                block.hash // <-- only return the hash string
                            }
                        } catch (e: IllegalStateException) {
                            // genesis missing: create then retry
                            minedHash = withContext(Dispatchers.Default) {
                                RealmBlockchainRepository.ensureGenesis()
                                val block = RealmBlockchainRepository.addOrder(order)
                                block.hash
                            }
                        } catch (e: Throwable) {
                            isProcessing = false
                            // Visible feedback but won’t crash if host is gone
                            runCatching {
                                snackbarHostState.showSnackbar("Failed to record order: ${e.message}")
                            }
                            return@launch
                        }

                        isProcessing = false

                        // Optional: cartManager.clearCart()

                        // Feedback (guard in case the host got disposed)
                        runCatching {
                            snackbarHostState.showSnackbar("Order recorded • Block ${minedHash?.take(8) ?: "??"}")
                        }

                        // Navigate safely
                        runCatching {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    if (canConfirm) "Confirm & Finish" else "Nothing to confirm",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
