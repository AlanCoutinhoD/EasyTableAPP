package com.example.easy.ui.screens

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
import androidx.compose.material.icons.outlined.Remove  // Changed to outlined variant
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    menuViewModel: MenuViewModel = viewModel(
        factory = MenuViewModelFactory(LocalContext.current)
    )
) {
    var showCart by remember { mutableStateOf(false) }
    var cartItems by remember { mutableStateOf(listOf<CartItem>()) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showQuantityDialog by remember { mutableStateOf(false) }

    val menu by menuViewModel.menu.collectAsState()
    val isLoading by menuViewModel.isLoading.collectAsState()
    val error by menuViewModel.error.collectAsState()

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
                    Text(
                        text = error ?: "",
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
private fun MenuItemCard(
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

// Remove the CartItem data class declaration at the bottom of the file