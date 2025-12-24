@file:OptIn(ExperimentalFoundationApi::class)
package com.example.coffeeapp

import androidx.compose.foundation.ExperimentalFoundationApi
import kotlin.OptIn
import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.coffeeapp.blockchain.RealmBlockchainRepository
import com.example.coffeeapp.blockchain.BlockRealm
import com.example.coffeeapp.blockchain.shareJson
import com.example.coffeeapp.blockchain.copyToClipboard
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val blocks by RealmBlockchainRepository
        .blocksFlow
        .collectAsStateWithLifecycle(initialValue = emptyList())

    // 🎨 color palette (ONLY colors)
    val bg = Color(0xFFF5ECE4)
    val coffeeBrown = Color(0xFF381D12)
    val softBrown = Color(0xFFA68C7E)
    val cardBg = Color(0xFFFFFBF8)
    val indicator = Color(0xFF5C3321)

    var verify by remember { mutableStateOf<Boolean?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    val ordered = remember(blocks) { blocks.sortedByDescending { it.index } }

    Scaffold(
        containerColor = bg, // 🎨 background
        topBar = {
            TopAppBar(
                title = { Text("Transaction History", color = coffeeBrown) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = coffeeBrown
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { verify = RealmBlockchainRepository.isValid() }) {
                        Text("Verify", color = coffeeBrown)
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = coffeeBrown
                            )
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Export JSON", color = coffeeBrown) },
                                onClick = {
                                    showMenu = false
                                    val json = Gson().toJson(blocks)
                                    shareJson(context, "coffee_chain.json", json)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bg
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            verify?.let {
                val label = if (it) "Chain is VALID ✅" else "Chain is INVALID ❌"
                ElevatedAssistChip(
                    label = { Text(label, color = coffeeBrown) },
                    onClick = { verify = null },
                    modifier = Modifier.padding(12.dp),
                    colors = AssistChipDefaults.elevatedAssistChipColors(
                        containerColor = Color(0xFFEDE2D6)
                    )
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (ordered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No transactions yet", color = softBrown)
                        }
                    }
                } else {
                    items(
                        items = ordered,
                        key = { it.index }
                    ) { block ->
                        val brokenLink = remember(ordered) {
                            val prev = ordered.find { it.index == block.index - 1 }
                            if (block.index == 0) false
                            else prev?.hash?.takeIf { it.isNotBlank() } != block.previousHash
                        }
                        BlockCard(
                            block = block,
                            brokenLink = brokenLink,
                            cardBg = cardBg,          // 🎨
                            coffeeBrown = coffeeBrown,// 🎨
                            softBrown = softBrown,    // 🎨
                            indicator = indicator,    // 🎨
                            onCopyHash = {
                                copyToClipboard(context, "Block Hash", block.hash)
                                Toast.makeText(context, "Hash copied", Toast.LENGTH_SHORT).show()
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BlockCard(
    block: BlockRealm,
    brokenLink: Boolean,
    onCopyHash: () -> Unit,
    // 🎨 colors passed in (ONLY colors)
    cardBg: Color,
    coffeeBrown: Color,
    softBrown: Color,
    indicator: Color
) {
    val fmt = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val order = block.data

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onCopyHash
            ),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = cardBg) // 🎨 card bg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Block #${block.index}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    color = coffeeBrown // 🎨
                )

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (brokenLink) "Invalid" else "Valid",
                            color = coffeeBrown // 🎨
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (brokenLink)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            Color(0xFFEDE2D6) // 🎨 soft coffee chip
                    )
                )
            }

            Text(
                text = "Hash: ${block.hash.take(12)}…",
                style = MaterialTheme.typography.bodySmall,
                color = softBrown // 🎨
            )

            if (block.index != 0) {
                Text(
                    text = "Prev: ${block.previousHash.take(12)}…",
                    style = MaterialTheme.typography.bodySmall,
                    color = softBrown // 🎨
                )
            }

            Divider(
                Modifier.padding(vertical = 6.dp),
                color = Color(0xFFD8C6B4) // 🎨
            )

            if (order != null) {
                Text(
                    text = "Order #${order.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = coffeeBrown // 🎨
                )

                Text(
                    text = fmt.format(Date(order.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = softBrown // 🎨
                )

                if (order.itemsSummary.isNotBlank()) {
                    Text(
                        text = order.itemsSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = softBrown // 🎨
                    )
                }

                Text(
                    text = "Total: $${"%.2f".format(order.total)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = indicator // 🎨 coffee accent
                )
            } else {
                Text(
                    text = "Genesis Block",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = coffeeBrown // 🎨
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCopyHash) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = coffeeBrown // 🎨
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Copy Hash", color = coffeeBrown) // 🎨
                }
            }
        }
    }
}
