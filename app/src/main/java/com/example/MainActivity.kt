package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MartViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDark by viewModel.isDarkMode.collectAsState()
            val currentLang by viewModel.currentLanguage.collectAsState()
            val isAdmin by viewModel.isAdminMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val user by viewModel.currentUser.collectAsState()

            var showLanguageDialog by remember { mutableStateOf(false) }
            var showNotificationsAlert by remember { mutableStateOf(false) }
            var showProfileDialog by remember { mutableStateOf(false) }

            MyApplicationTheme(darkTheme = isDark) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("app_scaffold"),
                    topBar = {
                        if (currentScreen != "Login" && currentScreen != "Register") {
                            // Sleek Interface styled header
                            Surface(
                                shadowElevation = 0.dp,
                                color = MaterialTheme.colorScheme.background,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Title Logo BRAND - Clickable Avatar showing user initials
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { showProfileDialog = true }
                                                .testTag("profile_avatar_btn")
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(40.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    val initials = user?.name?.split(Regex("\\s+"))
                                                        ?.mapNotNull { it.firstOrNull() }
                                                        ?.joinToString("")
                                                        ?.uppercase() ?: "SV"
                                                    Text(
                                                        text = if (initials.isNotEmpty()) initials.take(2) else "SV",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = user?.name ?: "Online Mart",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onBackground
                                                )
                                                Text(
                                                    text = if (user != null) "LOGGED IN" else "AI POWERED",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    letterSpacing = 1.sp
                                                )
                                            }
                                            // Mode Indicator
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isAdmin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.padding(start = 8.dp)
                                            ) {
                                                Text(
                                                    text = if (isAdmin) "ADMIN" else "MEMBER",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isAdmin) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                    // Dynamic action controls
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Language toggle
                                        IconButton(
                                            onClick = { showLanguageDialog = true },
                                            modifier = Modifier.size(36.dp).testTag("lang_toggle_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Language,
                                                contentDescription = "Language Dropdown",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Light/Dark mode dynamic toggle
                                        IconButton(
                                            onClick = { viewModel.isDarkMode.value = !isDark },
                                            modifier = Modifier.size(36.dp).testTag("dark_mode_toggle_btn")
                                        ) {
                                            Icon(
                                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                                contentDescription = "Theme Color Control Switch",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Simulated push notifications
                                        Box {
                                            IconButton(
                                                onClick = { showNotificationsAlert = true },
                                                modifier = Modifier.size(36.dp).testTag("notif_bell_btn")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Notifications,
                                                    contentDescription = "Alerts Center",
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            // Red notification dot
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(Color.Red, shape = androidx.compose.foundation.shape.CircleShape)
                                                    .align(Alignment.TopEnd)
                                                    .padding(2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Role Mode Selector switch (Admin Dashboard vs Customer Mall)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isAdmin) "Operating Admin Intelligence mode" else "Shopping Customer Platform",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (currentLang == "Español") "Modo Admin" else if (currentLang == "বাংলা") "অ্যাডমিন মোড" else "Admin Mode",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(end = 6.dp)
                                        )
                                        Switch(
                                            checked = isAdmin,
                                            onCheckedChange = { active ->
                                                viewModel.isAdminMode.value = active
                                                if (active) {
                                                    viewModel.navigateTo("AdminDashboard")
                                                } else {
                                                    viewModel.navigateTo("Home")
                                                }
                                            },
                                            modifier = Modifier.testTag("role_mode_switch")
                                        )
                                    }
                                }
                            }
                        }
                    }
                    },
                    bottomBar = {
                        // Show bottom navigation bar ONLY if not in Admin Mode and not on Auth screens
                        if (!isAdmin && currentScreen != "Login" && currentScreen != "Register") {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.testTag("app_bottom_nav_bar")
                            ) {
                                val shopLabel = if (currentLang == "Español") "Tienda" else if (currentLang == "বাংলা") "মার্কেট" else "Shop"
                                val wishLabel = if (currentLang == "Español") "Favoritos" else if (currentLang == "বাংলা") "পছন্দ" else "Wishlist"
                                val chatLabel = if (currentLang == "Español") "Asistente" else if (currentLang == "বাংলা") "এআই চ্যাট" else "AI Chat"
                                val cartLabel = if (currentLang == "Español") "Cesta" else if (currentLang == "বাংলা") "কার্ট" else "Cart"
                                val specsLabel = if (currentLang == "Español") "Documentos" else if (currentLang == "বাংলা") "ডকুমেন্টস" else "Specs/Docs"

                                NavigationBarItem(
                                    selected = currentScreen == "Home" || currentScreen == "ProductDetails",
                                    onClick = { viewModel.navigateTo("Home") },
                                    icon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                                    label = { Text(shopLabel, fontSize = 10.sp) },
                                    modifier = Modifier.testTag("nav_item_shop")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "Wishlist",
                                    onClick = { viewModel.navigateTo("Wishlist") },
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                                    label = { Text(wishLabel, fontSize = 10.sp) },
                                    modifier = Modifier.testTag("nav_item_wishlist")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "ChatAssistant",
                                    onClick = { viewModel.navigateTo("ChatAssistant") },
                                    icon = { Icon(Icons.Default.Psychology, contentDescription = null) },
                                    label = { Text(chatLabel, fontSize = 10.sp) },
                                    modifier = Modifier.testTag("nav_item_chat")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "Cart" || currentScreen == "Checkout" || currentScreen == "OrderTracking",
                                    onClick = { viewModel.navigateTo("Cart") },
                                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                                    label = { Text(cartLabel, fontSize = 10.sp) },
                                    modifier = Modifier.testTag("nav_item_cart")
                                )
                                NavigationBarItem(
                                    selected = currentScreen == "SystemDocs",
                                    onClick = { viewModel.navigateTo("SystemDocs") },
                                    icon = { Icon(Icons.Default.CloudQueue, contentDescription = null) },
                                    label = { Text(specsLabel, fontSize = 10.sp) },
                                    modifier = Modifier.testTag("nav_item_specs")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(top = 12.dp, start = 14.dp, end = 14.dp)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        when (currentScreen) {
                            "Login" -> LoginScreen(viewModel)
                            "Register" -> RegisterScreen(viewModel)
                            "Home" -> CustomerShopScreen(viewModel)
                            "ProductDetails" -> ProductDetailsScreen(viewModel)
                            "Wishlist" -> WishlistScreen(viewModel)
                            "Cart" -> CartScreen(viewModel)
                            "Checkout" -> CheckoutScreen(viewModel)
                            "OrderTracking" -> OrderTrackingScreen(viewModel)
                            "ChatAssistant" -> ChatScreen(viewModel)
                            "AdminDashboard" -> AdminDashboardScreen(viewModel)
                            "SystemDocs" -> SystemDocsScreen()
                            else -> CustomerShopScreen(viewModel)
                        }
                    }
                }

                // INTERACTIVE MEMBER PROFILE DIALOG
                if (showProfileDialog && user != null) {
                    var editName by remember { mutableStateOf(user?.name ?: "") }
                    var editAddress by remember { mutableStateOf(user?.deliveryAddress ?: "") }
                    var feedbackMessage by remember { mutableStateOf("") }
                    var feedbackSuccess by remember { mutableStateOf(false) }

                    LaunchedEffect(showProfileDialog) {
                        editName = user?.name ?: ""
                        editAddress = user?.deliveryAddress ?: ""
                        feedbackMessage = ""
                    }

                    AlertDialog(
                        onDismissRequest = { showProfileDialog = false },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (currentLang == "Español") "Configuración de Cuenta" else if (currentLang == "বাংলা") "অ্যাকাউন্ট সেটিংস" else "Premium Account Settings",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        text = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "${if (currentLang == "Español") "Identificación" else if (currentLang == "বাংলা") "ইউজার আইডি" else "Account ID"}: ${user?.email}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )

                                if (feedbackMessage.isNotEmpty()) {
                                    Surface(
                                        color = if (feedbackSuccess) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.errorContainer,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = feedbackMessage,
                                            color = if (feedbackSuccess) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onErrorContainer,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = editName,
                                    onValueChange = { editName = it; feedbackMessage = "" },
                                    label = { Text(if (currentLang == "Español") "Nombre" else if (currentLang == "বাংলা") "নাম" else "Full Name") },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("profile_dialog_name_input")
                                )

                                OutlinedTextField(
                                    value = editAddress,
                                    onValueChange = { editAddress = it; feedbackMessage = "" },
                                    label = { Text(if (currentLang == "Español") "Dirección" else if (currentLang == "বাংলা") "ঠিকানা" else "Default Delivery Address") },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("profile_dialog_address_input")
                                )
                            }
                        },
                        confirmButton = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.updateUserProfile(editName, editAddress) { success, msg ->
                                            feedbackSuccess = success
                                            feedbackMessage = msg
                                            if (success) {
                                                showProfileDialog = false
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("profile_dialog_save_btn"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (currentLang == "Español") "Guardar cambios" else if (currentLang == "বাংলা") "পরিবর্তন সংরক্ষণ করুন" else "Save Changes")
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = { showProfileDialog = false }
                                    ) {
                                        Text(if (currentLang == "Español") "Cancelar" else if (currentLang == "বাংলা") "বাতিল" else "Dismiss")
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.logoutUser()
                                            showProfileDialog = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("profile_dialog_logout_btn")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Logout,
                                                contentDescription = "Log out item icon",
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(if (currentLang == "Español") "Cerrar sesión" else if (currentLang == "বাংলা") "লগআউট" else "Log Out")
                                        }
                                    }
                                }
                            }
                        }
                    )
                }

                // LANGUAGE DIALOG SELECTOR
                if (showLanguageDialog) {
                    AlertDialog(
                        onDismissRequest = { showLanguageDialog = false },
                        title = { Text("Select Region/Language", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
                        text = {
                            Column {
                                val languages = listOf("English", "Español", "বাংলা")
                                languages.forEach { l ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                viewModel.currentLanguage.value = l
                                                showLanguageDialog = false
                                            }
                                            .padding(vertical = 12.dp, horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = currentLang == l,
                                            onClick = {
                                                viewModel.currentLanguage.value = l
                                                showLanguageDialog = false
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = l, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showLanguageDialog = false }) { Text("Dismiss") }
                        }
                    )
                }

                // SIMULATED NOTIFICATION PREVIEW DIALOGUE
                if (showNotificationsAlert) {
                    AlertDialog(
                        onDismissRequest = { showNotificationsAlert = false },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Alert Panel Center", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("🚀 20% DISCOUNT ENABLED!", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text("Apply coupone code SVMART20 inside shop checkout carriage to redeem 20% savings on high premium Electronics instantly.", fontSize = 11.sp, lineHeight = 16.sp)
                                    }
                                }
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f))) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("🔮 AI RECOMMENDATION LOG", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                        Text("Your virtual Shopping assistant has categorized tailored matcha tea leaf selections to complement your ambient room humidifier choices.", fontSize = 11.sp, lineHeight = 16.sp)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showNotificationsAlert = false }) { Text("Acknowledge") }
                        }
                    )
                }
            }
        }
    }
}
