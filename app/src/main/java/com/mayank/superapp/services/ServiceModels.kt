package com.mayank.superapp.services

import java.util.UUID
import kotlin.random.Random

/** Simple document model */
data class Document(
    val name: String
)

/** Step in the process */
data class ProcessStep(
    val description: String
)

enum class ServiceCategory { GENERAL, HEALTH, TRANSPORT, EDUCATION }

enum class Department { ADMINISTRATION, POLICE, FINANCE, HEALTH }

/** Service model */
data class Service(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val department: Department,
    val category: ServiceCategory,
    val isOnline: Boolean,
    val fee: Double?,
    val processingTime: String,
    val requiredDocuments: List<Document>,
    val eligibilityCriteria: List<String>,
    val process: List<ProcessStep>,
    val popularityScore: Int = Random.nextInt(0, 100),
    val averageRating: Float = Random.nextDouble(1.0, 5.0).toFloat(),
    val totalReviews: Int = Random.nextInt(0, 1000)
)
