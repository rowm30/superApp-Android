package com.mayank.superapp

import retrofit2.http.GET

/**
 * Retrofit service for user related endpoints.
 */
interface UserService {
    /**
     * Fetch the profile of the currently authenticated user.
     */
    @GET("user/me")
    suspend fun getCurrentUser(): UserDto
}
