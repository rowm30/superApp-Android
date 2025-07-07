package com.mayank.superapp.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ServicesRepository {
    suspend fun fetchServices(): List<Service> = withContext(Dispatchers.IO) {
        delay(500)
        MockServiceGenerator.generate()
    }
}
