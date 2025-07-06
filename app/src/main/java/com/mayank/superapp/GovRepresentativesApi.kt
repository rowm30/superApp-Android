package com.mayank.superapp


import retrofit2.http.GET
import retrofit2.http.Query

interface GovRepresentativesApi {
    @GET("geo-features/pc-name")
    suspend fun getConstituencyByLocation(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): List<String>

    @GET("sitting-members/by-constituency")
    suspend fun getSittingMembers(
        @Query("constituency") constituency: String
    ): List<SittingMember>
}