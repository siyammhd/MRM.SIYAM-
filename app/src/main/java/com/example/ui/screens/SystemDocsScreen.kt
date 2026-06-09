package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SystemDocsScreen(modifier: Modifier = Modifier) {
    val docCategories = listOf(
        "Complete System Architecture" to Icons.Default.Cloud,
        "Entity Relation (ER) Diagram" to Icons.Default.Share,
        "Database Schema (PostgreSQL)" to Icons.Default.Storage,
        "High-Performance API Specification" to Icons.Default.Api,
        "Deployment Guide (GCP Cloud)" to Icons.Default.ElectricalServices,
        "Security Strategy & Encryption" to Icons.Default.Security,
        "Testing Strategy" to Icons.Default.BugReport,
        "Cost Estimation Models" to Icons.Default.Analytics,
        "Scalability Plan (100k+ Users)" to Icons.Default.TrendingUp
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxSize()
            .testTag("system_docs_root")
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left sidebar document index with 48dp target buttons
            Column(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "SPECIFICATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 8.dp)
                )

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(docCategories.size) { i ->
                        val item = docCategories[i]
                        val isSel = selectedIndex == i
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { selectedIndex = i }
                                .padding(vertical = 12.dp, horizontal = 12.dp)
                                .testTag("doc_nav_pill_$i")
                        ) {
                            Icon(
                                imageVector = item.second,
                                contentDescription = null,
                                tint = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.first,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // Right content terminal reader
            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("doc_reader_scroller")
                ) {
                    item {
                        when (selectedIndex) {
                            0 -> SystemArchitectureContent()
                            1 -> ERDiagramContent()
                            2 -> DatabaseSchemaContent()
                            3 -> APISpecificContent()
                            4 -> DeploymentGuideContent()
                            5 -> SecurityStrategyContent()
                            6 -> TestingStrategyContent()
                            7 -> CostEstimationContent()
                            8 -> ScalabilityContent()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodeBlock(code: String) {
    Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Terminal JSON/SQL Block",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = Color(0xFFFFB700).copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = code,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Color(0xFF38BDF8),
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun DocSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun DocSubHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
    )
}

@Composable
fun DocParagraph(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
        lineHeight = 18.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SystemArchitectureContent() {
    DocSectionHeader("SV Online Mart - Cloud Architecture")
    DocParagraph(
        "SV Online Mart leverages a high-availability, microservices-driven framework " +
        "deployed on Google Cloud Platform (GCP). It is engineered to process up to 100,000+ " +
        "concurrent requests with extremely low latency boundaries."
    )
    DocSubHeader("1. Key Components & Traffic Flow")
    DocParagraph("- CLIENT ELEVATION: Web clients (Next.js SSR via Vercel) and mobile devices (Native Android/Kotlin & Flutter) communicate through Google Cloud HTTPS Load Balancing.")
    DocParagraph("- API INGESTION GATEWAY: Google Cloud Apigee serves as our API Management Layer managing rate-limiting, edge validations, and telemetry hooks.")
    DocParagraph("- IDENTITY PROVIDER: Google Identity Platform provides OAuth2/JWT processing, federated authentications (Google, Facebook, Apple), and two-factor compliance.")
    DocParagraph("- REPLICA BACKEND SERVICES: Specialized container pods orchestrated across Google Kubernetes Engine (GKE) manage checkout pipelines, inventory matching, and catalog queries.")

    DocSubHeader("2. Physical Network Architectural Layout")
    CodeBlock("""
[Clients] -> Vercel (Next.js) & Android / iOS
   |
   +--> HTTPS (SSL Terminated)
   v
[GCP CDN & Cloud Load Balancer]
   |
   +--> /api/* Route Rules
   v
[Apigee API Gateway] (JWT & Token Audits)
   |
   +--------------------+---------------------+
   |                    |                     |
   v                    v                     v
[GKE User Pod]    [GKE Inventory]     [GKE AI Assistant]
   |                    |                     |
   +-----+--------------+---------------------+
         |
         v
[Cloud Spanner / Cloud SQL PostgreSQL] <== Bi-directional Sync
         |
         v
[Vertex AI & Gemini API Model Hub]
    """.trimIndent())
}

@Composable
fun ERDiagramContent() {
    DocSectionHeader("Entity Relationship Modeling")
    DocParagraph(
        "The relational database utilizes a highly normalized schema mapping user transactions, " +
        "automated cart statuses, specific inventory SKU trackers, and historic support ticket transcripts."
    )
    DocSubHeader("Logical Entity Map")
    CodeBlock("""
  +------------------+             +-----------------+
  |     USERS        |             |    PRODUCTS     |
  +------------------+             +-----------------+
  | PK: user_id      |1           *| PK: product_id  |
  |     email        |---[cart]--- |     name        |
  |     role_id      |             |     stock_level |
  +------------------+             +-----------------+
           |                                |
           | 1                              | 1
           |                                |
           | *                              | *
  +------------------+             +-----------------+
  |     ORDERS       |             |  ORDER_ITEMS    |
  +------------------+             +-----------------+
  | PK: order_id     |1           *| PK: item_id     |
  | FK: user_id      |------------>| FK: order_id    |
  |     total_cost   |             | FK: product_id  |
  +------------------+             +-----------------+
    """.trimIndent())
    DocSubHeader("Structural Rules")
    DocParagraph("1. User-to-Orders: 1-to-many relationship. Enforced with cascading deletion locks.")
    DocParagraph("2. Products-to-OrderItems: 1-to-many. Stores fixed historical transactional prices to defend against retrofitted product catalog updates.")
}

@Composable
fun DatabaseSchemaContent() {
    DocSectionHeader("PostgreSQL Schema Declarations")
    DocParagraph(
        "To operate at massive transactional scales safely, we leverage a dual PostgreSQL deployment " +
        "incorporating database connection partitioning, composite index arrays, and strict foreign constraints."
    )
    DocSubHeader("PostgreSQL DDL Statements")
    CodeBlock("""
-- Tables creation scripts (PostgreSQL)

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    role VARCHAR(20) DEFAULT 'customer' CHECK (role IN ('customer', 'admin')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category_id INT REFERENCES categories(id),
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    description TEXT,
    rating NUMERIC(2,1) DEFAULT 5.0
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    total_price DECIMAL(12,2) NOT NULL,
    payment_method VARCHAR(50),
    status VARCHAR(30) DEFAULT 'Pending',
    driver_name VARCHAR(100),
    latitude NUMERIC(9,6),
    longitude NUMERIC(9,6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
    """.trimIndent())
}

@Composable
fun APISpecificContent() {
    DocSectionHeader("RESTful HTTPS API Specification")
    DocParagraph(
        "All client actions are mediated over standard security-hardened TLS 1.3 endpoints. " +
        "Authentication is validated via asymmetric RS256 JWT tokens."
    )
    DocSubHeader("Core Endpoint Specifications")
    CodeBlock("""
POST /api/v1/auth/login
Request: { "email": "admin@sv.com", "password": "SecurePassword15!" }
Response: { "status": "success", "token": "eyJhbGciOiJSUzI1Ni..." }

GET /api/v1/products?category=Electronics&search=Watch
Headers: Authorization: Bearer <JWT_TOKEN>
Response: {
  "status": "success",
  "data": [
    { "id": 101, "name": "SV Alpha Watch", "price": 249.99, "stock": 45 }
  ]
}

POST /api/v1/orders/checkout
Headers: Authorization: Bearer <JWT_TOKEN>
Request: { "address": "12 Main St, NY", "payment_method": "Visa" }
Response: { "status": "success", "order_id": 9042 }
    """.trimIndent())
}

@Composable
fun DeploymentGuideContent() {
    DocSectionHeader("GCP Cloud Deployment Guide")
    DocParagraph(
        "Deployments are completely automated inside Google Cloud using Terraform " +
        "for Infrastructure as Code and GitHub Actions for CI/CD container distribution."
    )
    DocSubHeader("Strategic Deployment Sequence")
    DocParagraph("1. TERRAFORM INITIALIZE: Run scripts to formulate private Google Virtual Private Cloud (VPC), GKE clusters, and Cloud Armor firewalls.")
    DocParagraph("2. CONFIGURE SECRETS: Write dynamic Google Cloud Secret Manager keys containing Vertex AI variables and PG keys.")
    DocParagraph("3. BUILD CONTAINER GKE ARCHITECTURE:")
    CodeBlock("""
docker build -t gcr.io/sv-online-mart/backend:v1.0 .
docker push gcr.io/sv-online-mart/backend:v1.0
gcloud container clusters get-credentials sv-gke-cluster --region us-central1
kubectl apply -f k8s/deployment.yaml
    """.trimIndent())
}

@Composable
fun SecurityStrategyContent() {
    DocSectionHeader("Enterprise Security & Encryption Strategy")
    DocParagraph("Security is structured directly into our core framework layouts to protect client asset data.")
    DocSubHeader("1. Encrypted Storage Channels")
    DocParagraph("- REST ENCRYPTION: All databases use AES-256 Cloud Customer-Managed Encryption Keys (CMEK) via Google Key Management Service.")
    DocParagraph("- TRANSIT ENCRYPTION: Strict enforce-only HTTPS TLS 1.3 with automated HSTS header bindings.")
    DocSubHeader("2. Two-Factor Authentication (2FA) Loop")
    DocParagraph("- Standard multi-factor authentication validated during checkout and administrator logins via Google Authenticator TOTP tokens.")
}

@Composable
fun TestingStrategyContent() {
    DocSectionHeader("Testing Strategy Matrix")
    DocParagraph(
        "A rigorous multi-tiered testing plan protects the build against regression failures " +
        "and latency spikes at maximum customer loads."
    )
    DocSubHeader("Integrated Testing Protocols")
    DocParagraph("- LOCAL JVM UNIT TESTING: MockK + JUnit5 models critical cart logic and business rules.")
    DocParagraph("- INTEGRATED WORKFLOW TESTING: Robolectric tests verified local SQLite / Room migrations without emulator overhead.")
    DocParagraph("- PERFORMANCE TESTING: Gatling framework models 10,000 concurrent checkout requests to evaluate API gate latencies.")
}

@Composable
fun CostEstimationContent() {
    DocSectionHeader("GCP Cloud Cost Matrix (Monthly)")
    DocParagraph(
        "Estimated cloud expenses calculated to handle 100,000+ dynamic monthly consumers " +
        "with generous safety headroom allocations."
    )
    DocSubHeader("Calculated Hardware Allocations")
    CodeBlock("""
| GCP Component               | Hardware Spec                       | Cost (USD) |
|-----------------------------|-------------------------------------|------------|
| Google Kubernetes GKE       | 3 Nodes (e2-standard-4 instances)   | ${"$"}345.00   |
| Cloud SQL (PostgreSQL)      | db-custom-4-16288 (HA replication)  | ${"$"}280.00   |
| Google Apigee (Pay-as-you-go)| 4,000,000 API operations           | ${"$"}200.00   |
| Vertex AI / Gemini API      | text/embedding token calculations   | ${"$"}180.00   |
| Cloud Armour & Firewalls    | DDOS active package security checks | ${"$"}150.00   |
| Cloud DNS & CDN Routing     | High density global asset distribution| ${"$"}80.00    |
|-----------------------------|-------------------------------------|------------|
| Total Estimated Cost        |                                     | ${"$"}1,235.00 |
    """.trimIndent())
}

@Composable
fun ScalabilityContent() {
    DocSectionHeader("100,000+ User Scalability Layout")
    DocParagraph(
        "The SV Online Mart software suite relies on 4 dynamic scalers " +
        "to manage burst traffic triggers securely."
    )
    DocSubHeader("Scalability Pillars")
    DocParagraph("1. DATABASE HORIZONTAL REPLICATION: Cloud SQL implements read-replica engines to offload search traffic away from the primary transactional instance.")
    DocParagraph("2. REDIS DISTRIBUTED CACHING: Memorystore Redis serves hot catalog queries instantly, skipping PostgreSQL computation entirely.")
    DocParagraph("3. Horizontal Pod Autoscaling (HPA): GKE pods automatically expand from 3 nodes to 30 nodes during sales peak milestones based on CPU triggers.")
}
