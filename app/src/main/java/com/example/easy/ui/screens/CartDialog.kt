package com.example.easy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
// Remove the unused import: filled.Remove
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.easy.ui.theme.TextSecondary

@Composable
fun CartDialog(
    onDismiss: () -> Unit,
    cartItems: List<CartItem>,
    onClearCart: () -> Unit,
    onUpdateQuantity: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onFinishOrder: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tu Orden",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        TextButton(
                            onClick = onClearCart,
                            enabled = cartItems.isNotEmpty()
                        ) {
                            Text("Vaciar", color = Color.Red)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, "Cerrar")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (cartItems.isEmpty()) {
                    // Empty cart view
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Carrito vacío",
                            modifier = Modifier.size(64.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Tu carrito está vacío",
                            color = TextSecondary
                        )
                    }
                } else {
                    // Cart items
                    cartItems.forEach { item ->
                        CartItemRow(
                            item = item,
                            onUpdateQuantity = onUpdateQuantity,
                            onRemoveItem = onRemoveItem
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal")
                        Text(
                            "$${cartItems.sumOf { it.price * it.quantity }}",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Finish order button
                    Button(
                        onClick = onFinishOrder,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Finalizar Orden")
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onUpdateQuantity: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                IconButton(
                    onClick = { onUpdateQuantity(item, item.quantity - 1) },
                    enabled = item.quantity > 1
                ) {
                    Text("-", style = MaterialTheme.typography.titleMedium)  // Using text instead of icon
                }
                Text(
                    text = "${item.quantity}",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = { onUpdateQuantity(item, item.quantity + 1) }
                ) {
                    Icon(Icons.Default.Add, "Aumentar cantidad")
                }
                Text(
                    text = "$${item.price} c/u",
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        IconButton(onClick = { onRemoveItem(item) }) {
            Icon(Icons.Default.Close, "Eliminar item", tint = Color.Red)
        }
    }
}

data class CartItem(
    val id: Int,
    val name: String,
    val price: Double,
    val quantity: Int
)