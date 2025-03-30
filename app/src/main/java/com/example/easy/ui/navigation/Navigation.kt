package com.example.easy.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easy.ui.screens.*
import com.example.easy.viewmodel.BranchViewModel

enum class Screen {
    Login,
    BranchSelection,
    Menu,
    Order
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    branchViewModel: BranchViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

    NavHost(navController, startDestination = Screen.Login.name.lowercase()) {
        composable(Screen.Login.name.lowercase()) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.BranchSelection.name.lowercase()) {
                        popUpTo(Screen.Login.name.lowercase()) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.BranchSelection.name.lowercase()) {
            BranchSelectionScreen(
                onBranchSelected = { branchId ->
                    navController.navigate(Screen.Menu.name.lowercase()) {
                        popUpTo(Screen.BranchSelection.name.lowercase()) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Menu.name.lowercase()) {
            MenuScreen()
        }

        composable(Screen.Order.name.lowercase()) {
            OrderScreen(
                onBackClick = { navController.navigateUp() },
                onCartClick = { /* Handle cart click */ }
            )
        }
    }
}