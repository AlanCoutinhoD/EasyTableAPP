package com.example.easy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.easy.ui.theme.PrimaryBlack
import com.example.easy.ui.theme.TextPrimary
import com.example.easy.ui.theme.TextSecondary
import com.example.easy.ui.theme.LinkColor
import com.example.easy.ui.theme.White
import com.example.easy.ui.viewmodel.LoginViewModel
import com.example.easy.ui.viewmodel.LoginUiState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsState(initial = LoginUiState.Initial)

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> onLoginSuccess()
            is LoginUiState.Error -> showError = (uiState as LoginUiState.Error).message
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = TextPrimary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Ingresa tus credenciales para acceder a tu cuenta",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = TextSecondary,
                focusedBorderColor = PrimaryBlack  // Changed from PrimaryBlue
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = TextSecondary,
                focusedBorderColor = PrimaryBlack  // Changed from PrimaryBlue
            ),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { /* Handle forgot password */ }) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = LinkColor
                )
            }
        }

        // Update the Button onClick:
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlack
            ),
            enabled = uiState !is LoginUiState.Loading
        ) {
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(color = White)
            } else {
                Text("Iniciar sesión")
            }
        }

        // Add error message if exists
        showError?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "¿No tienes una cuenta?",
                color = TextSecondary
            )
            TextButton(onClick = { /* Handle registration */ }) {
                Text(
                    "Regístrate",
                    color = LinkColor
                )
            }
        }
    }
}