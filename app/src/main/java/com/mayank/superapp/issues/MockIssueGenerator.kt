package com.mayank.superapp.issues

import kotlin.random.Random

/** Helper object to create mock issues for demos */
object MockIssueGenerator {
    fun generate(count: Int = 20): List<Issue> {
        val titles = listOf(
            "Pothole on Main Street needs urgent repair",
            "Street light not working near Park Avenue",
            "Garbage not collected for 3 days",
            "Water leakage from main pipeline",
            "Illegal parking blocking emergency exit",
            "Park maintenance required - broken swings",
            "Power outage in Sector 5",
            "Damaged road divider causing accidents"
        )

        val reporters = listOf(
            "Rahul Sharma", "Priya Patel", "Amit Kumar",
            "Sneha Gupta", "Raj Singh", "Anita Verma"
        )

        val handlers = listOf("MP", "MLA", "DM")

        return List(count) { index ->
            Issue(
                title = titles.random(),
                description = "Detailed description of the issue...",
                category = IssueCategory.values().random(),
                status = IssueStatus.values().random(),
                priority = Priority.values().random(),
                location = GeoLocation(
                    28.5355 + Random.nextDouble() * 0.1,
                    77.3910 + Random.nextDouble() * 0.1
                ),
                reporterId = "user_${Random.nextInt(1000)}",
                reporterName = reporters.random(),
                reporterAvatar = "https://i.pravatar.cc/150?img=${Random.nextInt(70)}",
                images = if (Random.nextBoolean())
                    listOf("https://picsum.photos/400/300?random=$index")
                else emptyList(),
                handler = handlers.random()
            )
        }
    }
}
