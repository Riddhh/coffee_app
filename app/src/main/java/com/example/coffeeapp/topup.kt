package com.example.coffeeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.coffeeapp.blockchain.RealmBlockchainRepository
import com.example.coffeeapp.micro.NetworkClient
import com.example.coffeeapp.micro.TopUpRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

enum class BankType {
    ABA,
    ACELEDA,
    WING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopUp(navController: NavHostController) {
    var amountText by remember { mutableStateOf("") }
    var selectedBank by remember { mutableStateOf<BankType?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val bg = Color(0xFFF5ECE4)
    val textBrown = Color(0xFF381D12)
    val texts = Color(0xFFFBF8F5)
//    val itemBg = Color(0xFFFBF8F5)
    val itemBg = Color(0xFF5C3321)

    0xFF5C3321
    Scaffold(
        containerColor = bg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Top Up Your Balance", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bg,              // ✅ top bar background
                    titleContentColor = textBrown     // ✅ title color
                )
            )
        },

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(paddingValues)
                .padding(10.dp)
        ) {
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    errorMessage = null
                },
                label = { Text("Type your balance", style = TextStyle(fontSize = 18.sp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5A3A26),
                    unfocusedBorderColor = Color(0xFFBCA89B),
                    cursorColor = Color(0xFF5A3A26),
                    focusedLabelColor = Color(0xFF5A3A26)
                )
            )

            Text(
                "Payment Method",
                style = TextStyle(
                    fontSize = 22.sp,
                    color = Color(0xFF381D12),
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(20.dp)
            )



            // ABA
            ListItem(
                colors = ListItemDefaults.colors(containerColor = bg),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5ECE4))
                    .clickable {
                        selectedBank = BankType.ABA
                        errorMessage = null
                    },
                leadingContent = {
                    Image(
                        painterResource(id = R.drawable.aba),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(90.dp)
                    )
                },
                headlineContent = {
                    Text(
                        "ABA Bank",
                        style = TextStyle(
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedBank == BankType.ABA) Color(0xFFCDA174) else Color(0xFF381D12)
                        )
                    )
                },
                supportingContent = {
                    Text(
                        if (selectedBank == BankType.ABA) "Will pay with ABA" else "Tap to pay with ABA Bank",
                        style = TextStyle(fontSize = 18.sp),
                        color = Color(0xFFA68C7E)
                    )
                }
            )

            // ACLEDA
            ListItem(
                colors = ListItemDefaults.colors(containerColor = bg),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .background(Color(0xFFF5ECE4))
                    .clickable {
                        selectedBank = BankType.ACELEDA
                        errorMessage = null
                    },
                leadingContent = {
                    Image(
                        painterResource(id = R.drawable.aceleda),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(90.dp)
                    )
                },
                headlineContent = {
                    Text(
                        "Aceleda Bank",
                        style = TextStyle(
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedBank == BankType.ACELEDA) Color(0xFFCDA174) else Color(0xFF381D12)
                        )
                    )
                },
                supportingContent = {
                    Text(
                        if (selectedBank == BankType.ACELEDA) "Will pay with Aceleda" else "Tap to pay with Aceleda Bank",
                        style = TextStyle(fontSize = 18.sp),
                        color = Color(0xFFA68C7E)
                    )
                }
            )

            // WING
            ListItem(
                colors = ListItemDefaults.colors(containerColor = bg),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp)
                    .background(Color(0xFFF5ECE4))
                    .clickable {
                        selectedBank = BankType.WING
                        errorMessage = null
                    },
                leadingContent = {
                    Image(
                        painterResource(id = R.drawable.wing),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(90.dp)
                    )
                },
                headlineContent = {
                    Text(
                        "Wing Bank",
                        style = TextStyle(
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedBank == BankType.WING) Color(0xFFCDA174) else Color(0xFF381D12)
                        )
                    )
                },
                supportingContent = {
                    Text(
                        if (selectedBank == BankType.WING) "Will pay with Wing" else "Tap to pay with Wing Bank",
                        style = TextStyle(fontSize = 18.sp),
                        color = Color(0xFFA68C7E)
                    )
                }
            )

            Spacer(Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = TextStyle(fontSize = 14.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            if (isProcessing) {
                Text(
                    text = "Processing bank payment...",
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .padding(20.dp)
                    .width(168.dp)
                    .height(50.dp)
                    .align(alignment = Alignment.CenterHorizontally)
                ,
                onClick = {
                    val amount = amountText.toDoubleOrNull()

                    if (amount == null || amount <= 0) {
                        errorMessage = "Please enter a valid amount."
                        return@Button
                    }

                    if (selectedBank == null) {
                        errorMessage = "Please select a bank."
                        return@Button
                    }

                    errorMessage = null
                    isProcessing = true

                    coroutineScope.launch {
                        try {
                            val bankName = when (selectedBank) {
                                BankType.ABA -> "ABA"
                                BankType.ACELEDA -> "ACELEDA"
                                BankType.WING -> "WING"
                                null -> "ABA"
                            }

                            val userId = "demoUser123"

                            val request = TopUpRequestDto(
                                userId = userId,
                                amount = amount,
                                bank = bankName,
                                mode = "TEST"
                            )

                            val response = NetworkClient.paymentApi.topUpTest(request)

                            if (response.success) {
                                // Optional: keep your local history chain
                                withContext(Dispatchers.IO) {
                                    RealmBlockchainRepository.addTopUpBlock(amount)
                                }

                                isProcessing = false

                                // ✅ Save data for QR screen
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "txId",
                                    response.transactionId
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "userId",
                                    userId
                                )

                                // ✅ Go to QR screen
                                navController.navigate("topup_qr")
                            } else {
                                isProcessing = false
                                errorMessage = "Payment failed: ${response.message}"
                            }

                        } catch (e: HttpException) {
                            isProcessing = false
                            val errBody = e.response()?.errorBody()?.string()
                            errorMessage = "Server error ${e.code()}: ${errBody ?: e.message()}"
                        } catch (e: Exception) {
                            isProcessing = false
                            errorMessage = "Error calling payment service: ${e.message}"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = itemBg,          // ✅ button background
                ),
                enabled = !isProcessing,
            ) {
                Text(
                    "Continue",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
            }
        }
    }
}
