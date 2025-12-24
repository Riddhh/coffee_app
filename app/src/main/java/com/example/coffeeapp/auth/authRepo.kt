package com.example.coffeeapp.auth


import android.R.attr.name
import android.content.Context

class AuthRepository(private val api: AuthApi, private val ctx: Context) {
    suspend fun register(name: String, email: String, pass: String): Result<Unit> = runCatching {
        api.register(RegisterReq( name = name.trim(), email.trim(), pass))
    }
    suspend fun login(email: String, pass: String): Result<Unit> = runCatching {
        val token = api.login(LoginReq(email.trim(), pass)).token
        TokenStore.save(ctx, token)
    }
    fun logout() = TokenStore.save(ctx, null)
    fun isLoggedIn(): Boolean = TokenStore.get(ctx) != null
}

