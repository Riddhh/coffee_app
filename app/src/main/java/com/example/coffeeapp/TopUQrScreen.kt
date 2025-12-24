package com.example.coffeeapp.micro

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

@Composable
fun TopUpQrScreen(navController: NavHostController) {

    val txId = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("txId") ?: ""

    val userId = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("userId") ?: ""

    // ✅ Status UI
    var statusText by remember { mutableStateOf("Processing top up…") }
    var statusColor by remember { mutableStateOf(Color.Gray) }
    var checking by remember { mutableStateOf(true) }

    // ✅ stop polling on back / leaving screen
    var stopPolling by remember { mutableStateOf(false) }

    // ✅ prevent calling auto-accept repeatedly
    var autoAcceptTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(txId, userId) {
        if (txId.isBlank() || userId.isBlank()) {
            statusText = "Missing transaction info"
            statusColor = Color.Red
            checking = false
            return@LaunchedEffect
        }

        checking = true
        while (!stopPolling) {
            try {
                val res = NetworkClient.paymentApi.verifyTopUp(
                    VerifyReceiptRequestDto(
                        userId = userId,
                        transactionId = txId
                    )
                )

                if (!res.valid) {
                    statusText = "Rejected: ${res.reason}"
                    statusColor = Color.Red
                    checking = false
                    break
                }

                if (res.consumed == true) {
                    statusText = "✅ Payment accepted"
                    statusColor = Color(0xFF2E7D32)
                    checking = false

                    delay(1000)
                    stopPolling = true
                    navController.popBackStack()
                    break
                } else {
                    // ✅ Option B: client triggers accept ONCE
                    if (!autoAcceptTriggered) {
                        autoAcceptTriggered = true
                        statusText = "Confirming payment…"
                        statusColor = Color.Gray

                        NetworkClient.paymentApi.autoAcceptTopUp(
                            AutoAcceptRequestDto(
                                userId = userId,
                                transactionId = txId
                            )
                        )
                    } else {
                        statusText = "Waiting for confirmation…"
                        statusColor = Color.Gray
                    }
                }

            } catch (e: Exception) {
                if (e is CancellationException) return@LaunchedEffect

                statusText = "Processing…"
                statusColor = Color.Gray
            }

            delay(1500)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Top up in progress", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(10.dp))

        Text(
            "Transaction ID:\n$txId",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (checking) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
            }
            Text(statusText, color = statusColor)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                stopPolling = true
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
