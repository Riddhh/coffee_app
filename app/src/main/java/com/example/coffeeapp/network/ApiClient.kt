package com.example.coffeeapp.network


import io.ktor.http.ContentType

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:3000"   // use 10.0.2.2 for Android emulator

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return client.post("$BASE_URL/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }.body()
    }

    suspend fun register(email: String, password: String): LoginResponse {
        return client.post("$BASE_URL/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }.body()
    }
}

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(
    val token: String? = null,
    val message: String? = null
)
