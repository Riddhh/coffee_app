package com.example.coffeeapp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.coffeeapp.blockchain.RealmBlockchainRepository
import com.example.coffeeapp.ui.theme.CoffeeAppTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import com.example.coffeeapp.card.CardScreen
import com.example.coffeeapp.micro.NetworkClient
import com.example.coffeeapp.micro.ProductApi
import com.example.coffeeapp.micro.TopUpQrScreen
import com.example.coffeeapp.order.OrderHistoryScreen
import com.example.coffeeapp.setting.PersonalInfoScreen
import androidx.navigation.compose.rememberNavController as rememberNavController1
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubbleOutline


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            RealmBlockchainRepository.ensureGenesis()
        }

        enableEdgeToEdge()

        setContent {
            CoffeeAppTheme {
                val navController = rememberNavController1()
                var hasShownDrinkPopup by rememberSaveable { mutableStateOf(false) }

                // NEW: auth
                val authVm: com.example.coffeeapp.auth.AuthViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel()
                val authState = authVm.state.collectAsState().value

                val bottomBarRoutes = setOf(
                    "home?fromLogin={fromLogin}", // ✅ match route string
                    "card", "scan", "topup", "setting",
                    "transactionHistory", "cart", "notification",
                    "hot", "cold", "frappe", "cake"
                )

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Auto-redirect to home if already logged in
                LaunchedEffect(authState.isAuthed) {
                    if (authState.isAuthed && currentRoute == "login") {
                        navController.navigate("home?fromLogin=true") {
                            popUpTo("login") { inclusive = true }
//                            launchSingleTop = true
//                            restoreState = true
//                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                        }
                    }
                }
                val appBg = Color(0xFFF5ECE4)
//                val appBg = Color(0xFFF4EAE1)
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = appBg,
                    bottomBar = {
                        if (currentRoute in bottomBarRoutes) {
                            NaviBar(navController)
                        }
                    },
                            // ✅ UPDATED: Ask Me button ONLY on Home screen (does not affect other flows)
                            floatingActionButton = {
                        val currentRoute =
                            navController.currentBackStackEntryAsState().value?.destination?.route

                        // home route is "home?fromLogin=..."
                        val isHome = currentRoute?.startsWith("home") == true

                        if (isHome) {
                            FloatingActionButton(
                                onClick = { navController.navigate("chat") },
                                containerColor = Color(0xFF6A4E39),
                                contentColor = Color.White,
                                shape = RoundedCornerShape(18.dp),
                                elevation = FloatingActionButtonDefaults.elevation(
                                    defaultElevation = 10.dp,
                                    pressedElevation = 16.dp
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.ChatBubbleOutline,
                                        contentDescription = "Chat bot"
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "Ask Me",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login", //login
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                            .background(appBg)
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

                        // ✅ Main home screen with popup + weather
                        composable(
                            route = "home?fromLogin={fromLogin}",
                            arguments = listOf(
                                navArgument("fromLogin") {
                                    type = NavType.BoolType
                                    defaultValue = false
                                }
                            )
                        ) { backStackEntry ->
                            val fromLogin =
                                backStackEntry.arguments?.getBoolean("fromLogin") ?: false
                            HomeScreen(
                                navController,
                                fromLogin = fromLogin,

                            )
                        }

                        composable("transactionHistory") {
                            TransactionHistoryScreen(navController)
                        }
                        composable("language") { LanguageScreen(navController) }

                        composable("order_confirmation") {
                            // convert from cart manager if needed
                            OrderConfirmationScreen(navController = navController)
                        }
                        composable("order_history") {
                            OrderHistoryScreen(navController)
                        }
                        composable("personal_info") {
                            PersonalInfoScreen(navController)
                        }


                        composable("detail") { backStackEntry ->
                            val coffee = navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<Coffee>("coffee")

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
                            CardScreen(navController)
                        }

                        composable("scan") {
                            val context = LocalContext.current
                            val btn = Color(0xFF5C3321)
                            val scanLauncher = rememberLauncherForActivityResult(
                                contract = ScanContract(),
                                onResult = { result: ScanIntentResult ->
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
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(20.dp))

                                Button(onClick = {
                                    scanLauncher.launch(ScanOptions().apply {
                                        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                                        setPrompt("Scan a QR Code")
                                        setBeepEnabled(true)
                                        setOrientationLocked(true)
                                    })
                                },
                                    modifier = Modifier.width(120.dp).height(50.dp),
                                        colors = ButtonDefaults.buttonColors(
                                        containerColor = btn,          // ✅ button background
                                ),
                                ) {
                                    Text("Scan QR", fontSize = 18.sp)
                                }
                            }
                        }

                        composable("topup") {
                            TopUp(navController)
                        }
                        composable("topup_qr") {
                            TopUpQrScreen(navController)
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
                        composable("chat") { ChatScreen(navController) }

                    }
                }
            }
        }
    }
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun WeatherRecommendationBubble(
        tempC: Double,
        isNight: Boolean,
        onGoHot: () -> Unit,
        onGoCold: () -> Unit,
        onGoFrappe: () -> Unit,
        onGoCake: () -> Unit
    ) {
        var visible by remember { mutableStateOf(true) }
        if (!visible) return

        val (message, suggestion) = getCoolWeatherAdvice(tempC, isNight)

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    color = Color(0xFF3B2A1F),
                    modifier = Modifier.clickable {
                        when (suggestion) {
                            "hot" -> onGoHot()
                            "cold" -> onGoCold()
                            "frappe" -> onGoFrappe()
                            "cake" -> onGoCake()
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFFFC107))
                        )

                        Spacer(Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI Weather Pick",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFFD180)
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = message,
                                fontSize = 13.sp,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }

                        IconButton(
                            onClick = { visible = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    android.R.drawable.ic_menu_close_clear_cancel
                                ),
                                contentDescription = "Dismiss",
                                tint = Color(0xFFFFD180),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }


    // ---------------- HOME SCREEN WITH WEATHER + POPUP ----------------

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun HomeScreen(navController: NavHostController, fromLogin: Boolean) {
        val context = LocalContext.current
        var drink by remember { mutableStateOf<DrinkResponse?>(null) }
        var showPopup by remember { mutableStateOf(false) }
        var loading by remember { mutableStateOf(false) }
        var query by rememberSaveable { mutableStateOf("") }
        var results by remember { mutableStateOf<List<Coffee>>(emptyList()) }
        var searching by remember { mutableStateOf(false) }
        var searchError by remember { mutableStateOf<String?>(null) }

        val scope = rememberCoroutineScope()

        // ✅ NEW: popup shown only once (persist for this Home destination)
        var hasShownPopupOnce by rememberSaveable { mutableStateOf(false) }

        // weather state
        var currentTemp by remember { mutableStateOf<Double?>(null) }
        var weatherError by remember { mutableStateOf<String?>(null) }

        // determine day/night
        val isNight = remember {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            hour < 6 || hour >= 18
        }

        // ################### WEATHER FETCH ###################
        LaunchedEffect(Unit) {
            weatherError = null
            currentTemp = null

            try {
                val weather = WeatherApi.service.getWeather(
                    latitude = 11.5564,
                    longitude = 104.9282
                )
                currentTemp = weather.currentWeather.temperature
            } catch (e: Exception) {
                weatherError = e.localizedMessage ?: "Unknown error"
            }
        }

        // ################### POPUP (ONLY AFTER LOGIN + ONLY ONCE) ###################
        LaunchedEffect(fromLogin) {
            if (fromLogin && !hasShownPopupOnce) {
                loading = true
                try {
                    val result = DrinkApi.service.getRandomDrink()
                    drink = result
                } catch (e: Exception) {
                    drink = DrinkResponse(
                        name = "House Latte",
                        description = "Our special latte, perfect to welcome you back.",
                        image = null,
                        imageUrl = null,
                        img = null,
                        price = 3.50
                    )
                    Toast.makeText(
                        context,
                        "Showing sample drink (offline).",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    showPopup = drink != null
                    loading = false
                    hasShownPopupOnce = true   // ✅ IMPORTANT
                }
            }
        }



        // ################### UI ###################
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5ECE4))
        ) {
            val bgBlur = if (showPopup) 12.dp else 0.dp
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .heightIn(min = screenHeight)
                    .verticalScroll(rememberScrollState())
                    .blur(bgBlur)
            ) {
                Head(navController)
                showBalance()
                Spacer(Modifier.height(12.dp))
                Body()

                Spacer(Modifier.height(20.dp))
                InputField(
                    query = query,
                    onQueryChange = {
                        query = it
                        if (it.isBlank()) {
                            results = emptyList()
                            searchError = null
                        }
                    },
                    onSearchClick = {
                        val q = query.trim()
                        if (q.isBlank()) return@InputField

                        scope.launch {
                            searching = true
                            searchError = null
                            try {
                                results = NetworkClient.productApi.searchProducts(q)
                                Toast.makeText(context, "Found ${results.size} items", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                searchError = e.localizedMessage ?: "Search failed"
                                results = emptyList()
                                Toast.makeText(context, "Error: $searchError", Toast.LENGTH_LONG).show()
                            } finally {
                                searching = false
                            }
                        }
                    }
                )
                Spacer(Modifier.height(10.dp))
                if (searching) {
                    Text("Searching...", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }

                searchError?.let {
                    Text(it, color = Color.Red, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }

//                if (query.isNotBlank() && results.isEmpty() && !searching && searchError == null) {
//                    Text("No results for \"$query\"", color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
//                }

                if (results.isNotEmpty()) {
                    results.take(10).forEach { coffee ->
                        SearchResultRow(
                            coffee = coffee,
                            onClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("coffee", coffee)
                                navController.navigate("detail")
                            }
                        )
                    }
                }



                Text(
                    "Menu",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF381D12),
                    fontSize = 23.sp,
                    modifier = Modifier.padding(15.dp)
                )

                // Weather bubble
                when {
                    currentTemp != null -> {
                        WeatherRecommendationBubble(
                            tempC = currentTemp!!,
                            isNight = isNight,
                            onGoHot = { navController.navigate("hot") },
                            onGoCold = { navController.navigate("cold") },
                            onGoFrappe = { navController.navigate("frappe") },
                            onGoCake = { navController.navigate("cake") }
                        )
                    }
                    weatherError != null -> {
                        Text(
                            text = "Weather error: $weatherError",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Cat(navController)

                Text(
                    text = "Signature Food & Drink",
                    color = Color(0xFF381D12),
                    modifier = Modifier.padding(18.dp),
                    style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold)
                )
                Recommend(navController)
            }

            if (showPopup) {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                )
            }

            AnimatedVisibility(
                visible = showPopup && drink != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                DrinkPopup(
                    drink = drink!!,
                    onDismiss = { showPopup = false }
                )
            }

            if (loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }




    @Composable
    fun DrinkPopup(drink: DrinkResponse, onDismiss: () -> Unit) {
        val imageModel: Any = if (drink.photo.isNullOrBlank()) {
            R.drawable.cardd
        } else {
            drink.photo!!
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = drink.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.cardd),
                        error = painterResource(R.drawable.cardd)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = drink.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF6A4E39),
                        textAlign = TextAlign.Center
                    )

                    if (drink.description.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = drink.description,
                            color = Color.Gray,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Price: $${"%.2f".format(drink.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF6A4E39)
                    )

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4E39))
                    ) {
                        Text("Close", color = Color.White)
                    }
                }
            }
        }
    }

    // tempC in °C, isNight from Calendar
    // ✅ SCROLLABLE CHAT SCREEN
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatScreen(navController: NavHostController) {

        data class UiMsg(val fromUser: Boolean, val text: String)

        val scope = rememberCoroutineScope()
        var input by remember { mutableStateOf("") }
        var loading by remember { mutableStateOf(false) }
        val messages = remember { mutableStateListOf<UiMsg>() }

        val listState = rememberLazyListState()

        LaunchedEffect(messages.size, loading) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }

        fun send(text: String) {
            val msg = text.trim()
            if (msg.isBlank()) return

            messages.add(UiMsg(fromUser = true, text = msg))
            input = ""
            loading = true

            scope.launch {
                try {
                    val res = com.example.coffeeapp.ai.ChatHttp.api.chat(
                        com.example.coffeeapp.ai.ChatRequest(message = msg)
                    )
                    messages.add(UiMsg(fromUser = false, text = res.reply))
                } catch (e: Exception) {
                    messages.add(UiMsg(fromUser = false, text = "⚠️ Failed to connect: ${e.message}"))
                } finally {
                    loading = false
                }
            }
        }

        @Composable
        fun Bubble(m: UiMsg) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = if (m.fromUser) Arrangement.End else Arrangement.Start
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (m.fromUser) Color(0xFF6A4E39) else Color.White,
                    tonalElevation = 2.dp,
                    shadowElevation = 3.dp
                ) {
                    Text(
                        text = m.text,
                        modifier = Modifier.padding(12.dp),
                        color = if (m.fromUser) Color.White else Color.Black
                    )
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Coffee Assistant", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6A4E39),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF7F1EA))
            ) {
                if (messages.isEmpty()) {
                    Surface(
                        modifier = Modifier.padding(16.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        tonalElevation = 4.dp,
                        shadowElevation = 6.dp
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text("Hi 👋", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Ask me about drinks, prices, top-up, scan, and app features.",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Try: “top up” or “How much is Iced Latte?”",
                                color = Color(0xFF6A4E39),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    state = listState,
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(messages) { m ->
                        Bubble(m)
                    }
                    if (loading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text("Typing…", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message…") },
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true
                    )

                    Spacer(Modifier.width(10.dp))

                    Button(
                        onClick = { send(input) },
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4E39))
                    ) {
                        Text("Send", color = Color.White)
                    }
                }
            }
        }
    }
            }


    // suggestion: "hot" / "cold" / "frappe" / "cake" / null
    fun getCoolWeatherAdvice(tempC: Double, isNight: Boolean): Pair<String, String?> {
        return if (!isNight) {
            when {
                tempC < 25.0 -> {
                    "It's ${"%.1f".format(tempC)}°C 🌤 Nice daytime weather — a warm coffee would be great." to "hot"
                }
                tempC > 31.0 -> {
                    "It's ${"%.1f".format(tempC)}°C ☀️ Quite hot outside — maybe a cold drink or frappe?" to "cold"
                }
                else -> {
                    "It's ${"%.1f".format(tempC)}°C ☁️ A smooth frappe or cake sounds perfect." to "frappe"
                }
            }
        } else {
            when {
                tempC < 25.0 -> {
                    "It's ${"%.1f".format(tempC)}°C 🌙 Cool night — a warm drink is comforting." to "hot"
                }
                tempC > 31.0 -> {
                    "It's ${"%.1f".format(tempC)}°C 🌙 Still warm tonight — maybe a cold drink?" to "cold"
                }
                else -> {
                    "It's ${"%.1f".format(tempC)}°C 🌙 Perfect night — maybe enjoy some cake." to "cake"
                }
            }
        }
    }


    // ---------------- EXISTING COMPONENTS ----------------

    @Composable
    fun showBalance() {
        val balanceState = remember { mutableStateOf(0.0) }

        LaunchedEffect(Unit) {
            balanceState.value = RealmBlockchainRepository.getBalance()
        }

        val bal = balanceState.value

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF5C3321))
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Wallet Balance",
                        color = Color(0xFFEED9C4),
                        fontSize = 15.sp
                    )
                    Text(
                        text = "$${"%.2f".format(bal)}",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    )

                }
                Icon(
                    painter = painterResource(id = R.drawable.wallet),
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
            color = Color(0xFF381D12),
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        CoffeeAppTheme {
            // Preview content
        }
    }

