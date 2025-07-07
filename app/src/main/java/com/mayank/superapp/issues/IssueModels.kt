package com.mayank.superapp.issues

import java.util.UUID
import kotlin.random.Random

/** Simple location representation */
data class GeoLocation(
    val latitude: Double,
    val longitude: Double
)

/** Priority levels for issues */
enum class Priority { LOW, MEDIUM, HIGH, CRITICAL }

/** Status of an issue */
enum class IssueStatus { PENDING, ACKNOWLEDGED, IN_PROGRESS, RESOLVED, CLOSED }

/** Category of issue with icon and color resource id */
enum class IssueCategory(val icon: Int, val color: Int) {
    ROADS(android.R.drawable.ic_menu_compass, android.R.color.darker_gray),
    WATER(android.R.drawable.ic_menu_compass, android.R.color.holo_blue_light),
    ELECTRICITY(android.R.drawable.ic_menu_compass, android.R.color.holo_orange_light),
    WASTE(android.R.drawable.ic_menu_compass, android.R.color.holo_green_dark),
    SAFETY(android.R.drawable.ic_menu_compass, android.R.color.holo_red_dark),
    PARKS(android.R.drawable.ic_menu_compass, android.R.color.holo_green_light),
    OTHER(android.R.drawable.ic_menu_help, android.R.color.darker_gray)
}

/** Main Issue model */
data class Issue(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val category: IssueCategory,
    val status: IssueStatus,
    val priority: Priority,
    val location: GeoLocation,
    val reporterId: String,
    val reporterName: String,
    val reporterAvatar: String?,
    val assignedTo: String? = null,
    val assignedToName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val upvotes: Int = Random.nextInt(0, 1000),
    val downvotes: Int = Random.nextInt(0, 100),
    val comments: Int = Random.nextInt(0, 50),
    val images: List<String> = emptyList(),
    val distance: Float = Random.nextFloat() * 10f,
    val isFollowing: Boolean = false,
    val hasUpvoted: Boolean = false,
    /** Which representative will handle this issue */
    val handler: String
)
