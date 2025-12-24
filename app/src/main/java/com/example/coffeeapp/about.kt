package com.example.coffeeapp

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUs(navController: NavHostController) {
    val bg = Color(0xFFF5ECE4)
    val textBrown = Color(0xFF381D12)
    val subtxt = Color(0xFFA68C7E)

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Text(
                        "About Us",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bg,              // ✅ top bar background
                    titleContentColor = textBrown    // ✅ title color
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(paddingValues)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(R.drawable.cafe),
                contentDescription = null,
                modifier = Modifier
                    .width(220.dp)
                    .height(220.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                "Welcome to Morning Roast, where every cup is crafted with passion. " +
                        "We believe coffee is more than a drink—it’s an experience. " +
                        "From carefully selected beans to cozy atmospheres, we aim to bring warmth " +
                        "and joy to your daily routine. Our mission is to serve high-quality coffee " +
                        "while supporting sustainable practices and local communities. Connect with " +
                        "us on social media or reach out anytime—we’d love to hear from you.",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = subtxt
            )

            Text(
                "\nVersion: 1.0.0\n\nContact: info@morningRoast.com\n\nFollow Us",
                textAlign = TextAlign.Center,
                color = subtxt,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(R.drawable.facebook),
                        contentDescription = null
                    )
                }
                Spacer(Modifier.width(10.dp))
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(R.drawable.telegram),
                        contentDescription = null
                    )
                }
                Spacer(Modifier.width(10.dp))
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(R.drawable.twitter),
                        contentDescription = null
                    )
                }
            }
        }
    }
}
