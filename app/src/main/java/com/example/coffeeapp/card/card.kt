package com.example.coffeeapp.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.coffeeapp.CardViewModel
import com.example.coffeeapp.R
import kotlin.collections.plus

@Composable
fun CardScreen(navController: NavHostController) {
    val vm: CardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val cards = vm.cards.collectAsState().value

    var showAdd by remember { mutableStateOf(false) }
    var showRemove by remember { mutableStateOf(false) }
    var removeId by remember { mutableStateOf<String?>(null) }

    // UI same idea as you already have
    Column(Modifier.fillMaxSize().padding(15.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Payment Methods", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

        Spacer(Modifier.height(12.dp))

        if (cards.isEmpty()) {
            // keep your empty state
            Text("No saved cards yet. Add one for demo mode.")
        } else {
            cards.forEach { c ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF5C3321)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("${c.brand}  •••• ${c.last4}", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("${c.holder} • Exp ${c.expMonth.toString().padStart(2,'0')}/${(c.expYear % 100).toString().padStart(2,'0')}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                        }
                        IconButton(onClick = {
                            removeId = c.id
                            showRemove = true
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { showAdd = true },
            modifier = Modifier.align(Alignment.CenterHorizontally).width(200.dp).height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) { Text("Add new card") }
    }

    if (showAdd) {
        DemoAddCardDialog(
            onDismiss = { showAdd = false },
            onSave = { number, holder, mm, yy ->
                vm.addDemoCard(number, holder, mm, yy)
                showAdd = false
            }
        )
    }

    if (showRemove && removeId != null) {
        AlertDialog(
            onDismissRequest = { showRemove = false },
            title = { Text("Remove card") },
            text = { Text("Remove this card from your device?") },
            confirmButton = {
                Button(onClick = {
                    vm.removeCard(removeId!!)
                    showRemove = false
                    removeId = null
                }) { Text("Remove") }
            },
            dismissButton = { TextButton(onClick = { showRemove = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun DemoAddCardDialog(
    onDismiss: () -> Unit,
    onSave: (number: String, holder: String, expMonth: Int, expYear: Int) -> Unit
) {
    var number by remember { mutableStateOf("") }
    var holder by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") } // MM/YY
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add card (Demo)") },
        text = {
            Column {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it.filter(Char::isDigit).take(19); error = null },
                    label = { Text("Card number") },
                    singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = holder,
                    onValueChange = { holder = it; error = null },
                    label = { Text("Cardholder name") },
                    singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = expiry,
                    onValueChange = { expiry = formatExpiry(it); error = null },
                    label = { Text("Expiry (MM/YY)") },
                    singleLine = true
                )
                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error!!, color = Color(0xFFB00020))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val digits = number.filter(Char::isDigit)
                val (mm, yy) = parseExpiry(expiry)

                when {
                    digits.length < 13 -> error = "Card number too short."
                    holder.isBlank() -> error = "Enter cardholder name."
                    mm == null || yy == null -> error = "Expiry must be MM/YY."
                    mm !in 1..12 -> error = "Month must be 01–12."
                    else -> {
                        val yearFull = 2000 + yy
                        onSave(digits, holder.trim(), mm, yearFull)
                    }
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private fun formatExpiry(input: String): String {
    val digits = input.filter(Char::isDigit).take(4)
    return if (digits.length <= 2) digits else "${digits.substring(0, 2)}/${digits.substring(2)}"
}

private fun parseExpiry(exp: String): Pair<Int?, Int?> {
    val parts = exp.split("/")
    if (parts.size != 2) return null to null
    return parts[0].toIntOrNull() to parts[1].toIntOrNull()
}

