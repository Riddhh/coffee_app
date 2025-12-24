package com.example.coffeeapp

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val border_color = Color(0xFFE3CDB8)

    // ✅ read current language
    val currentLang = LocaleHelper.getLanguage(context)
    val bg = Color(0xFFFBF3EA)
    val textt = Color(0xFF5A3A26)

    Scaffold(
        containerColor = bg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Language", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bg,              // ✅ top bar background
                    titleContentColor = textt     // ✅ title color
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            LanguageOption(
                title = "English",
                selected = currentLang == "en",
                onClick = {
                    if (currentLang != "en") {
                        LocaleHelper.setLanguage(context, "en")
                        activity?.recreate()
                    }
                }
            )

            LanguageOption(
                title = "Khmer (ភាសាខ្មែរ)",
                selected = currentLang == "km",
                onClick = {
                    if (currentLang != "km") {
                        LocaleHelper.setLanguage(context, "km")
                        activity?.recreate()
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                border = BorderStroke(1.5.dp, color = border_color  ),
                modifier = Modifier.fillMaxWidth().height(45.dp),
                onClick = { navController.popBackStack() }
            ) {
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textt)
            }
        }
    }
}

@Composable
private fun LanguageOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) Color(0xFF6A4E39) else Color(0xFFE3CDB8)
    val bgColor = if (selected) Color(0xFFF3E9E1) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp)
            .clickable(enabled = !selected) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            color = if (selected) Color(0xFF6A4E39) else Color.Black
        )

        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color(0xFF6A4E39)
            )
        }
    }
}
