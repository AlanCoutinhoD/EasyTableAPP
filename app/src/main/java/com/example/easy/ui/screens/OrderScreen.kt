package com.example.easy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.easy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    var showCart by remember { mutableStateOf(false) }
    var cartItems by remember { mutableStateOf(listOf<CartItem>()) }
    var selectedMenuItem by remember { mutableStateOf<MenuItem?>(null) }
    var showQuantityDialog by remember { mutableStateOf(false) }

    val menuItems = remember {
        listOf(
            MenuItem(0, "Pasta Carbonara", "Pasta con salsa cremosa de huevo, queso parmesano, panceta y...", 15.99, Tag.POPULAR),
            MenuItem(1, "Filete de Salmón", "Filete de salmón a la parrilla con salsa de limón y hierbas,...", 22.50, Tag.SIN_GLUTEN),
            MenuItem(2, "Risotto de Champiñones", "Arroz cremoso cocinado lentamente con champiñones,...", 18.75, Tag.VEGETARIANO),
            MenuItem(3, "Ensalada César", "Lechuga romana, crutones, queso parmesano y aderezo César...", 12.25, Tag.VEGETARIANO)
        )
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
        
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(menuItems) { menuItem ->
                MenuItemCard(
                    title = menuItem.name,
                    description = menuItem.description,
                    price = "$${menuItem.price}",
                    tag = menuItem.tag,
                    onClick = {
                        selectedMenuItem = menuItem
                        showQuantityDialog = true
                    }
                )
            }
        }
    }

    if (showQuantityDialog && selectedMenuItem != null) {
        AddToCartDialog(
            menuItem = selectedMenuItem!!,
            onDismiss = { 
                showQuantityDialog = false
                selectedMenuItem = null
            },
            onConfirm = { quantity ->
                cartItems = cartItems + CartItem(
                    id = selectedMenuItem!!.id,
                    name = selectedMenuItem!!.name,
                    price = selectedMenuItem!!.price,
                    quantity = quantity
                )
                showQuantityDialog = false
                selectedMenuItem = null
            }
        )
    }

    if (showCart) {
        CartDialog(
            onDismiss = { showCart = false },
            cartItems = cartItems,
            onClearCart = { cartItems = emptyList() },
            onUpdateQuantity = { item, newQuantity ->
                cartItems = cartItems.map {
                    if (it.id == item.id) it.copy(quantity = newQuantity)
                    else it
                }
            },
            onRemoveItem = { item ->
                cartItems = cartItems.filter { it.id != item.id }
            },
            onFinishOrder = {
                showCart = false
            }
        )
    }
}

data class MenuItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val tag: Tag
)

@Composable
private fun MenuItemCard(
    title: String,
    description: String,
    price: String,
    tag: Tag? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
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
                Spacer(modifier = Modifier.height(8.dp))
                tag?.let { 
                    TagChip(tag)
                }
            }
            Text(
                text = price,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
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
    menuItem: MenuItem,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = menuItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        enabled = quantity > 1
                    ) {
                        Text("-", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "$quantity",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { quantity++ }) {
                        Icon(Icons.Default.Add, "Aumentar cantidad")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onConfirm(quantity) }) {
                        Text("Añadir")
                    }
                }
            }
        }
    }
}