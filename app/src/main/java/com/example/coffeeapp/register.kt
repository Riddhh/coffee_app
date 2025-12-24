package com.example.coffeeapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.coffeeapp.auth.AuthViewModel

@Composable
fun Register(navController: NavHostController) {
    val context = LocalContext.current
    val vm: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val state = vm.state.collectAsState().value

    var name by remember { mutableStateOf("") } // optional: not used in backend
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val textBrown = Color(0xFF381D12)
    val btn = Color(0xFF5C3321)
    val txtbtn = Color(0xFF5C3321)

    Column(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.cafe),
            contentDescription = null
        )
        Text(
            text = "Create Account",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp),
            textAlign = TextAlign.Center,
            color = textBrown
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5A3A26),
                unfocusedBorderColor = Color(0xFFBCA89B),
                cursorColor = Color(0xFF5A3A26),
                focusedLabelColor = Color(0xFF5A3A26)
            ),
            shape = RoundedCornerShape(16.dp),
        )

        Spacer(Modifier.height(15.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5A3A26),
                unfocusedBorderColor = Color(0xFFBCA89B),
                cursorColor = Color(0xFF5A3A26),
                focusedLabelColor = Color(0xFF5A3A26)
            ),
            shape = RoundedCornerShape(16.dp),
        )

        Spacer(Modifier.height(15.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5A3A26),
                unfocusedBorderColor = Color(0xFFBCA89B),
                cursorColor = Color(0xFF5A3A26),
                focusedLabelColor = Color(0xFF5A3A26)
            ),
            shape = RoundedCornerShape(16.dp),
        )

        Spacer(Modifier.height(25.dp))

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = btn ,          // ✅ button background
            ),
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else {
                    vm.register(name = name, email = email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (state.loading)
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            else
                Text("Register", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Show any error message
        state.error?.let {
            Text(
                it,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Navigate automatically to login after successful register
        if (state.isAuthed) {
            LaunchedEffect(Unit) {
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login", color = txtbtn, fontSize = 17.sp)
        }
    }
}
