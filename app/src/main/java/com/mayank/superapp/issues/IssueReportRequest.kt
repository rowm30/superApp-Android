package com.mayank.superapp.issues

/**
 * Payload for submitting a new issue report.
 */
data class IssueReportRequest(
    val title: String,
    val description: String,
    val roads: String,
    val low: String,
    val mp: String,
    val image: String
)
