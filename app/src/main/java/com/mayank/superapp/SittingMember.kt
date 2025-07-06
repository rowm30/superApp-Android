package com.mayank.superapp

data class SittingMember(
    val id: Int,
    val lastName: String?,
    val nameOfMember: String,
    val partyName: String,
    val constituency: String,
    val state: String,
    val membershipStatus: String,
    val lokSabhaTerms: Int,
    val district: String?,
    val latitude: Double?,
    val longitude: Double?
)