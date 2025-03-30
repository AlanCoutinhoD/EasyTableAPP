package com.example.easy.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easy.data.model.Branch
import com.example.easy.viewmodel.BranchViewModel
import com.example.easy.viewmodel.BranchViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchSelectionScreen(
    onBranchSelected: (Int) -> Unit,
    viewModel: BranchViewModel = viewModel(
        factory = BranchViewModelFactory(LocalContext.current)
    )
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val branches by viewModel.branches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Add LaunchedEffect to fetch branches when screen is shown
    LaunchedEffect(Unit) {
        viewModel.fetchBranches()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Seleccionar Sucursal") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                branches.isEmpty() -> {
                    Text(
                        text = "No hay sucursales disponibles",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(branches) { branch ->
                            // Inside BranchCard onClick
                            BranchCard(
                                branch = branch,
                                onClick = {
                                    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                                    prefs.edit()
                                        .putInt("selected_branch_id", branch.id)
                                        .apply()
                                    onBranchSelected(branch.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BranchCard(
    branch: Branch,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = branch.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = branch.address,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tel: ${branch.phone}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

