package com.example.easy.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.easy.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easy.viewmodel.MenuViewModel
import com.example.easy.viewmodel.MenuViewModelFactory
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.easy.data.model.Product

// Add this import at the top with other imports
import com.example.easy.data.model.CartItem

import androidx.compose.material3.OutlinedTextField

// Add to imports
import com.example.easy.data.model.Branch
import com.example.easy.data.model.Order
import com.example.easy.data.model.OrderItem
import com.example.easy.data.api.ApiClient
import kotlinx.coroutines.launch
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    onBackClick: () -> Unit,
    menuViewModel: MenuViewModel = viewModel(
        factory = MenuViewModelFactory(LocalContext.current)
    )
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showCart by remember { mutableStateOf(false) }
    var cartItems by remember { mutableStateOf(listOf<CartItem>()) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showQuantityDialog by remember { mutableStateOf(false) }
    var tableNumber by remember { mutableStateOf("") } // Add this state

    // Fix the state collection
    val menu = menuViewModel.menu.collectAsState().value
    val isLoading = menuViewModel.isLoading.collectAsState().value
    val error = menuViewModel.error.collectAsState().value
    val selectedBranch = menuViewModel.selectedBranch.collectAsState().value

    LaunchedEffect(Unit) {
        menuViewModel.fetchMenu()
    }

    Column {
        CenterAlignedTopAppBar(
            title = { Text("Tomar Orden") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            },
            actions = {
                IconButton(onClick = { showCart = true }) {
                    Icon(Icons.Default.ShoppingCart, "Cart")
                }
            }
        )
        
        // Add table number input field
        OutlinedTextField(
            value = tableNumber,
            onValueChange = { tableNumber = it },
            label = { Text("Número de Mesa") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )
        
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    // Fix the Elvis operator warning
                    Text(
                        text = error.toString(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                menu.isEmpty() -> {
                    Text(
                        text = "No hay productos disponibles",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        menu.forEach { menuResponse ->
                            items(menuResponse.products) { product ->
                                MenuItemCard(
                                    title = product.name,
                                    description = product.description,
                                    price = "$ ${product.price}",
                                    imageUrl = product.image_url,
                                    onClick = {
                                        selectedProduct = product
                                        showQuantityDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add this block to show the cart dialog
    // Replace the entire cart dialog implementation
    if (showCart) {
        AlertDialog(
            onDismissRequest = { showCart = false },
            title = { Text("Carrito") },
            text = {
                if (cartItems.isEmpty()) {
                    Text("El carrito está vacío")
                } else {
                    LazyColumn {
                        items(cartItems) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Cantidad: ${item.quantity}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = "$${item.price}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Divider()
                        }
                    }
                }
            },
            confirmButton = {
                // Inside the confirmButton onClick
                Button(
                    onClick = {
                        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                        val branchId = prefs.getInt("selected_branch_id", -1)
                        
                        if (branchId == -1) {
                            Toast.makeText(context, "Por favor seleccione una sucursal", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        val order = Order(
                            total_amount = cartItems.sumOf { it.price.toDoubleOrNull()?.times(it.quantity) ?: 0.0 },
                            payment_method = "cash",
                            branch_id = branchId,  // Using the stored branch ID
                            tableNumber = tableNumber,
                            items = cartItems.map { item ->
                                OrderItem(
                                    product_id = item.id,
                                    quantity = item.quantity,
                                    price = item.price.toDoubleOrNull() ?: 0.0
                                )
                            }
                        )
                        
                        scope.launch {
                            try {
                                val response = ApiClient.orderService.createOrder(order)
                                if (response.isSuccessful) {
                                    Log.d("OrderScreen", "Order created successfully: ${response.body()}")
                                    Toast.makeText(context, "Orden creada exitosamente", Toast.LENGTH_SHORT).show()
                                    showCart = false
                                    cartItems = emptyList()
                                } else {
                                    Log.e("OrderScreen", "Error creating order: ${response.errorBody()?.string()}")
                                    Toast.makeText(context, "Error al crear la orden", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e("OrderScreen", "Exception creating order", e)
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = cartItems.isNotEmpty() && tableNumber.isNotEmpty()
                ) {
                    Text("Confirmar Orden")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCart = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showQuantityDialog && selectedProduct != null) {
        AddToCartDialog(
            product = selectedProduct!!,
            onDismiss = { 
                showQuantityDialog = false
                selectedProduct = null
            },
            onConfirm = { quantity ->
                cartItems = cartItems + CartItem(
                    id = selectedProduct!!.id,
                    name = selectedProduct!!.name,
                    price = selectedProduct!!.price,
                    quantity = quantity
                )
                showQuantityDialog = false
                selectedProduct = null
            }
        )
    }
}

@Composable
fun MenuItemCard(
    title: String,
    description: String,
    price: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Text(
                    text = price,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TagChip(tag: Tag) {
    Surface(
        color = when(tag) {
            Tag.POPULAR -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
            Tag.VEGETARIANO -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            Tag.SIN_GLUTEN -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
        },
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = tag.text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = when(tag) {
                Tag.POPULAR -> MaterialTheme.colorScheme.error
                Tag.VEGETARIANO -> MaterialTheme.colorScheme.primary
                Tag.SIN_GLUTEN -> MaterialTheme.colorScheme.secondary
            },
            style = MaterialTheme.typography.labelSmall
        )
    }
}

enum class Tag(val text: String) {
    POPULAR("Popular"),
    VEGETARIANO("Vegetariano"),
    SIN_GLUTEN("Sin Gluten")
}

@Composable
private fun AddToCartDialog(
    product: Product,  // Changed from MenuItem to Product
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar al carrito") },
        text = {
            Column {
                Text("${product.name} - $${product.price}")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- }
                    ) {
                        Icon(Icons.Rounded.Remove, "Decrease")  // Changed to Rounded variant
                    }
                    Text(quantity.toString())
                    IconButton(
                        onClick = { quantity++ }
                    ) {
                        Icon(Icons.Default.Add, "Increase")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(quantity) }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}