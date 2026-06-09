package com.example.data

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.example.BuildConfig

// ==========================================
// GEMINI API REQUEST/RESPONSE MOSHI DATA MODELS
// ==========================================

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

// ==========================================
// RETROFIT API SERVICE INTERFACE
// ==========================================

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// ==========================================
// GEMINI REPOSITORY CONTROLLER
// ==========================================

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }

    suspend fun queryGemini(systemPrompt: String, userMessage: String, history: List<SupportMessageEntity>): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey == "MY_GEMINI_API_KEY" || apiKey.isEmpty()) {
            return "API_KEY_MISSING_ERROR"
        }

        // Construct contents with system prompt combined or structured
        val contents = mutableListOf<GeminiContent>()

        // Introduce background system context
        contents.add(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = "System Directive: $systemPrompt\n\nConfirm you understand this mode context."))
            )
        )
        contents.add(
            GeminiContent(
                role = "model",
                parts = listOf(GeminiPart(text = "Understood. I will operate strictly in this designated role and assist the user accordingly."))
            )
        )

        // Append recent chat history
        history.takeLast(12).forEach { msg ->
            val role = if (msg.sender == "User") "user" else "model"
            contents.add(GeminiContent(role = role, parts = listOf(GeminiPart(text = msg.message))))
        }

        // Append current message
        contents.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = userMessage))))

        return try {
            val response = api.generateContent(apiKey, GeminiRequest(contents))
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I apologize, but I couldn't formulate a recommendation at this time. Please try reframing your query."
        } catch (e: Exception) {
            "Error communicating with Gemini: ${e.localizedMessage ?: "Unknown network exception"}"
        }
    }
}
