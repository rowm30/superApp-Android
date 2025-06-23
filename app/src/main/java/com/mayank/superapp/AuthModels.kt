package com.mayank.superapp

import com.google.gson.annotations.SerializedName

data class GoogleSignInRequest(
    val idToken: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val data: JwtData?,          // <─ nested object
    val error: String? = null
)

data class JwtData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    @SerializedName("expires_in")
    val expiresIn: Long,
    @SerializedName("token_type")
    val tokenType: String,
    val user: User
)

data class User(
    val id: String,
    val email: String,
    val name: String
)