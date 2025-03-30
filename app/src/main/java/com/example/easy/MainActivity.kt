package com.example.easy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.easy.ui.screens.LoginScreen
import com.example.easy.ui.screens.HomeScreen
import com.example.easy.ui.screens.OrderScreen
import com.example.easy.ui.theme.EasyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
            
            EasyTheme {
                when {
                    !isLoggedIn -> {
                        LoginScreen(onLoginSuccess = { isLoggedIn = true })
                    }
                    currentScreen == Screen.Home -> {
                        HomeScreen(
                            onLogout = { isLoggedIn = false },
                            onCreateOrder = { currentScreen = Screen.Order },
                            onViewOrders = { /* Implementar navegación a ver órdenes */ }
                        )
                    }
                    currentScreen == Screen.Order -> {
                        OrderScreen(
                            onBackClick = { currentScreen = Screen.Home },
                            onCartClick = { /* Implementar carrito */ }
                        )
                    }
                }
            }
        }
    }
}

enum class Screen {
    Home,
    Order
}