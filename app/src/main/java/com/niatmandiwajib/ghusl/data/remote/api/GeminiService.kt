package com.niatmandiwajib.ghusl.data.remote.api

import android.content.Context
import com.niatmandiwajib.ghusl.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

// Panggilan Gemini diubah menggunakan OkHttp untuk mem-bypass validasi format API Key di SDK versi lama.
// Hal ini memungkinkan penggunaan format API Key baru (AQ.) yang dikeluarkan Google AI Studio.
// TODO fase 2: pindahkan panggilan Gemini ke GitHub Actions proxy, lihat catatan arsitektur
class GeminiService(private val context: Context) {

    private val systemPrompt: String by lazy {
        context.assets.open("ustadz_system_prompt.md")
            .bufferedReader().use { it.readText() }
    }

    private val client = OkHttpClient()

    /**
     * Sends a question to Gemini and returns the response text using direct REST API call.
     */
    suspend fun askQuestion(question: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=$apiKey"

        val sysInstPart = JSONObject().apply { put("text", systemPrompt) }
        val sysInstPartsArray = JSONArray().put(sysInstPart)
        val sysInstObj = JSONObject().apply { put("parts", sysInstPartsArray) }

        val contentPart = JSONObject().apply { put("text", question) }
        val contentPartsArray = JSONArray().put(contentPart)
        val contentObj = JSONObject().apply { put("parts", contentPartsArray) }

        val jsonBody = JSONObject().apply {
            put("systemInstruction", sysInstObj)
            put("contents", JSONArray().put(contentObj))
        }

        val body = jsonBody.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                throw IOException("Unexpected code $response, body: $errorBody")
            }
            val responseBody = response.body?.string() ?: throw IOException("Empty response")
            val jsonResponse = JSONObject(responseBody)
            
            try {
                jsonResponse.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            } catch (e: Exception) {
                "Mohon maaf, terjadi kesalahan dalam memproses respons: ${e.message}"
            }
        }
    }
}
