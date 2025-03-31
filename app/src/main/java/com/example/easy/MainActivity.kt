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
import com.example.easy.ui.screens.ViewOrdersScreen // Add this import
import com.example.easy.ui.theme.EasyTheme

// Add these imports at the top
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController() // Initialize NavController

            NavHost(navController = navController, startDestination = "loginScreen") {
                composable("loginScreen") {
                    LoginScreen(
                        onLoginSuccess = { navController.navigate("homeScreen") }
                    )
                }
                composable("homeScreen") {
                    HomeScreen(
                        navController = navController,
                        onLogout = { navController.navigate("loginScreen") },
                        onCreateOrder = { navController.navigate("orderScreen") },
                        onViewOrders = { navController.navigate("viewOrdersScreen") } // Ensure this navigates to viewOrdersScreen
                    )
                }
                composable("orderScreen") {
                    OrderScreen(
                        onBackClick = { navController.navigate("homeScreen") }
                    )
                }
                composable("viewOrdersScreen") { // Ensure this route is defined
                    ViewOrdersScreen()
                }
            }
        }
    }
}

enum class Screen {
    Home,
    Order
}