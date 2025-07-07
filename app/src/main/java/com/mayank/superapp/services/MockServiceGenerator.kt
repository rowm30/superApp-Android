package com.mayank.superapp.services

/** Generates mock services list */
object MockServiceGenerator {
    private val names = listOf(
        "Apply for Birth Certificate",
        "Renew Driving License",
        "Water Connection Request",
        "Electricity Bill Payment",
        "Passport Application",
        "Property Tax Payment"
    )

    fun generate(count: Int = 20): List<Service> = List(count) { index ->
        val title = names.random()
        Service(
            name = title,
            description = "Information regarding $title",
            department = Department.values().random(),
            category = ServiceCategory.values().random(),
            isOnline = listOf(true, false).random(),
            fee = if (index % 3 == 0) 100.0 else null,
            processingTime = "3-5 days",
            requiredDocuments = listOf(Document("ID Proof")),
            eligibilityCriteria = listOf("Citizen"),
            process = listOf(ProcessStep("Submit form"))
        )
    }
}
