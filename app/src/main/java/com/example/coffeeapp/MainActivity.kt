package com.example.coffeeapp


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.example.coffeeapp.ui.theme.CoffeeAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.coffeeapp.blockchain.RealmBlockchainRepository

import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController as rememberNavController1

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            com.example.coffeeapp.blockchain.RealmBlockchainRepository.ensureGenesis()
//            RealmBlockchainRepository.ensureGenesis()
        }
        enableEdgeToEdge()

        setContent {
            CoffeeAppTheme {
                val navController = rememberNavController1()
                // NEW: auth
                val authVm: com.example.coffeeapp.auth.AuthViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel()
                val authState = authVm.state.collectAsState().value

                val bottomBarRoutes = setOf(
                    "home","card","scan","topup","setting",
                    "transactionHistory","cart","notification","hot","cold","frappe","cake"
                )
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

// Auto-redirect to home if already logged in
                LaunchedEffect(authState.isAuthed) {
                    if (authState.isAuthed && currentRoute == "login") {
                        navController.navigate("home") { popUpTo("login") { inclusive = true } }
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute in bottomBarRoutes) {
                            NaviBar(navController)
                        }

                    }) { innerPadding ->
                    // Content goes here
                    NavHost(
                        navController = navController,
//                        startDestination = "login",
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            Login(navController)
                        }
                        composable("aboutus") {
                            AboutUs(navController)
                        }
                        composable("register") {
                            Register(navController)
                        }
                        composable("forget") {
                            ForgetPw(navController)
                        }
                        composable("home") {

                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Head(navController)
                                showBalance()
                                Spacer(Modifier.height(12.dp))
                                Body()
                                Spacer(Modifier.height(20.dp))
                                InputField()
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    "Menu",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 23.sp,
                                    modifier = Modifier.padding(15.dp)
                                )
//                                Spacer(Modifier.height(15.dp))
                                Cat(navController)
                                Text(
                                    text = "Recommended",
                                    modifier = Modifier.padding(18.dp),
                                    style = TextStyle(
                                        fontSize = 22.sp, fontWeight = FontWeight.Bold
                                    )
                                )
                                Recommend()


                            }
                        }
                        composable("transactionHistory") {
                            TransactionHistoryScreen(navController)
                        }
                        composable(
                            route = "order_confirmation",
                        ) {
                            // Convert from Parcelable list if needed
                            val orderItems = cartManager.items.map {
                                cartItem(
                                    name = it.name,
                                    img = it.img,
                                    price = it.price,
                                    size = it.size,
                                    quantity = it.quantity
                                )
                            }

                            OrderConfirmationScreen(navController = navController)
                        }
                        composable("detail") { backStackEntry ->
                            val coffee = navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<Coffee>("coffee")  // ✅ get the Coffee object

                            if (coffee != null) {
                                DetailScreen(navController = navController, coffee = coffee)
                            }
                        }
                        composable("cart") {
                            CartPage(navController)
                        }
                        composable("notification") {
                            Notification(navController)
                        }

                        composable("hot") {
                            HotScreen(navController)
                        }
                        composable("cold") {
                            ColdScreen(navController)
                        }
                        composable("frappe") {
                            FrappeScreen(navController)
                        }
                        composable("cake") {
                            CakeScreen(navController)
                        }
                        composable("card") {
                            var showDialog by remember { mutableStateOf(false) }
                            var cards by remember { mutableStateOf(listOf<Map<String, String>>()) }

                            // For remove confirmation
                            var showRemoveDialog by remember { mutableStateOf(false) }
                            var cardToRemoveIndex by remember { mutableStateOf(-1) }

                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(modifier = Modifier.padding(15.dp)) {

                                    // --- Header ---
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "My Card",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Button(
                                            onClick = { showDialog = true },
                                            modifier = Modifier.size(35.dp),
                                            contentPadding = PaddingValues(0.dp),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Add")
                                        }
                                    }

                                    Spacer(Modifier.height(20.dp))

                                    // --- Card List / Empty State ---
                                    if (cards.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(300.dp)
                                                .background(color = Color(0xFFD9D9D9)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.cardd),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(80.dp)
                                                )
                                                Spacer(Modifier.height(10.dp))
                                                Text(
                                                    text = "Your card(s) will be shown here if you add more",
                                                    textAlign = TextAlign.Center,
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }
                                    } else {
                                        Column {
                                            cards.forEachIndexed { index, card ->
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 6.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = Color(
                                                            0xFF4C64FF
                                                        )
                                                    )
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(16.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column {
                                                            Text(
                                                                "Card Number: ${card["number"]}",
                                                                color = Color.White
                                                            )
                                                            Text(
                                                                "Holder: ${card["holder"]}",
                                                                color = Color.White
                                                            )
                                                            Text(
                                                                "Expiry: ${card["expiry"]}",
                                                                color = Color.White
                                                            )
                                                        }
                                                        IconButton(onClick = {
                                                            cardToRemoveIndex = index
                                                            showRemoveDialog = true
                                                        }) {
                                                            Icon(
                                                                Icons.Default.Delete,
                                                                contentDescription = "Remove Card",
                                                                tint = Color.White
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(30.dp))

                                    // --- Add More Button ---
                                    Button(
                                        onClick = { showDialog = true },
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .width(180.dp)
                                            .height(55.dp)
                                    ) {
                                        Text(
                                            "Add More Card",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // --- Add Card Dialog ---
                                if (showDialog) {
                                    var number by remember { mutableStateOf("") }
                                    var holder by remember { mutableStateOf("") }
                                    var expiry by remember { mutableStateOf("") }

                                    AlertDialog(
                                        onDismissRequest = { showDialog = false },
                                        title = { Text("Add New Card") },
                                        text = {
                                            Column {
                                                OutlinedTextField(
                                                    value = number,
                                                    onValueChange = { number = it },
                                                    label = { Text("Card Number") })
                                                OutlinedTextField(
                                                    value = holder,
                                                    onValueChange = { holder = it },
                                                    label = { Text("Card Holder Name") })
                                                OutlinedTextField(
                                                    value = expiry,
                                                    onValueChange = { expiry = it },
                                                    label = { Text("Expiry Date") })
                                            }
                                        },
                                        confirmButton = {
                                            Button(onClick = {
                                                if (number.isNotBlank() && holder.isNotBlank() && expiry.isNotBlank()) {
                                                    cards = cards + mapOf(
                                                        "number" to number,
                                                        "holder" to holder,
                                                        "expiry" to expiry
                                                    )
                                                    showDialog = false
                                                }
                                            }) {
                                                Text("Add")
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showDialog = false }) {
                                                Text("Cancel")
                                            }
                                        })
                                }

                                // --- Remove Card Confirmation Dialog ---
                                if (showRemoveDialog && cardToRemoveIndex >= 0) {
                                    AlertDialog(
                                        onDismissRequest = { showRemoveDialog = false },
                                        title = { Text("Remove Card") },
                                        text = { Text("Are you sure you want to remove this card?") },
                                        confirmButton = {
                                            Button(onClick = {
                                                cards =
                                                    cards.filterIndexed { index, _ -> index != cardToRemoveIndex }
                                                showRemoveDialog = false
                                            }) {
                                                Text("Yes")
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showRemoveDialog = false }) {
                                                Text("No")
                                            }
                                        })
                                }
                            }
                        }

                        composable("scan") {
                            val context = LocalContext.current
                            val scanLauncher = rememberLauncherForActivityResult(
                                contract = ScanContract(), onResult = { result: ScanIntentResult ->
                                    if (result.contents == null) {
                                        Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT)
                                            .show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Scanned: ${result.contents}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                })
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Tap below to scan a QR Code",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(20.dp))

                                Button(onClick = {
                                    scanLauncher.launch(ScanOptions().apply {
                                        setDesiredBarcodeFormats(ScanOptions.QR_CODE) // Only QR
                                        setPrompt("Scan a QR Code")
                                        setBeepEnabled(true)
                                        setOrientationLocked(true)
                                    })
                                }) {
                                    Text("Scan QR")
                                }
                            }

                        }
                        composable("topup") {
                            TopUp(navController)
                        }
                        composable("setting") {
                            SettingPage(navController)
                        }
                        composable("head") {
                            Head(navController)
                        }
                        composable("navbar") {
                            NaviBar(navController)
                        }

                    }
                }
            }
        }
    }

    @Composable
    fun showBalance() {
        val balanceState = remember { mutableStateOf(0.0) }

        LaunchedEffect(Unit) {
            balanceState.value = RealmBlockchainRepository.getBalance()
        }

        val bal = balanceState.value   // <-- unwrap the state

        Card (
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6A4E39))
        ) {
            Row(
                Modifier.fillMaxSize().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Wallet Balance",
                        color = Color(0xFFEED9C4), // latte cream
                        fontSize = 14.sp
                    )
                    Text(
                        text = "$${"%.2f".format(bal)}",   // <-- use bal, not balanceState
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    )

                }
                Icon(
                    painter = painterResource(id = R.drawable.wallet), // replace with your icon
//                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color(0xFFEED9C4),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }


    @Composable
    fun Body() {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "What would you like today?",

            style = TextStyle(
                fontSize = 22.sp, fontWeight = FontWeight.Bold
            )
        )
    }



    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        CoffeeAppTheme {
//        Head()
        }
    }
}
