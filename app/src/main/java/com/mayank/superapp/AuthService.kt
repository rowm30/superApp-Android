package com.mayank.superapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/google/signin")
    suspend fun googleSignIn(@Body request: GoogleSignInRequest): Response<AuthResponse>
}