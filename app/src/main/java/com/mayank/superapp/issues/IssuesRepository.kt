package com.mayank.superapp.issues

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/** Simple repository returning mock issues */
class IssuesRepository {
    /** Simulate network load with delay */
    suspend fun fetchIssues(): List<Issue> = withContext(Dispatchers.IO) {
        delay(500)
        MockIssueGenerator.generate()
    }
}
