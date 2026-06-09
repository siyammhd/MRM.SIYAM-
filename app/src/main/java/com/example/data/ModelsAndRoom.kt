package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ==========================================
// 1. ROOM ENTITIES
// ==========================================

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val description: String,
    val stock: Int,
    val rating: Float,
    val imageUrl: String,
    val reviewsJson: String // Serialized format: "[{\"user\":\"Jon\",\"rating\":5,\"comment\":\"Incredible premium feel!\"}]"
)

@Entity(tableName = "wishlist")
data class WishlistItemEntity(
    @PrimaryKey val productId: Int
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val name: String,
    val passwordHash: String,
    val deliveryAddress: String = ""
)

@Entity(tableName = "cart")
data class CartItemEntity(
    @PrimaryKey val productId: Int,
    val quantity: Int
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val totalPrice: Double,
    val status: String, // "Pending", "Preparing", "Shipped", "Delivered"
    val paymentMethod: String, // "Visa", "MasterCard", "PayPal", "Stripe", "Google Pay"
    val driverName: String,
    val shippingAddress: String,
    val driverLat: Float, // Simulated map coordinates
    val driverLng: Float,
    val currentStep: Int // 0 to 4 in status track
)

@Entity(tableName = "support_messages")
data class SupportMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "User", "AI_Assistant" (shopping chatbot), "AI_Support" (system info chatbot)
    val message: String,
    val timestamp: Long
)

// ==========================================
// 2. DAOs (DATA ACCESS OBJECTS)
// ==========================================

@Dao
interface MartDao {
    // Products
    @Query("SELECT * FROM products")
    fun getAllProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Int)

    // Wishlist
    @Query("SELECT * FROM wishlist")
    fun getWishlistFlow(): Flow<List<WishlistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistItemEntity)

    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun removeFromWishlist(productId: Int)

    @Query("SELECT COUNT(*) FROM wishlist WHERE productId = :productId")
    suspend fun isInWishlist(productId: Int): Int

    // Cart
    @Query("SELECT * FROM cart")
    fun getCartFlow(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart")
    suspend fun getCartItems(): List<CartItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(item: CartItemEntity)

    @Query("DELETE FROM cart WHERE productId = :productId")
    suspend fun removeFromCart(productId: Int)

    @Query("DELETE FROM cart")
    suspend fun clearCart()

    // Orders
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrdersFlow(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    suspend fun getAllOrders(): List<OrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    // Chat
    @Query("SELECT * FROM support_messages ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<SupportMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: SupportMessageEntity)

    @Query("DELETE FROM support_messages")
    suspend fun clearChatHistory()

    // Users (Authorization)
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Update
    suspend fun updateUser(user: UserEntity)
}

// ==========================================
// 3. DATABASE BASE CLASS
// ==========================================

@Database(
    entities = [
        ProductEntity::class,
        WishlistItemEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        SupportMessageEntity::class,
        UserEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun martDao(): MartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sv_mart_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
