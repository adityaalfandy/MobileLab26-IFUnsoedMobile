package com.pemmob.fandy.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pemmob.fandy.R
import com.pemmob.fandy.data.model.Product
import com.pemmob.fandy.ui.viewmodel.ProductUiState
import com.pemmob.fandy.ui.viewmodel.ProductViewModel
import com.pemmob.fandy.util.JualanConstants.BASE_URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailProductScreen(
    productId: Int,
    navController: NavController?,
    viewModel: ProductViewModel
) {
    val context = LocalContext.current
    var quantity by rememberSaveable { mutableStateOf(1) }
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is ProductUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ProductUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
        }
        is ProductUiState.Success -> {
            val product = state.products.find { it.id == productId }

            if (product == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Produk tidak ditemukan.")
                }
            } else {
                StatelessDetailProduct(
                    product = product,
                    quantity = quantity,
                    onQuantityChange = { newQuantity -> quantity = newQuantity },
                    onBackClick = { navController?.popBackStack() },
                    onAddToCartClick = {
                        Toast.makeText(context, "Membeli sebanyak $quantity", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatelessDetailProduct(
    product: Product?,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onBackClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(id = R.drawable.back_icon), contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (product != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                val imageModel: Any = if (product.img == "dummy_product") {
                    R.drawable.dummy_product
                } else {
                    "${BASE_URL}img/${product.img}"
                }
                
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        contentScale = ContentScale.Fit
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rp ${product.price}",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Deskripsi", fontWeight = FontWeight.Bold)
                    Text(product.description ?: "")
                    Text("Stok: ${product.stock}")

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Jumlah Beli")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalIconButton(
                                onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                                enabled = quantity > 1
                            ) {
                                Text("-")
                            }
                            Text(
                                text = quantity.toString(),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            FilledTonalIconButton(
                                onClick = { if (quantity < product.stock) onQuantityChange(quantity + 1) },
                                enabled = quantity < product.stock
                            ) {
                                Text("+")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onAddToCartClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = product.stock > 0 && quantity > 0
                    ) {
                        Text("Tambah ke Keranjang")
                    }
                }
            }
        }
    }
}