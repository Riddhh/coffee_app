package com.example.coffeeapp.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.coffeeapp.auth.Http

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(navController: NavHostController) {

    val ctx = LocalContext.current
    val api = remember { Http.api(ctx) } // ✅ YOUR Http

    val vm: PersonalInfoViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PersonalInfoViewModel(api) as T
            }
        }
    )

    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Information") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            when {
                state.loading -> {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(10.dp))
                    Text("Loading your account…")
                }

                state.error != null -> {
                    Text(
                        "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                state.profile != null -> {
                    val p = state.profile!!

                    InfoRow("Name", p.name ?: "Not set")
                    InfoRow("Email", p.email)

                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge)
        Divider(Modifier.padding(top = 8.dp))
    }
}
