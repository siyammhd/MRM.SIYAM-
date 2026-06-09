package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MartViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = MartRepository(database.martDao())

    // ==========================================
    // 1. APP SETTINGS & LOCALIZATION STATES
    // ==========================================
    var isDarkMode = MutableStateFlow(true)
    var currentLanguage = MutableStateFlow("English") // "English", "Español", "বাংলা"
    var isAdminMode = MutableStateFlow(false)

    // ==========================================
    // USER ACCOUNT & PROFILE CONTROLS
    // ==========================================
    val currentUser = MutableStateFlow<UserEntity?>(null)

    // ==========================================
    // 2. BACKSTACK & NAVIGATION ROUTING
    // ==========================================
    private val navigationStack = mutableListOf<String>()
    val currentScreen = MutableStateFlow("Login") // "Login", "Register", "Home", "ProductDetails", "Wishlist", "Cart", "Checkout", "OrderTracking", "ChatAssistant", "AdminDashboard", "SystemDocs"
    val selectedProductId = MutableStateFlow<Int?>(null)
    val activeTrackedOrderId = MutableStateFlow<Int?>(null)

    // ==========================================
    // 3. CATALOG & SEARCH/FILTER MODULES
    // ==========================================
    val products = repository.allProducts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")

    // High performance search and categorization filter
    val filteredProducts = combine(products, searchQuery, selectedCategory) { list, query, category ->
        list.filter { prod ->
            val matchesSearch = prod.name.contains(query, ignoreCase = true) || 
                                prod.description.contains(query, ignoreCase = true)
            val matchesCategory = (category == "All" || prod.category.equals(category, ignoreCase = true))
            matchesSearch && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ==========================================
    // 4. CART & WISHLIST MODULES
    // ==========================================
    val wishlist = repository.wishlist.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val cartItems = repository.cartItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val orders = repository.allOrders.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Cart pricing summary
    val cartSummary = combine(cartItems, products) { cart, prods ->
        var subtotal = 0.0
        var totalCount = 0
        val itemsMap = prods.associateBy { it.id }

        cart.forEach { item ->
            val p = itemsMap[item.productId]
            if (p != null) {
                subtotal += p.price * item.quantity
                totalCount += item.quantity
            }
        }
        val tax = subtotal * 0.08
        val shipping = if (subtotal > 100.0 || totalCount == 0) 0.0 else 15.0
        val grandTotal = subtotal + tax + shipping

        CartSummaryData(subtotal, tax, shipping, grandTotal, totalCount)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartSummaryData())

    // ==========================================
    // 5. CHAT & AI CUSTOMER ENGINE
    // ==========================================
    val chatHistory = repository.chatHistory.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val isChatTyping = MutableStateFlow(false)
    val chatRecommendedProducts = MutableStateFlow<List<ProductEntity>>(emptyList())

    // ==========================================
    // 6. BUSINESS ANALYTICS & AI FORECAST STATE
    // ==========================================
    val aiForecastText = MutableStateFlow("")
    val isForecastLoading = MutableStateFlow(false)

    // Coupon states
    val couponCode = MutableStateFlow("")
    val couponDiscount = MutableStateFlow(0.0) // percentage e.g., 0.15 = 15%
    val couponMessage = MutableStateFlow("")

    private var trackingJob: Job? = null

    init {
        viewModelScope.launch {
            repository.prepopulateDatabaseIfEmpty()
            repository.prepopulateUsersIfEmpty()
        }
    }

    // ==========================================
    // NAVIGATION CONTROLLER
    // ==========================================
    fun navigateTo(screen: String) {
        navigationStack.add(currentScreen.value)
        currentScreen.value = screen
    }

    fun navigateBack() {
        if (navigationStack.isNotEmpty()) {
            currentScreen.value = navigationStack.removeAt(navigationStack.size - 1)
        } else {
            currentScreen.value = "Home"
        }
    }

    fun selectProduct(productId: Int) {
        selectedProductId.value = productId
        navigateTo("ProductDetails")
    }

    fun setCategory(category: String) {
        selectedCategory.value = category
    }

    // ==========================================
    // USER REGISTRATION & AUTHENTICATION ACTIONS
    // ==========================================
    fun registerUser(email: String, name: String, pass: String, address: String, onResult: (Boolean, String) -> Unit) {
        if (email.isBlank() || name.isBlank() || pass.isBlank()) {
            onResult(false, "Please fill out all fields.")
            return
        }
        viewModelScope.launch {
            val success = repository.registerUser(email, name, pass, address)
            if (success) {
                onResult(true, "Successfully registered! You can now log in.")
            } else {
                onResult(false, "Email is already registered.")
            }
        }
    }

    fun loginUser(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onResult(false, "Please fill out all fields.")
            return
        }
        viewModelScope.launch {
            val user = repository.authenticateUser(email, pass)
            if (user != null) {
                currentUser.value = user
                onResult(true, "Welcome back, ${user.name}!")
            } else {
                onResult(false, "Invalid email or password.")
            }
        }
    }

    fun logoutUser() {
        currentUser.value = null
        navigationStack.clear()
        currentScreen.value = "Login"
    }

    fun updateUserProfile(name: String, address: String, onResult: (Boolean, String) -> Unit) {
        val user = currentUser.value
        if (user == null) {
            onResult(false, "No active user session")
            return
        }
        if (name.isBlank()) {
            onResult(false, "Name cannot be empty")
            return
        }
        viewModelScope.launch {
            val updatedUser = user.copy(name = name.trim(), deliveryAddress = address.trim())
            repository.updateUserProfile(updatedUser)
            currentUser.value = updatedUser
            onResult(true, "Profile updated successfully")
        }
    }

    // ==========================================
    // WISHLIST & CART ACTION METHODS
    // ==========================================
    fun toggleWishlist(productId: Int) {
        viewModelScope.launch {
            repository.toggleWishlist(productId)
        }
    }

    fun addToCart(productId: Int, qty: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(productId, qty)
        }
    }

    fun updateCartQuantity(productId: Int, qty: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, qty)
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    // Apply corporate coupons
    fun validateCoupon(code: String) {
        if (code.equals("SVMART20", ignoreCase = true)) {
            couponDiscount.value = 0.20
            couponMessage.value = "20% Discount Applied!"
        } else if (code.equals("GEMINI15", ignoreCase = true)) {
            couponDiscount.value = 0.15
            couponMessage.value = "15% AI-Bonus Applied!"
        } else if (code.isNotEmpty()) {
            couponDiscount.value = 0.0
            couponMessage.value = "Invalid or Expired Coupon"
        }
    }

    // Checkout execution
    fun checkout(paymentMethod: String, address: String) {
        viewModelScope.launch {
            val total = cartSummary.value.grandTotal * (1.0 - couponDiscount.value)
            val orderId = repository.placeOrder(paymentMethod, address, total)
            activeTrackedOrderId.value = orderId.toInt()
            couponCode.value = ""
            couponDiscount.value = 0.0
            couponMessage.value = ""
            navigateTo("OrderTracking")
            startLiveTrackingSimulation(orderId.toInt())
        }
    }

    // ==========================================
    // LOGISTICS SIMULATION (REAL-TIME TRACKING)
    // ==========================================
    fun startLiveTrackingSimulation(orderId: Int) {
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            // Start Coordinates (Hub NYC Area)
            var lat = 40.7306f
            var lng = -73.9352f

            // Target destination coordinates for user
            val destLat = 40.7128f
            val destLng = -74.0060f

            // Phase 1: Preparing (Step 0)
            repository.updateOrderStatus(orderId, "Preparing Order", 1, lat, lng)
            delay(4000)

            // Phase 2: Packing Ready (Step 1)
            repository.updateOrderStatus(orderId, "Order Prepared & Cleaned", 1, lat, lng)
            delay(4000)

            // Phase 3: Shipped / Dispatched (Step 2)
            lat = 40.7350f
            lng = -73.9420f
            repository.updateOrderStatus(orderId, "Out for Dispatch (Sarah Mercer)", 2, lat, lng)

            // Dynamic movement ticks
            val ticks = 5
            for (i in 1..ticks) {
                delay(3000)
                val progression = i.toFloat() / ticks.toFloat()
                val currentLat = lat + (destLat - lat) * progression
                val currentLng = lng + (destLng - lng) * progression
                repository.updateOrderStatus(
                    orderId = orderId,
                    status = "In Transit - Approaching Destination ($i/$ticks)",
                    step = 3,
                    lat = currentLat,
                    lng = currentLng
                )
            }

            // Phase 4: Delivered (Step 4)
            repository.updateOrderStatus(orderId, "Delivered Safely", 4, destLat, destLng)
        }
    }

    // ==========================================
    // CHATBOT INTERACTIVE METHODS
    // ==========================================
    fun sendChatMessage(input: String, isShoppingCompanion: Boolean) {
        val msg = input.trim()
        if (msg.isEmpty()) return

        viewModelScope.launch {
            // 1. Save user text
            repository.addSupportMessage("User", msg)
            isChatTyping.value = true

            // Formulate system directive based on mode
            val systemPrompt = if (isShoppingCompanion) {
                val catalogList = products.value.joinToString { "[ID:${it.id}] ${it.name} - $${it.price} (${it.category})" }
                """
                You are SV Online Mart's conversational AI Shopping Companion. Help the user discover products of their choice, give smart luxury suggestions, explain specs, and advise matching collections. Ensure you keep advice concise, professional, and visually formatted.
                Active Catalog: $catalogList
                IMPORTANT: If you suggest specific items in the list, state their name and ID using the format: "Product ID: <ID>". This enables our app to automatically link they to the cart.
                """.trimIndent()
            } else {
                """
                You are SV Online Mart's Logistics & Support Helpdesk bot. Help customers troubleshoot delivery coordinates, standard shipping deadlines (1-3 business days), refund claims (full 30-day window), or corporate security rules. Provide reassurance with professional, crisp answers. Keep answers limited to 2-3 short, clean sentences.
                """.trimIndent()
            }

            // 2. Fetch history
            val currentHistory = chatHistory.value

            // 3. Query Gemini
            val reply = GeminiService.queryGemini(systemPrompt, msg, currentHistory)

            isChatTyping.value = false

            if (reply == "API_KEY_MISSING_ERROR") {
                val fallbackReply = if (isShoppingCompanion) {
                    "Thanks for asking! I'm operating in Sandbox mode since no Gemini API Key is loaded in the Secrets panel. Let me recommend: the **SV Alpha Pro Watch** ($249.99) with advanced sapphire glass, or our premium farm-sourced **SV Organic Ceremonial Matcha** ($39.99)!"
                } else {
                    "I am in Sandbox support mode. Standard shipping takes 1-3 business days, and we offer a comprehensive 30-day money-back refund guarantee with free home parcel pickups!"
                }
                repository.addSupportMessage("AI_Assistant", fallbackReply)
            } else {
                repository.addSupportMessage("AI_Assistant", reply)

                // Try to parse recommended product IDs from reply
                if (isShoppingCompanion) {
                    val foundIds = mutableListOf<ProductEntity>()
                    products.value.forEach { product ->
                        if (reply.contains("Product ID: ${product.id}", ignoreCase = true) || 
                            reply.contains(product.name, ignoreCase = true)) {
                            foundIds.add(product)
                        }
                    }
                    chatRecommendedProducts.value = foundIds
                }
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearSupportHistory()
            repository.addSupportMessage("AI_Assistant", "Hello! I am your AI Shopping Companion at SV Online Mart. Ask me anything about our elegant collection of electronics, premium clothing apparel, organic grocery harvests, or gravity-defying home decor. If you want support regarding orders or delivery, toggle to Support helpdesk mode!")
        }
    }

    // ==========================================
    // ADMIN DASHBOARD CRUD & INVENTORY OPERATIONS
    // ==========================================
    fun adminAddNewProduct(name: String, category: String, price: Double, description: String, stock: Int) {
        viewModelScope.launch {
            val newId = (products.value.maxOfOrNull { it.id } ?: 100) + 1
            val p = ProductEntity(
                id = newId,
                name = name,
                category = category,
                price = price,
                description = description,
                stock = stock,
                rating = 5.0f,
                imageUrl = "",
                reviewsJson = "[]"
            )
            repository.insertProduct(p)
        }
    }

    fun adminDeleteProduct(productId: Int) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    fun adminUpdateStock(productId: Int, newStock: Int) {
        viewModelScope.launch {
            val prod = repository.getProductById(productId)
            if (prod != null) {
                repository.updateProduct(prod.copy(stock = newStock))
            }
        }
    }

    // ==========================================
    // AI SALES FORECAST / PREDICTION SERVICES
    // ==========================================
    fun generateAIForecast(analysisType: String) {
        viewModelScope.launch {
            isForecastLoading.value = true
            val catalogSummary = products.value.joinToString { "${it.name} (Stock: ${it.stock}, Price: $${it.price})" }
            val systemPrompt = "You are the Senior Business intelligence strategist for SV Online Mart. Analyze raw catalog levels and render a highly sophisticated data analyst document."
            val userPrompt = when (analysisType) {
                "Sales" -> "Generate a strategic 12-month Sales Forecast report. Include predicted peak sales quarters, product categories driving high margin conversion, and a formatted table of expected revenue levels. Active inventory catalog: $catalogSummary"
                "Inventory" -> "Analyze our current inventory stock: $catalogSummary. Identify products at risk of stocking out, predict restocking milestones, and recommend purchase volumes to optimize holding cost."
                "Behavior" -> "Generate an AI Customer Behavior Analysis. Predict purchasing frequency indices, analyze modern dynamic user engagement across Tech vs Groceries, and outline retention action plans."
                else -> "Generate a standard business intelligence summary."
            }

            val reply = GeminiService.queryGemini(systemPrompt, userPrompt, emptyList())
            isForecastLoading.value = false

            if (reply == "API_KEY_MISSING_ERROR") {
                val fallbackForecast = when (analysisType) {
                    "Sales" -> """
                        ### 🔮 AI Sales Forecast (Sandbox Mode)
                        *Generated based on local statistical regressions*
                        
                        #### 1. Expected Sales Growth & Seasonality
                        - **Q3 Forecasted Revenue:** $450,000 (Expected rise of 24% driven by Apex knit runner launches and back-to-school accessories).
                        - **Q4 Holiday Projections:** $780,000 (Estimated peak during Black Friday weekend; Watch and Headphones represent high-yielding category drivers).
                        
                        #### 2. Category Margin Drivers
                        - **Electronics:** Expected to represent **62%** of gross profit margins.
                        - **Home Decor:** Highly resilient growth vectors (orbital floating lamps lead decorative conversions).
                        
                        #### 3. Strategic Action Plan
                        1. Secure early bulk stocking of **SV Quantum Headphones** before mid-November to mitigate logistics congestion.
                        2. Cross-bundle Matcha harvests with Zen humidifiers with custom discount coupons.
                    """.trimIndent()
                    "Inventory" -> """
                        ### 📦 AI Inventory & Stock Predictor (Sandbox Mode)
                        *Analytical alert diagnostics*
                        
                        #### 1. Under-Stock Risk Warnings (Out of Stock alert)
                        - **SV AeroFit Gold Jacket:** Active Stock: **15 units** remaining. Predicted stock-out milestone: **8 days** based on high conversion velocities.
                        - **SV Aura Orbital Lamp:** Active Stock: **22 units** remaining. Predicted velocity exhaustion: **14 days**.
                        
                        #### 2. Reorder Advice
                        - Reorder **60 units** of AeroFit Gold jackets immediately to secure wholesale price retention.
                        - Current holdings of **Matcha tea** are high (120 units) — pause replenishment schedules until August cycle.
                    """.trimIndent()
                    else -> """
                        ### 👥 AI Customer Behavior Report (Sandbox Mode)
                        *Demographic engagement analytics*
                        
                        #### 1. User Engagement Indices
                        - Average session duration is exceptionally high inside the **AI Shopping Assistant (4.2 minutes)**.
                        - Chat-to-Cart conversion rate: **28.4%** across high-tier premium Electronics.
                        
                        #### 2. Behavioral Persona Segments
                        - **Elite Tech Collectors (45%):** Value premium metal casings and smart metrics. Highly responsive to customized coupons.
                        - **Organic Wellness Enthusiasts (35%):** Predictable monthly tea repeat-purchases holding high retention margins.
                    """.trimIndent()
                }
                aiForecastText.value = fallbackForecast
            } else {
                aiForecastText.value = reply
            }
        }
    }
}

// Data holder for cart values
data class CartSummaryData(
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shipping: Double = 0.0,
    val grandTotal: Double = 0.0,
    val totalCount: Int = 0
)
