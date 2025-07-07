package com.mayank.superapp

/**
 * Simple DTO representing a user profile returned from the API.
 */
data class UserDto(
    val id: String,
    val email: String,
    val name: String
)
