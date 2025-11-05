package com.example.coffeeapp

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
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

@Composable
fun AboutUs(navController: NavHostController) {
    Column(
        modifier = Modifier.padding(12.dp),

    ) {
        Box(modifier = Modifier.fillMaxWidth() .height(40.dp)){
            Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {navController.popBackStack()}) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null
                    )
                }
                Spacer(Modifier.width(20.dp))
                Text("About Us", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp )
            }
        }
        Image(
            painter = painterResource(R.drawable.cafe),
            contentDescription = null,
            modifier = Modifier
                .width(220.dp)
                .height(220.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
        Text("Welcome to Morning Roast, where every cup is crafted with passion. We believe coffee is more than a drink—it’s an experience. From carefully selected beans to cozy atmospheres, we aim to bring warmth and joy to your daily routine. Our mission is to serve high-quality coffee while supporting sustainable practices and local communities. Connect with us on social media or reach out anytime—we’d love to hear from you.",
            textAlign = TextAlign.Center,
            fontSize = 18.sp)

        Text(
            "\nVersion: 1.0.0\n" +
                    "\n" +
                    "Contact: info@morningRoast.com\n" +
                    "\n" +
                    "Follow Us",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center ){
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