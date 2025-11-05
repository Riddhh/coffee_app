package com.example.coffeeapp

import android.R.attr.type
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.example.coffeeapp.ui.theme.CoffeeAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.coffeeapp.blockchain.RealmBlockchainRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
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
        }
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            CoffeeAppTheme {
                val navController = rememberNavController1()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { NaviBar(navController) }) { innerPadding ->
                    // Content goes here
                    NavHost(
                        navController = navController,
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

                            OrderConfirmationScreen(navController = navController, orderItems = orderItems)
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

                    }
                }
            }
        }
    }


    @Composable
    fun Head(navController: NavHostController) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.cafe),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )
            Spacer(
                modifier = Modifier.width(150.dp)
            )
            IconButton(onClick = {
                navController.navigate("notification")
            }) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )
            }
            IconButton(onClick = {
                navController.navigate("cart")
            }) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )
            }
        }

    }
    @Composable
    fun showBalance() {
        val balance = remember { mutableStateOf(0.0) }

        LaunchedEffect(Unit) {
            balance.value = RealmBlockchainRepository.getBalance()
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .height(90.dp)
            ,

            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6A4E39)) // coffee brown
        ) {
            Row(
                Modifier.fillMaxSize().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Your Balance",
                        color = Color(0xFFEED9C4), // latte cream
                        fontSize = 14.sp
                    )

                    Text(
                        text = "$${"%.2f".format(balance.value)}",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.wallets), // create if not exist
                    contentDescription = "Wallet",
                    tint = Color(0xFFEED9C4),
                    modifier = Modifier.size(40.dp)
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

    @Composable
    fun NaviBar(navController: androidx.navigation.NavHostController) {
        var selectedItem by remember { mutableStateOf(0) }
        NavigationBar(
        ) {

            NavigationBarItem(
                selected = selectedItem == 0,
                onClick = {
                    selectedItem = 0
                    navController.navigate("home") {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true
                    }
                },

                icon = {
                    Icon(
                        painterResource(id = R.drawable.home),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = { Text("Home") },

                )
            NavigationBarItem(
                selected = selectedItem == 1,
                onClick = {
                    selectedItem = 1
                    navController.navigate("card") {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.card),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = { Text("Card") },

                )
            NavigationBarItem(
                selected = selectedItem == 2,
                onClick = {
                    selectedItem = 2
                    navController.navigate("scan") {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.scanning),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = { Text("Scan") },

                )
            NavigationBarItem(
                selected = selectedItem == 3,
                onClick = {
                    selectedItem = 3
                    navController.navigate("topup") {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.wallet),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = { Text("Top Up") },
            )
            NavigationBarItem(
                selected = selectedItem == 4,
                onClick = {
                    selectedItem = 4
                    navController.navigate("setting") {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.user),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = { Text("Setting") },
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        CoffeeAppTheme {
//        Head()
        }
    }
}
