package com.example.coffeeapp.auth


import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class RegisterReq(val email: String, val password: String)
data class LoginReq(val email: String, val password: String)
data class TokenRes(val token: String)

interface AuthApi {
    @POST("/auth/register") suspend fun register(@Body b: RegisterReq)
    @POST("/auth/login") suspend fun login(@Body b: LoginReq): TokenRes
    @GET("/me") suspend fun me(): Map<String,String>
}
