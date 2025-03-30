package com.example.easy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easy.ui.screens.BranchSelectionScreen
import com.example.easy.ui.screens.LoginScreen  // Add this import
import com.example.easy.ui.screens.MainMenuScreen
import com.example.easy.ui.screens.OrderScreen
import com.example.easy.viewmodel.BranchViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    branchViewModel: BranchViewModel = viewModel()
) {
    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("branch_selection") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("branch_selection") {
            BranchSelectionScreen(
                onBranchSelected = { branchId ->
                    branchViewModel.saveBranchId(branchId)
                    navController.navigate("main_menu") {
                        popUpTo("branch_selection") { inclusive = true }
                    }
                }
            )
        }

        composable("main_menu") {
            MainMenuScreen(
                onCreateOrderClick = {
                    navController.navigate("order")
                },
                onViewOrdersClick = {
                    navController.navigate("orders")
                }
            )
        }

        composable("order") {
            OrderScreen(
                onBackClick = { navController.navigateUp() },
                onCartClick = { /* Handle cart click */ }
            )
        }
    }
}