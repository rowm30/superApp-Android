package com.mayank.superapp.issues

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit service for submitting issue reports.
 */
interface IssueReportService {
    @POST("api/v1/report")
    suspend fun submitReport(@Body request: IssueReportRequest): Response<IssueReportResponse>
}
