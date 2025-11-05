package com.example.coffeeapp

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
                title = { Text("Transaction History (Blockchain)") },
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
            // Long-press anywhere on the card to copy hash
            .combinedClickable(
                onClick = {},
                onLongClick = onCopyHash
            ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Block #${block.index} • ${block.hash.take(10)}…",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (brokenLink) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Link broken") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            labelColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    )
                } else {
                    AssistChip(onClick = {}, label = { Text("OK") })
                }
            }

            Text("Prev: ${block.previousHash.take(10)}…", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(6.dp))

            if (order != null) {
                Text("Order #${order.id} • ${fmt.format(Date(order.timestamp))}")
                if (order.itemsSummary.isNotBlank()) {
                    Text(order.itemsSummary, style = MaterialTheme.typography.bodySmall)
                }
                Text("Total: $${"%.2f".format(order.total)}", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text("Genesis block")
            }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                FilledTonalButton(onClick = onCopyHash) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy hash")
                    Spacer(Modifier.width(8.dp))
                    Text("Copy hash")
                }
            }
        }
    }
}
