package com.example.coffeeapp

import androidx.compose.foundation.Image
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
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.coffeeapp.blockchain.BlockchainManager
import com.example.coffeeapp.blockchain.RealmBlockchainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TopUp(navController: NavHostController){
    var text by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            "Top up Your Balance", style = TextStyle(
                fontSize = 22.sp, fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(40.dp))
        Text(
            "How much Do you want to top up?",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 18.sp, fontWeight = FontWeight.Medium
            )
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("$5", style = TextStyle(fontSize = 18.sp)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            shape = RoundedCornerShape(12.dp),


            )
//                                Spacer(Modifier.height(69.dp))
        Text(
            "Payment Method", style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,

                ), modifier = Modifier.padding(20.dp)
        )
        ListItem(leadingContent = {
            Image(
                painterResource(id = R.drawable.aba),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(90.dp)

            )
        }, headlineContent = {
            Text(
                "ABA Bank", style = TextStyle(
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }, supportingContent = {
            Text(
                "Tap to pay with ABA Bank", style = TextStyle(
                    fontSize = 18.sp,
                )
            )
        })
        ListItem(
            modifier = Modifier.padding(top = 20.dp),
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
                    "Aceleda Bank", style = TextStyle(
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
            },
            supportingContent = {
                Text(
                    "Tap to pay with Aceleda Bank", style = TextStyle(
                        fontSize = 18.sp,
                    )
                )
            })
        ListItem(
            modifier = Modifier.padding(top = 20.dp),
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
                    "Wing Bank", style = TextStyle(
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
            },
            supportingContent = {
                Text(
                    "Tap to pay with Wing Bank", style = TextStyle(
                        fontSize = 18.sp,
                    )
                )
            })
        Spacer(Modifier.height(30.dp))
        Button(
            onClick = {
                val amount = text.toDoubleOrNull() ?: return@Button

                CoroutineScope(Dispatchers.IO).launch {
                    RealmBlockchainRepository.addTopUpBlock(amount)
                }

                navController.navigate("transactionHistory")
            },
            modifier = Modifier
                .padding(20.dp)
                .width(169.dp)
                .height(50.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                "Continue", style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }

    }
}