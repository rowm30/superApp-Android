package com.mayank.superapp.services

/** Generates mock services list */
object MockServiceGenerator {
    fun generate(count: Int = 20): List<Service> = List(count) {
        Service(
            name = "Service $it",
            description = "Description for service $it",
            department = Department.values().random(),
            category = ServiceCategory.values().random(),
            isOnline = listOf(true, false).random(),
            fee = if (it % 3 == 0) 100.0 else null,
            processingTime = "3-5 days",
            requiredDocuments = listOf(Document("ID Proof")),
            eligibilityCriteria = listOf("Citizen"),
            process = listOf(ProcessStep("Submit form"))
        )
    }
}
