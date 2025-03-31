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

import android.content.Context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController() // Initialize NavController

            // Retrieve values from shared preferences
            val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            val branchId = prefs.getInt("selected_branch_id", -1) // Default to -1 if not found
            val token = prefs.getString("token", "") ?: "" // Default to empty string if not found

            NavHost(navController = navController, startDestination = "loginScreen") {
                composable("loginScreen") {
                    LoginScreen(
                        onLoginSuccess = {
                            // Save token in shared preferences
                            prefs.edit().putString("token", "your_bearer_token_here").apply()
                            navController.navigate("homeScreen")
                        }
                    )
                }
                composable("homeScreen") {
                    HomeScreen(
                        navController = navController,
                        onLogout = { navController.navigate("loginScreen") },
                        onCreateOrder = { navController.navigate("orderScreen") },
                        onViewOrders = { navController.navigate("viewOrdersScreen") }
                    )
                }
                composable("orderScreen") {
                    OrderScreen(
                        onBackClick = { navController.navigate("homeScreen") }
                    )
                }
                composable("viewOrdersScreen") {
                    ViewOrdersScreen(branchId = branchId, token = token) // Pass branchId and token
                }
            }
        }
    }
}

enum class Screen {
    Home,
    Order
}