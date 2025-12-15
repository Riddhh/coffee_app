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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.coffeeapp.blockchain.RealmBlockchainRepository
import com.example.coffeeapp.blockchain.BlockRealm
import com.example.coffeeapp.blockchain.shareJson
import com.example.coffeeapp.blockchain.copyToClipboard
import com.example.coffeeapp.blockchain.shareJson
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val blocks by RealmBlockchainRepository
        .blocksFlow
        .collectAsStateWithLifecycle(initialValue = emptyList())   // ✅ lifecycle-aware

    var verify by remember { mutableStateOf<Boolean?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    // Show newest first, and keep genesis first if you like—here we fully reverse:
    val ordered = remember(blocks) { blocks.sortedByDescending { it.index } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { verify = RealmBlockchainRepository.isValid() }) {
                        Text("Verify")
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Export JSON") },
                                onClick = {
                                    showMenu = false
                                    val json = Gson().toJson(blocks)
                                    shareJson(context, "coffee_chain.json", json)
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            verify?.let {
                val label = if (it) "Chain is VALID ✅" else "Chain is INVALID ❌"
                ElevatedAssistChip(
                    label = { Text(label) },
                    onClick = { verify = null },
                    modifier = Modifier.padding(12.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (ordered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("No transactions yet") }
                    }
                } else {
                    // ✅ stable keys
                    items(
                        items = ordered,
                        key = { it.index }
                    ) { block ->
                        val brokenLink = remember(ordered) {
                            // Simple link check: previousHash must equal previous block's hash when indices are consecutive
                            val prev = ordered.find { it.index == block.index - 1 }
                            // For genesis (index 0) we don’t flag
                            if (block.index == 0) false
                            else prev?.hash?.takeIf { it.isNotBlank() } != block.previousHash
                        }
                        BlockCard(
                            block = block,
                            brokenLink = brokenLink,
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
    onCopyHash: () -> Unit
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
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            /* ---------- HEADER ---------- */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Block #${block.index}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (brokenLink) "Invalid" else "Valid"
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (brokenLink)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }

            Text(
                text = "Hash: ${block.hash.take(12)}…",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (block.index != 0) {
                Text(
                    text = "Prev: ${block.previousHash.take(12)}…",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider(Modifier.padding(vertical = 6.dp))

            /* ---------- CONTENT ---------- */
            if (order != null) {
                Text(
                    text = "Order #${order.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = fmt.format(Date(order.timestamp)),
                    style = MaterialTheme.typography.bodySmall
                )

                if (order.itemsSummary.isNotBlank()) {
                    Text(
                        text = order.itemsSummary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "Total: $${"%.2f".format(order.total)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Genesis Block",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            /* ---------- ACTION ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCopyHash) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Copy Hash")
                }
            }
        }
    }
}

