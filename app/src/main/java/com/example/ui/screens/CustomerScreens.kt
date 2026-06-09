package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderEntity
import com.example.data.ProductEntity
import com.example.ui.MartViewModel
import com.example.ui.components.CategoryPill
import com.example.ui.components.HeroBanner
import com.example.ui.components.ProductCard
import org.json.JSONArray
import org.json.JSONException

@Composable
fun CustomerShopScreen(
    viewModel: MartViewModel,
    modifier: Modifier = Modifier
) {
    val filteredProds by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()

    val wordWelcome = when(lang) {
        "Español" -> "Bienvenido al SV Mart"
        "বাংলা" -> "এসভি অনলাইন মার্টে স্বাগতম"
        else -> "Welcome to SV Mart"
    }
    val wordDiscover = when(lang) {
        "Español" -> "Descubre colecciones exclusivas"
        "বাংলা" -> "এক্সক্লুসিভ কালেকশন খুঁজুন"
        else -> "Discover exclusive collections"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("customer_shop_screen")
    ) {
        // High-contrast greeting header with Real Logo Brand image
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_sv_logo),
                contentDescription = "SV Mart Brand Logo",
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = wordWelcome,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = wordDiscover,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar with test tags - Sleek Theme compliant pill-shaped TextField
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { 
                Text(
                    text = if (lang == "Español") "Buscar productos..." else if (lang == "বাংলা") "পণ্য খুঁজুন..." else "Search products...",
                    fontSize = 13.sp
                ) 
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "AI search indicator",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_bar")
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Promo AI Assistant Banner
        HeroBanner(
            title = if (lang == "Español") "Prueba el asistente de IA" else if (lang == "বাংলা") "এআই শপিং অ্যাসিস্ট্যান্ট ট্রাই করুন" else "Try AI Shopping Assistant",
            subtitle = if (lang == "Español") "Encuentra la combinación perfecta de moda y tecnología interactuando con Gemini." else if (lang == "বাংলা") "জেমিনি এআই-এর সাথে কথা বলে আপনার জন্য পারফেক্ট গ্যাজেট বা জামাকাপড় বেছে নিন।" else "Find the perfect fashion context or matching tech assets by speaking to Gemini.",
            buttonText = if (lang == "Español") "Iniciar Asistente" else if (lang == "বাংলা") "অ্যাসিস্ট্যান্ট চালু করুন" else "Launch Chat",
            onClick = { viewModel.navigateTo("ChatAssistant") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Category Horizontal Selection Row
        Text(
            text = if (lang == "Español") "CATEGORÍAS" else if (lang == "বাংলা") "ক্যাটাগরি" else "EXPLORE CATEGORIES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        val categories = listOf("All", "Electronics", "Fashion", "Grocery", "Home & Decor")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                CategoryPill(
                    name = cat,
                    isSelected = selectedCategory == cat,
                    onClick = { viewModel.setCategory(cat) }
                )
            }
        }

        // Product Catalog Grid List (Optimized)
        if (filteredProds.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = "No products found matching filters.",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("product_grid"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredProds, key = { it.id }) { product ->
                    val isStarred = wishlist.any { it.productId == product.id }
                    ProductCard(
                        product = product,
                        isStarred = isStarred,
                        onProductClick = { viewModel.selectProduct(product.id) },
                        onStarClick = { viewModel.toggleWishlist(product.id) },
                        onAddToCartClick = { viewModel.addToCart(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductDetailsScreen(
    viewModel: MartViewModel,
    modifier: Modifier = Modifier
) {
    val prods by viewModel.products.collectAsState()
    val selId by viewModel.selectedProductId.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()

    val product = prods.find { it.id == selId }

    if (product == null) {
        Box(modifier = modifier.fillMaxSize()) {
            Text("Product not found.", modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    val isStarred = wishlist.any { it.productId == product.id }
    var buyQty by remember { mutableStateOf(1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag("product_details_root")
    ) {
        // Simple back stack navigator trigger
        IconButton(
            onClick = { viewModel.navigateBack() },
            modifier = Modifier.testTag("details_back_button")
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Return home")
        }

        // Visual Presentation Header panel with Real Image support
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
        ) {
            if (product.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val illustrationIcon = when (product.category) {
                    "Electronics" -> Icons.Default.Watch
                    "Fashion" -> Icons.Default.Checkroom
                    "Grocery" -> Icons.Default.EnergySavingsLeaf
                    else -> Icons.Default.Home
                }
                Icon(
                    imageVector = illustrationIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.Center)
                )
            }

            // High contrast ratings row absolute mapping
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF1C40F), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${product.rating} Rating Indices", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Title and heart toggle row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(0.85f)) {
                Text(
                    text = product.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Collection: ${product.category}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(
                onClick = { viewModel.toggleWishlist(product.id) },
                modifier = Modifier.weight(0.15f).testTag("details_wish_toggle")
            ) {
                Icon(
                    imageVector = if (isStarred) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isStarred) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Price tags
        Text(
            text = "$${product.price}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Warehouse Stock Status: ${product.stock} Units Available",
            fontSize = 12.sp,
            color = if (product.stock < 10) Color.Red else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontWeight = if (product.stock < 10) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Document description details
        Text(
            text = if (lang == "Español") "Detalles del producto" else if (lang == "বাংলা") "পণ্যের বিবরণ" else "PRODUCT DETAILS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = product.description,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(18.dp))

        // Purchase quantity state metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (lang == "Español") "Seleccionar cantidad" else if (lang == "বাংলা") "পরিমাণ নির্ধারণ করুন" else "Select Purchase Volume:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                IconButton(
                    onClick = { buyQty = (buyQty - 1).coerceAtLeast(1) },
                    modifier = Modifier.size(36.dp).testTag("qty_decrease")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                }
                Text(
                    text = "$buyQty",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                IconButton(
                    onClick = { buyQty = (buyQty + 1).coerceAtMost(product.stock) },
                    modifier = Modifier.size(36.dp).testTag("qty_increase")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons
        Button(
            onClick = {
                viewModel.addToCart(product.id, buyQty)
                buyQty = 1
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("details_add_to_cart_button")
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (lang == "Español") "Añadir a la cesta" else if (lang == "বাংলা") "কার্টে যোগ করুন" else "Add to Shop Basket", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Interactive "Ask AI about this product" Button
        OutlinedButton(
            onClick = {
                viewModel.navigateTo("ChatAssistant")
                viewModel.sendChatMessage("Tell me about the ${product.name}, what makes it premium and what features it has?", true)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("details_ask_ai_button")
        ) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (lang == "Español") "Preguntar al asistente de IA" else if (lang == "বাংলা") "পণ্যের ব্যাপারে আমাদের এআই-কে জিজ্ঞেস করুন" else "Consult AI Assistant About This item", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Reviews mapping listings
        Text(
            text = if (lang == "Español") "Opiniones de los usuarios" else if (lang == "বাংলা") "ব্যবহারকারী রিভিউ" else "VERIFIED CUSTOMER TESTIMONIALS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        val reviewsList = remember(product.reviewsJson) {
            try {
                val array = JSONArray(product.reviewsJson)
                val list = mutableListOf<Triple<String, Int, String>>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(Triple(obj.getString("user"), obj.getInt("rating"), obj.getString("comment")))
                }
                list
            } catch (e: JSONException) {
                emptyList()
            }
        }

        if (reviewsList.isEmpty()) {
            Text("No user reviews checked yet.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                reviewsList.forEach { r ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = r.first, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Row {
                                    repeat(r.second) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF1C40F), modifier = Modifier.size(11.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = r.third, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun WishlistScreen(
    viewModel: MartViewModel,
    modifier: Modifier = Modifier
) {
    val prods by viewModel.products.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()

    val wishlistedProds = remember(wishlist, prods) {
        val set = wishlist.map { it.productId }.toSet()
        prods.filter { it.id in set }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("wishlist_root")
    ) {
        Text(
            text = if (lang == "Español") "Tu lista de deseos" else if (lang == "বাংলা") "পছন্দের তালিকা" else "Your Wishlist Assets",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = if (lang == "Español") "Artículos que te interesan" else if (lang == "বাংলা") "যেসব পণ্য আপনি বুকমার্ক করেছেন" else "Starred items mapped dynamically",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (wishlistedProds.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (lang == "Español") "Tu lista de deseos está vacía." else if (lang == "বাংলা") "আপনার পছন্দের তালিকায় কিছুই নেই" else "Wishlist matches represent null status currently.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize().testTag("wishlist_scrollable")
            ) {
                items(wishlistedProds) { prod ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectProduct(prod.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                            ) {
                                val vector = when (prod.category) {
                                    "Electronics" -> Icons.Default.Watch
                                    "Fashion" -> Icons.Default.Checkroom
                                    "Grocery" -> Icons.Default.EnergySavingsLeaf
                                    else -> Icons.Default.Home
                                }
                                Icon(imageVector = vector, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = prod.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(text = "$${prod.price}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(
                                onClick = { viewModel.addToCart(prod.id) },
                                modifier = Modifier.testTag("wish_add_${prod.id}")
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Add to Basket", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(
                                onClick = { viewModel.toggleWishlist(prod.id) },
                                modifier = Modifier.testTag("wish_delete_${prod.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartScreen(
    viewModel: MartViewModel,
    modifier: Modifier = Modifier
) {
    val prods by viewModel.products.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val summary by viewModel.cartSummary.collectAsState()

    val couponCode by viewModel.couponCode.collectAsState()
    val discountPercent by viewModel.couponDiscount.collectAsState()
    val couponFeedback by viewModel.couponMessage.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()

    val cartFullProducts = remember(cartItems, prods) {
        cartItems.mapNotNull { item ->
            val p = prods.find { it.id == item.productId }
            if (p != null) Pair(p, item.quantity) else null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("cart_root")
    ) {
        Text(
            text = if (lang == "Español") "Tu carrito" else if (lang == "বাংলা") "শপিং কার্ট" else "Your Shopping Basket",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "${summary.totalCount} items loaded in active queue",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (cartFullProducts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (lang == "Español") "Tu carrito de compras está vacío." else if (lang == "বাংলা") "আপনার কার্ট বর্তমানে খালি আছে" else "Your shopping carriage is empty.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("cart_list"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartFullProducts) { pair ->
                        val item = pair.first
                        val qty = pair.second
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val vector = when (item.category) {
                                        "Electronics" -> Icons.Default.Watch
                                        "Fashion" -> Icons.Default.Checkroom
                                        "Grocery" -> Icons.Default.EnergySavingsLeaf
                                        else -> Icons.Default.Home
                                    }
                                    Icon(imageVector = vector, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                    Text(text = "$${item.price} each", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.updateCartQuantity(item.id, qty - 1) },
                                        modifier = Modifier.size(32.dp).testTag("cart_qty_dec_${item.id}")
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(12.dp))
                                    }
                                    Text(text = "$qty", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp))
                                    IconButton(
                                        onClick = { viewModel.updateCartQuantity(item.id, qty + 1) },
                                        modifier = Modifier.size(32.dp).testTag("cart_qty_inc_${item.id}")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Coupon codes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = couponCode,
                        onValueChange = { viewModel.couponCode.value = it },
                        placeholder = { Text(if (lang == "Español") "Entrar cupón..." else "Enter SV Code...") },
                        modifier = Modifier
                            .weight(0.7f)
                            .padding(end = 6.dp)
                            .testTag("coupon_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Button(
                        onClick = { viewModel.validateCoupon(couponCode) },
                        modifier = Modifier
                            .weight(0.3f)
                            .testTag("coupon_apply_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Apply", fontSize = 11.sp)
                    }
                }

                if (couponFeedback.isNotEmpty()) {
                    Text(
                        text = couponFeedback,
                        fontSize = 11.sp,
                        color = if (discountPercent > 0.0) MaterialTheme.colorScheme.primary else Color.Red,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Order summary breakdown Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        SummaryFeeRow("Cart Subtotal", "$${String.format("%.2f", summary.subtotal)}")
                        SummaryFeeRow("VAT / Sales Tax (8%)", "$${String.format("%.2f", summary.tax)}")
                        SummaryFeeRow("Secure Courier Delivery", if (summary.shipping == 0.0) "FREE" else "$${summary.shipping}")
                        if (discountPercent > 0.0) {
                            SummaryFeeRow("Applied Coupon Discount (${(discountPercent * 100).toInt()}%)", "-$${String.format("%.2f", summary.subtotal * discountPercent)}", MaterialTheme.colorScheme.primary)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Payable", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            val payableTotal = summary.grandTotal * (1.0 - discountPercent)
                            Text("$${String.format("%.2f", payableTotal)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Button(
                    onClick = { viewModel.navigateTo("Checkout") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("cart_checkout_button")
                ) {
                    Text("Proceed to Secure Gateway", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun SummaryFeeRow(label: String, valStr: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(text = valStr, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun CheckoutScreen(
    viewModel: MartViewModel,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.cartSummary.collectAsState()
    val discountPercent by viewModel.couponDiscount.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    val paymentMethods = listOf("Visa", "MasterCard", "PayPal", "Stripe", "Google Pay", "Apple Pay")
    var selectedGate by remember { mutableStateOf("Visa") }
    var addressInput by remember { mutableStateOf(user?.deliveryAddress?.takeIf { it.isNotBlank() } ?: "Suite 94A WallStreet, NY 10005") }

    LaunchedEffect(user) {
        val userAddr = user?.deliveryAddress
        if (!userAddr.isNullOrBlank()) {
            addressInput = userAddr
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag("checkout_root")
    ) {
        IconButton(onClick = { viewModel.navigateBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Text(
            text = if (lang == "Español") "Confirmar orden" else if (lang == "বাংলা") "অর্ডার নিশ্চিত করুন" else "SECURE SYSTEM CHECKOUT",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text("Verified SSL Security Gateway Enabled", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(16.dp))

        // Total block
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("GRAND LIQUIDATION TOTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                val finalCost = summary.grandTotal * (1.0 - discountPercent)
                Text("$${String.format("%.2f", finalCost)} USD", fontSize = 22.sp, fontWeight = FontWeight.Black)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Delivery address input
        Text("SHIPPING COURIER ADDRESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        OutlinedTextField(
            value = addressInput,
            onValueChange = { addressInput = it },
            placeholder = { Text("Enter delivery address street") },
            maxLines = 2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("address_field"),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Payment Gateway card grid
        Text("CHOOSE PAYMENT METHOD GATEWAY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(6.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            paymentMethods.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    pair.forEach { gate ->
                        val isSel = selectedGate == gate
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, if (isSel) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .clickable { selectedGate = gate }
                                .testTag("pay_gate_card_$gate")
                        ) {
                            Box(modifier = Modifier.padding(14.dp).fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val icon = when (gate) {
                                        "Google Pay" -> Icons.Default.Circle
                                        "Apple Pay" -> Icons.Default.PhoneIphone
                                        else -> Icons.Default.Payment
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = gate,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (addressInput.trim().isNotEmpty()) {
                    viewModel.checkout(selectedGate, addressInput)
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_order_button")
        ) {
            Icon(Icons.Default.VerifiedUser, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Verify Authentication & Pay", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OrderTrackingScreen(
    viewModel: MartViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val activeIdx by viewModel.activeTrackedOrderId.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()

    val currentOrder = orders.find { it.id == activeIdx } ?: orders.firstOrNull()

    if (currentOrder == null) {
        Box(modifier = modifier.fillMaxSize()) {
            Text("No active order currently tracked.", modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag("order_tracking_root")
    ) {
        Text(
            text = if (lang == "Español") "Rastreo de Orden" else if (lang == "বাংলা") "অর্ডার ট্র্যাকিং" else "COURIER RADAR PATH TRACKING",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )
        Text(text = "Consignment ID: #SVM-${currentOrder.id} • Driver: ${currentOrder.driverName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(14.dp))

        // Customs GPS Simulation radar Map Canvas!
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
        ) {
            // Drawings
            GPSRadarMapCanvas(currentOrder)

            // Current coordinates overlay badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = "GPS: ${String.format("%.4f", currentOrder.driverLat)} : ${String.format("%.4f", currentOrder.driverLng)}",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        // Linear steps progress vertical tracker
        Text("DELIVERY PIPELINE STATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))

        val steps = listOf("Consignment Confirmed", "Wrapped & Packed", "In Transit (Radar Live)", "Delivered Safely")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (stepIdx in 0 until steps.size) {
                val isCompleted = currentOrder.currentStep >= stepIdx
                val stepText = steps[stepIdx]

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stepText,
                            fontSize = 12.sp,
                            fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GPSRadarMapCanvas(order: OrderEntity) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Background grid dots
        val gridStep = 40f
        var x = 0f
        while (x < w) {
            drawCircle(color = Color(0xFF1E293B), radius = 2f, center = Offset(x, 10f))
            drawCircle(color = Color(0xFF1E293B), radius = 2f, center = Offset(x, 70f))
            drawCircle(color = Color(0xFF1E293B), radius = 2f, center = Offset(x, 130f))
            x += gridStep
        }

        // Hub (Origin) Node
        val originalOffset = Offset(80f, 120f)
        drawCircle(color = Color(0xFF38BDF8), radius = 10f, center = originalOffset)
        drawCircle(color = Color(0xFF38BDF8).copy(alpha = 0.3f), radius = 22f, center = originalOffset, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))

        // Consignee destination Node
        val destinationOffset = Offset(w - 80f, 60f)
        drawCircle(color = Color(0xFFFFB700), radius = 10f, center = destinationOffset)
        drawCircle(color = Color(0xFFFFB700).copy(alpha = 0.3f), radius = 22f, center = destinationOffset, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))

        // Mapped Dotted Route Trace
        drawLine(
            color = Color(0xFF475569),
            start = originalOffset,
            end = destinationOffset,
            strokeWidth = 4f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
        )

        // Interpolated Driver Vehicle Marker along progress
        val progress = when(order.currentStep) {
            0 -> 0.0f
            1 -> 0.15f
            2 -> 0.35f
            3 -> {
                // Calculate mapping projection from GPS coordinates
                // order.driverLat moves from 40.735 to 40.7128. Interpolate locally:
                val spanY = 40.7306f - 40.7128f
                val interpolFactor = if (spanY == 0f) 0.5f else ((40.7306f - order.driverLat) / spanY).coerceIn(0f, 1f)
                0.35f + (0.55f * interpolFactor)
            }
            4 -> 1.0f
            else -> 0.3f
        }

        val currentDriverOffset = Offset(
            originalOffset.x + (destinationOffset.x - originalOffset.x) * progress,
            originalOffset.y + (destinationOffset.y - originalOffset.y) * progress
        )

        // Render flashing vehicle point
        drawCircle(color = Color(0xFF22C55E), radius = 12f, center = currentDriverOffset)
        drawCircle(color = Color(0xFF22C55E).copy(alpha = 0.4f), radius = 20f, center = currentDriverOffset, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f))
    }
}

@Composable
fun ChatScreen(
    viewModel: MartViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatHistory.collectAsState()
    val isTyping by viewModel.isChatTyping.collectAsState()
    val recProducts by viewModel.chatRecommendedProducts.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()

    var activeChatCompanion by remember { mutableStateOf(true) } // true = Shopping assistant, false = HelpDesk
    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("chat_assistant_root")
    ) {
        // Toggle tabs for AI modes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = { activeChatCompanion = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeChatCompanion) MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (activeChatCompanion) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .testTag("chat_tab_shopping"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.ShoppingBasket, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Shopping Companion", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { activeChatCompanion = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!activeChatCompanion) MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (!activeChatCompanion) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .testTag("chat_tab_helpdesk"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("HelpDesk Ticket", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Subheader descriptor
        Text(
            text = if (activeChatCompanion) "PREMIUM DISCOVERY BOT POWERED BY GEMINI" else "AUTOMATED CUSTOMER AUTOMATION RECOVERY HELPDESK",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Clear log
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                text = "Clear transcripts",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { viewModel.clearChat() }
                    .padding(bottom = 6.dp)
            )
        }

        // Messages scroll list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("messages_logs_scroller"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { m ->
                val isUser = m.sender == "User"
                val chatBubbleBg = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                val alignSide = if (isUser) Alignment.End else Alignment.Start

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = alignSide
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 260.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = if (isUser) 12.dp else 2.dp,
                                    bottomEnd = if (isUser) 2.dp else 12.dp
                                )
                            )
                            .background(chatBubbleBg)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = m.message,
                            fontSize = 12.sp,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            if (isTyping) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gemini is composing response...", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // Interactive recommended products shelf overlay (ONLY Shopping companion)
        if (activeChatCompanion && recProducts.isNotEmpty()) {
            Text("AI RECOMMENDED CATALOG TARGETS:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(recProducts) { prod ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .width(160.dp)
                            .clickable { viewModel.selectProduct(prod.id) }
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(prod.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("$${prod.price}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // Input bottom tray
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text(if (activeChatCompanion) "Ask about Watch, Jacket or Matcha..." else "Issue shipping, returns dispute...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_bar")
                    .padding(end = 4.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            IconButton(
                onClick = {
                    val prompt = textInput.trim()
                    if (prompt.isNotEmpty()) {
                        viewModel.sendChatMessage(prompt, activeChatCompanion)
                        textInput = ""
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("chat_send_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send text button", modifier = Modifier.size(16.dp))
            }
        }
    }
}
