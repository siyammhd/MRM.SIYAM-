package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProductEntity
import com.example.ui.MartViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: MartViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("Analytics") } // "Analytics", "Inventory", "AI_Broker"

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_root")
    ) {
        // Tab selector bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            val tabs = listOf("Analytics" to Icons.Default.BarChart, "Inventory" to Icons.Default.Category, "AI_Broker" to Icons.Default.Psychology)
            tabs.forEach { tab ->
                val isSelected = activeTab == tab.first
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { activeTab = tab.first }
                        .padding(vertical = 12.dp)
                        .testTag("admin_tab_${tab.first}")
                ) {
                    Icon(
                        imageVector = tab.second,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tab.first,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Active screen contents
        AnimatedContent(
            targetState = activeTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_fade"
        ) { tab ->
            when (tab) {
                "Analytics" -> AdminAnalyticsPanel(viewModel)
                "Inventory" -> AdminInventoryPanel(viewModel)
                "AI_Broker" -> AdminAIIntelligencePanel(viewModel)
            }
        }
    }
}

@Composable
fun AdminAnalyticsPanel(viewModel: MartViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "E-COMMERCE METRICS DIRECTORY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Micro total summaries row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AnalyticsQuickCard("Gross Sale", "$43,921.00", "+18.4% YoY", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            AnalyticsQuickCard("Active Order", "45 Shipments", "+5 New Out", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Chart 1: Bar Chart
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Monthly Sales Chart (USD)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                SalesBarChartCanvas()
            }
        }

        // Chart 2: Category Share Donut
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Volume Breakdown by Category", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(0.5f), contentAlignment = Alignment.Center) {
                        CategoryDonutChartCanvas()
                    }
                    Column(
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(start = 12.dp)
                    ) {
                        CategoryLegendRow("Electronics", MaterialTheme.colorScheme.primary, "55%")
                        CategoryLegendRow("Fashion", MaterialTheme.colorScheme.tertiary, "25%")
                        CategoryLegendRow("Grocery", MaterialTheme.colorScheme.secondary, "15%")
                        CategoryLegendRow("Home", Color(0xFFB69DF8), "5%")
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsQuickCard(title: String, valStr: String, trend: String, accent: Color, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = valStr, fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = trend, fontSize = 10.sp, color = accent, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CategoryLegendRow(name: String, color: Color, percentage: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = name, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), modifier = Modifier.weight(1f))
        Text(text = percentage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// Custom canvas bar chart
@Composable
fun SalesBarChartCanvas() {
    val barValues = listOf(45f, 60f, 90f, 130f, 185f, 250f) // Represents thousands of USD
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
    val barColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 8.dp)
    ) {
        val totalWidth = size.width
        val totalHeight = size.height
        val barCount = barValues.size
        val gap = 20f
        val calculatedBarWidth = (totalWidth - (gap * (barCount - 1))) / barCount

        val maxValue = 300f

        for (i in 0 until barCount) {
            val hValue = barValues[i]
            val barHeight = (hValue / maxValue) * totalHeight
            val startX = i * (calculatedBarWidth + gap)
            val startY = totalHeight - barHeight

            // Draw shadow bar
            drawRoundRect(
                color = barColor.copy(alpha = 0.05f),
                topLeft = Offset(startX, 0f),
                size = Size(calculatedBarWidth, totalHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )

            // Draw actual styled gradient bar
            val brush = Brush.verticalGradient(
                colors = listOf(barColor, barColor.copy(alpha = 0.5f)),
                startY = startY,
                endY = totalHeight
            )
            drawRoundRect(
                brush = brush,
                topLeft = Offset(startX, startY),
                size = Size(calculatedBarWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )
        }
    }
    Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        months.forEach { m ->
            Text(text = m, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

// Custom canvas pie chart
@Composable
fun CategoryDonutChartCanvas(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .size(110.dp)
    ) {
        val w = size.width
        val h = size.height
        val radius = size.minDimension / 2
        val strokeW = 28f

        // Portions: 55%, 25%, 15%, 5%
        val sweep1 = 360f * 0.55f
        val sweep2 = 360f * 0.25f
        val sweep3 = 360f * 0.15f
        val sweep4 = 360f * 0.05f

        drawArc(
            color = Color(0xFF6750A4),
            startAngle = -90f,
            sweepAngle = sweep1,
            useCenter = false,
            style = Stroke(width = strokeW),
            topLeft = Offset((w - radius * 2)/2, (h - radius * 2)/2),
            size = Size(radius * 2, radius * 2)
        )
        drawArc(
            color = Color(0xFFD0BCFF),
            startAngle = -90f + sweep1,
            sweepAngle = sweep2,
            useCenter = false,
            style = Stroke(width = strokeW),
            topLeft = Offset((w - radius * 2)/2, (h - radius * 2)/2),
            size = Size(radius * 2, radius * 2)
        )
        drawArc(
            color = Color(0xFFE8DEF8),
            startAngle = -90f + sweep1 + sweep2,
            sweepAngle = sweep3,
            useCenter = false,
            style = Stroke(width = strokeW),
            topLeft = Offset((w - radius * 2)/2, (h - radius * 2)/2),
            size = Size(radius * 2, radius * 2)
        )
        drawArc(
            color = Color(0xFFB69DF8),
            startAngle = -90f + sweep1 + sweep2 + sweep3,
            sweepAngle = sweep4,
            useCenter = false,
            style = Stroke(width = strokeW),
            topLeft = Offset((w - radius * 2)/2, (h - radius * 2)/2),
            size = Size(radius * 2, radius * 2)
        )
    }
}

@Composable
fun AdminInventoryPanel(viewModel: MartViewModel) {
    val productsList by viewModel.products.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    var pName by remember { mutableStateOf("") }
    var pCategory by remember { mutableStateOf("Electronics") }
    var pPrice by remember { mutableStateOf("") }
    var pStock by remember { mutableStateOf("") }
    var pDesc by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("STOCKS CATALOG CONTROL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.minimumInteractiveComponentSize().testTag("admin_add_product_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Item", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(productsList) { prod ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(0.6f)) {
                            Text(text = prod.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Category: ${prod.category} • Price: $${prod.price}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Stock Index: ${prod.stock}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (prod.stock < 10) Color.Red else MaterialTheme.colorScheme.primary)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(0.4f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Add stock
                            IconButton(
                                onClick = { viewModel.adminUpdateStock(prod.id, prod.stock + 10) },
                                modifier = Modifier.testTag("admin_restock_${prod.id}")
                            ) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Add Stock", tint = MaterialTheme.colorScheme.primary)
                            }
                            // Delete
                            IconButton(
                                onClick = { viewModel.adminDeleteProduct(prod.id) },
                                modifier = Modifier.testTag("admin_delete_${prod.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Product", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Deploy New Product", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth()) {
                    OutlinedTextField(value = pName, onValueChange = { pName = it }, label = { Text("Product Label") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pPrice, onValueChange = { pPrice = it }, label = { Text("Price (USD)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pStock, onValueChange = { pStock = it }, label = { Text("Initial Stock Count") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pDesc, onValueChange = { pDesc = it }, label = { Text("Detailed Specifications") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Category Selector", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Electronics", "Fashion", "Grocery", "Home & Decor").forEach { cat ->
                            FilterChip(
                                selected = pCategory == cat,
                                onClick = { pCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val priceNum = pPrice.toDoubleOrNull() ?: 10.0
                        val stockNum = pStock.toIntOrNull() ?: 20
                        if (pName.isNotEmpty()) {
                            viewModel.adminAddNewProduct(pName, pCategory, priceNum, pDesc, stockNum)
                            pName = ""
                            pPrice = ""
                            pStock = ""
                            pDesc = ""
                            pCategory = "Electronics"
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("admin_confirm_add_product")
                ) {
                    Text("Deploy Item")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Abort") }
            }
        )
    }
}

@Composable
fun AdminAIIntelligencePanel(viewModel: MartViewModel) {
    val aiResponse by viewModel.aiForecastText.collectAsState()
    val isTyping by viewModel.isForecastLoading.collectAsState()

    var selectedModule by remember { mutableStateOf("Sales") } // "Sales", "Inventory", "Behavior"

    Column(modifier = Modifier.fillMaxSize()) {
        Text("GEMINI BUSINESS SYSTEM INTELLIGENCE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))

        // Analysis buttons row with 48dp metrics
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            val modules = listOf("Sales" to "Sales Forecast", "Inventory" to "Stock Prediction", "Behavior" to "User Analysis")
            modules.forEach { mod ->
                val isSel = selectedModule == mod.first
                Button(
                    onClick = {
                        selectedModule = mod.first
                        viewModel.generateAIForecast(mod.first)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("ai_forecast_tab_${mod.first}")
                ) {
                    Text(text = mod.second, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Response viewer terminal pane
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                if (isTyping) {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Gemini Business Engine compiling metrics...", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    val terminalText = aiResponse.ifEmpty { "Select an analysis module above. Gemini will formulate a comprehensive analytical report by modeling the live inventory levels." }
                    LazyColumn(modifier = Modifier.fillMaxSize().testTag("admin_forecast_scroller")) {
                        item {
                            Text(
                                text = terminalText,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
