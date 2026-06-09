package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class MartRepository(private val dao: MartDao) {

    val allProducts: Flow<List<ProductEntity>> = dao.getAllProductsFlow()
    val wishlist: Flow<List<WishlistItemEntity>> = dao.getWishlistFlow()
    val cartItems: Flow<List<CartItemEntity>> = dao.getCartFlow()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrdersFlow()
    val chatHistory: Flow<List<SupportMessageEntity>> = dao.getAllMessagesFlow()

    suspend fun getProductById(id: Int): ProductEntity? {
        return dao.getProductById(id)
    }

    suspend fun updateProduct(product: ProductEntity) {
        dao.updateProduct(product)
    }

    suspend fun deleteProduct(id: Int) {
        dao.deleteProductById(id)
    }

    suspend fun insertProduct(product: ProductEntity) {
        dao.insertProduct(product)
    }

    suspend fun toggleWishlist(productId: Int) {
        val count = dao.isInWishlist(productId)
        if (count > 0) {
            dao.removeFromWishlist(productId)
        } else {
            dao.addToWishlist(WishlistItemEntity(productId))
        }
    }

    suspend fun isWishlisted(productId: Int): Boolean {
        return dao.isInWishlist(productId) > 0
    }

    suspend fun addToCart(productId: Int, quantity: Int = 1) {
        val existing = dao.getCartItems().find { it.productId == productId }
        if (existing != null) {
            dao.addToCart(CartItemEntity(productId, existing.quantity + quantity))
        } else {
            dao.addToCart(CartItemEntity(productId, quantity))
        }
    }

    suspend fun updateCartQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            dao.removeFromCart(productId)
        } else {
            dao.addToCart(CartItemEntity(productId, quantity))
        }
    }

    suspend fun removeFromCart(productId: Int) {
        dao.removeFromCart(productId)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    suspend fun placeOrder(
        paymentMethod: String,
        address: String,
        totalPrice: Double
    ): Long {
        val drivers = listOf("Sarah Jenkins", "Alex Mercer", "Michael Chang", "Sophia Patel")
        val randomDriver = drivers.random()
        val productsInCart = dao.getCartItems()

        // Reduce stock in database
        for (item in productsInCart) {
            val prod = dao.getProductById(item.productId)
            if (prod != null) {
                val newStock = (prod.stock - item.quantity).coerceAtLeast(0)
                dao.updateProduct(prod.copy(stock = newStock))
            }
        }

        val order = OrderEntity(
            timestamp = System.currentTimeMillis(),
            totalPrice = totalPrice,
            status = "Pending",
            paymentMethod = paymentMethod,
            driverName = randomDriver,
            shippingAddress = address,
            driverLat = 40.7128f, // NYC area center coords simulate delivery tracking
            driverLng = -74.0060f,
            currentStep = 0
        )
        val orderId = dao.insertOrder(order)
        dao.clearCart()
        return orderId
    }

    suspend fun updateOrderStatus(orderId: Int, status: String, step: Int, lat: Float, lng: Float) {
        val orders = dao.getAllOrders()
        val order = orders.find { it.id == orderId }
        if (order != null) {
            dao.updateOrder(
                order.copy(
                    status = status,
                    currentStep = step,
                    driverLat = lat,
                    driverLng = lng
                )
            )
        }
    }

    suspend fun addSupportMessage(sender: String, message: String) {
        dao.insertMessage(
            SupportMessageEntity(
                sender = sender,
                message = message,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearSupportHistory() {
        dao.clearChatHistory()
    }

    suspend fun prepopulateDatabaseIfEmpty() {
        val existing = dao.getAllProducts()
        if (existing.isEmpty()) {
            val preloads = listOf(
                ProductEntity(
                    id = 101,
                    name = "SV Alpha Pro Watch",
                    category = "Electronics",
                    price = 249.99,
                    description = "A premium sapphire-glass smartwatch built with lightweight space-grade titanium. Features localized ECG metrics, dual-band real-time tracking, 14-day battery reserve, and a spectacular Always-On AMOLED panel.",
                    stock = 45,
                    rating = 4.8f,
                    imageUrl = "https://images.unsplash.com/photo-1542496658-e33a6d0d50f6?w=600&auto=format&fit=crop&q=80",
                    reviewsJson = """
                        [
                          {"user":"Rohan Amin","rating":5,"comment":"Incredible build, feels extremely premium compared to other luxury wear."},
                          {"user":"Elena Rostova","rating":4,"comment":"Accurate tracking, beautiful gold bezel detail."}
                        ]
                    """.trimIndent()
                ),
                ProductEntity(
                    id = 102,
                    name = "SV Quantum Headphones",
                    category = "Electronics",
                    price = 189.99,
                    description = "Ultra-premium noise-canceling open-ear wireless headphones packing custom high-fidelity dual-diaphragm transducers. Delivers rich spatial acoustic resolution and standard-setting comfort.",
                    stock = 32,
                    rating = 4.7f,
                    imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                    reviewsJson = """
                        [
                          {"user":"M. Thompson","rating":5,"comment":"The bass control and deep ambient spatial acoustic feedback is spectacular."},
                          {"user":"Nico Lopez","rating":4,"comment":"Slightly heavy but audio quality easily makes up for it."}
                        ]
                    """.trimIndent()
                ),
                ProductEntity(
                    id = 103,
                    name = "SV Core Nexus Station",
                    category = "Electronics",
                    price = 59.99,
                    description = "Industrial 4-in-1 magnetic quick charging console that dynamically directs power delivery up to 100W across devices. Constructed in polished space grey anodized iron metal.",
                    stock = 80,
                    rating = 4.5f,
                    imageUrl = "https://images.unsplash.com/photo-1622445262465-2481c4574875?w=600&auto=format&fit=crop&q=80",
                    reviewsJson = """
                        [
                          {"user":"Sarah G.","rating":5,"comment":"Charges my phone, earbuds and laptop beautifully without heating up."}
                        ]
                    """.trimIndent()
                ),
                ProductEntity(
                    id = 201,
                    name = "SV AeroFit Gold Jacket",
                    category = "Fashion",
                    price = 150.00,
                    description = "Minimalist, luxury windbreaker jacket crafted in high-strength golden-threaded waterproof fabric. Fully breathable, packable, and wind-resistant.",
                    stock = 15,
                    rating = 4.9f,
                    imageUrl = "https://images.unsplash.com/photo-1551488831-00ddcb6c6bd3?w=600&auto=format&fit=crop&q=80",
                    reviewsJson = """
                        [
                          {"user":"Kenji S.","rating":5,"comment":"An absolute masterpiece. Fits perfectly and catches eyes everywhere!"},
                          {"user":"Claire M.","rating":5,"comment":"Incredibly light and weather-proof."}
                        ]
                    """.trimIndent()
                ),
                ProductEntity(
                    id = 202,
                    name = "SV Apex Knit Trainers",
                    category = "Fashion",
                    price = 110.00,
                    description = "High-performance running shoes made of recycled oceanic plastics. Specially designed orthotic sole guarantees cloud-like shock-absorption for all athletic pursuits.",
                    stock = 25,
                    rating = 4.6f,
                    imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80",
                    reviewsJson = """
                        [
                          {"user":"David Baker","rating":4,"comment":"Superb everyday comfort, fantastic springy arch support."}
                        ]
                    """.trimIndent()
                ),
                ProductEntity(
                    id = 203,
                    name = "SV Sol Premium Dress",
                    category = "Fashion",
                    price = 135.00,
                    description = "Breathable tailored summer wear composed of standard biological cotton and flaxen fibers. Elegant structure with soft asymmetric pleating.",
                    stock = 18,
                    rating = 4.7f,
                    imageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=600&auto=format&fit=crop&q=80",
                    reviewsJson = """
                        [
                          {"user":"Aranya Dey","rating":5,"comment":"Exquisite tailoring. Fits gracefully and color holds deep."}
                        ]
                    """.trimIndent()
                ),
                ProductEntity(
                    id = 301,
                    name = "SV Organic Ceremonial Matcha",
                    category = "Grocery",
                    price = 39.99,
                    description = "Direct farm-sourced, micro-milled stoneground organic green tea leaf powder from Uji, Kyoto. Extremely rich L-theanine count and vibrant natural emerald green shade.",
                    stock = 120,
                    rating = 4.9f,
                    imageUrl = "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=600&auto=format&fit=crop&q=80",
                    reviewsJson = """
                        [
                          {"user":"Yuki Haneda","rating":5,"comment":"Pure umami, smooth frothing, zero bitterness. This is real ceremonial grade."}
                        ]
                    """.trimIndent()
                ),
                ProductEntity(
                    id = 302,
                    name = "SV Persian Saffron Threads",
                    category = "Grocery",
                    price = 65.00,
                    description = "Super-negin grade whole Persian red saffron stems selected during peak dawn harvests. Offers an intensely aromatic, state-of-the-art sensory addition.",
                    stock = 50,
                    rating = 4.8f,
                    imageUrl = "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600&auto=format&fit=crop&q=80",
                    reviewsJson = """
                        [
                          {"user":"Fatemeh R.","rating":5,"comment":"Incredibly potent aroma. Just 4 threads colored my whole pilaf rice vibrant yellow."}
                        ]
                    """.trimIndent()
                ),
                ProductEntity(
                    id = 401,
                    name = "SV Zen Ambient Humidifier",
                    category = "Home & Decor",
                    price = 79.99,
                    description = "Quiet ultrasonic dispersion machine that emits high-frequency water vapor while cycling through warm glowing organic lights. Constructed in natural oak timber casing.",
                    stock = 40,
                    rating = 4.7f,
                    imageUrl = "https://images.unsplash.com/photo-1602928321679-560bb453f190?w=600&auto=format&fit=crop&q=80",
                    reviewsJson = """
                        [
                          {"user":"Clara Ost","rating":5,"comment":"Transforms the mood of my studies completely. Absolute stillness."}
                        ]
                    """.trimIndent()
                ),
                ProductEntity(
                    id = 402,
                    name = "SV Aura Orbital Lamp",
                    category = "Home & Decor",
                    price = 120.00,
                    description = "A gravity-defying floating lunar ring that stays suspended over an electromagnetic pedestal. Glows gently in dynamic neutral white or sunset amber hues.",
                    stock = 22,
                    rating = 4.8f,
                    imageUrl = "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=600&auto=format&fit=crop&q=80",
                    reviewsJson = """
                        [
                          {"user":"Liam Neale","rating":5,"comment":"Absolutely mesmerizing technology. Everyone who visits asks about it!"}
                        ]
                    """.trimIndent()
                )
            )
            dao.insertProducts(preloads)
        }
    }

    // ==========================================
    // USER REGISTRATION & AUTHENTICATION METHODS
    // ==========================================

    suspend fun registerUser(email: String, name: String, passwordRaw: String, address: String = ""): Boolean {
        val trimmedEmail = email.trim().lowercase()
        val existing = dao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return false // User already exists
        }
        val newUser = UserEntity(
            email = trimmedEmail,
            name = name.trim(),
            passwordHash = passwordRaw, // Note: For sandbox demonstration, flat comparisons are preferred
            deliveryAddress = address.trim()
        )
        dao.insertUser(newUser)
        return true
    }

    suspend fun authenticateUser(email: String, passwordRaw: String): UserEntity? {
        val trimmedEmail = email.trim().lowercase()
        val user = dao.getUserByEmail(trimmedEmail)
        if (user != null && user.passwordHash == passwordRaw) {
            return user
        }
        return null
    }

    suspend fun updateUserProfile(user: UserEntity) {
        dao.updateUser(user)
    }

    suspend fun prepopulateUsersIfEmpty() {
        val existing = dao.getUserByEmail("customer@sv.com")
        if (existing == null) {
            dao.insertUser(
                UserEntity(
                    email = "customer@sv.com",
                    name = "Jane Doe",
                    passwordHash = "password123",
                    deliveryAddress = "500 Pentonville Rd, London, N1 9JY"
                )
            )
        }
    }
}
