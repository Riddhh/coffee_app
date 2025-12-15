package com.example.coffeeapp.micro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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

    val payload = remember(txId, userId) {
        """{"transactionId":"$txId","userId":"$userId"}"""
    }

    val qrBitmap = remember(payload) { generateQrBitmap(payload, 700) }

    // ✅ Status UI
    var statusText by remember { mutableStateOf("Waiting for seller verification…") }
    var statusColor by remember { mutableStateOf(Color.Gray) }
    var checking by remember { mutableStateOf(true) }

    // ✅ stop polling on back / leaving screen
    var stopPolling by remember { mutableStateOf(false) }

    // ✅ Poll backend for status (read-only)
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
                    statusText = "✅ Payment accepted (consumed)"
                    statusColor = Color(0xFF2E7D32) // green
                    checking = false
                    break
                } else {
                    statusText = "Waiting for seller to accept…"
                    statusColor = Color.Gray
                }

            } catch (e: Exception) {
                // ✅ Most common when leaving screen: ignore so no red flash
                if (e is CancellationException) return@LaunchedEffect

                // ✅ Don't flash red for temporary issues; keep waiting
                statusText = "Waiting for seller to accept…"
                statusColor = Color.Gray
                // (Optional: you can log e.message if you want)
            }

            delay(2000) // check every 2 seconds
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Show QR to seller", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier.size(280.dp)
        )

        Spacer(Modifier.height(16.dp))
        Text("Transaction ID:", style = MaterialTheme.typography.titleMedium)
        Text(txId, textAlign = TextAlign.Center)

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (checking) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
            }
            Text(statusText, color = statusColor)
        }

        Spacer(Modifier.height(16.dp))

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
