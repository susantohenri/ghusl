package com.niatmandiwajib.ghusl.data.remote.api

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.niatmandiwajib.ghusl.BuildConfig

// TODO fase 2: pindahkan panggilan Gemini ke GitHub Actions proxy, lihat catatan arsitektur
class GeminiService(private val context: Context) {

    private val systemPrompt: String by lazy {
        context.assets.open("ustadz_system_prompt.md")
            .bufferedReader().use { it.readText() }
    }

    private val model: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            systemInstruction = content { text(systemPrompt) }
        )
    }

    /**
     * Sends a question to Gemini and returns the response text.
     * TODO fase 2: pindahkan panggilan Gemini ke GitHub Actions proxy, lihat catatan arsitektur
     */
    suspend fun askQuestion(question: String): String {
        val response = model.generateContent(question)
        return response.text ?: "Mohon maaf, terjadi kesalahan dalam memproses pertanyaan Anda."
    }
}
