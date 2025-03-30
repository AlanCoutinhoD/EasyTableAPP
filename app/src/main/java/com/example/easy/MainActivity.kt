package com.example.easy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.easy.ui.screens.LoginScreen
import com.example.easy.ui.screens.HomeScreen
import com.example.easy.ui.screens.OrderScreen
import com.example.easy.ui.screens.BranchSelectionScreen
import com.example.easy.ui.theme.EasyTheme

// Add these imports at the top
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }
            var hasBranchSelected by remember { mutableStateOf(false) }
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
            
            EasyTheme {
                when {
                    !isLoggedIn -> {
                        LoginScreen(onLoginSuccess = { isLoggedIn = true })
                    }
                    !hasBranchSelected -> {
                        BranchSelectionScreen(
                            onBranchSelected = { branchId: Int ->
                                hasBranchSelected = true
                                currentScreen = Screen.Home
                            }
                        )
                    }
                    currentScreen == Screen.Home -> {
                        HomeScreen(
                            onLogout = { 
                                isLoggedIn = false
                                hasBranchSelected = false 
                            },
                            onCreateOrder = { currentScreen = Screen.Order },
                            onViewOrders = { /* Implementar navegación a ver órdenes */ }
                        )
                    }
                    currentScreen == Screen.Order -> {
                        OrderScreen(
                            onBackClick = { currentScreen = Screen.Home }
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