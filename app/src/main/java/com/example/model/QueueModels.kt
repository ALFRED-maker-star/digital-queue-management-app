package com.example.model

enum class TicketStatus(val label: String) {
    WAITING("Waiting"),
    ALMOST_TURN("Almost Your Turn!"),
    SERVING("Now Serving"),
    COMPLETED("Completed"),
    SKIPPED("Skipped"),
    CANCELLED("Cancelled")
}

data class ServiceCategory(
    val id: String,
    val name: String,
    val prefix: String,
    val description: String,
    val avgWaitMinutes: Int,
    val iconKey: String = "service",
    val assignedCounters: List<Int> = listOf(1, 2),
    val isActive: Boolean = true
)

data class QueueTicket(
    val id: String,
    val ticketNumber: String,
    val categoryId: String,
    val categoryName: String,
    val customerName: String,
    val notes: String = "",
    val issuedAtMillis: Long = System.currentTimeMillis(),
    val status: TicketStatus = TicketStatus.WAITING,
    val assignedCounter: Int? = null,
    val calledAtMillis: Long? = null,
    val completedAtMillis: Long? = null,
    val estimatedMinutes: Int = 10
)

data class CounterInfo(
    val id: String,
    val number: Int,
    val name: String,
    val assignedStaffName: String = "",
    val activeCategoryIds: List<String> = emptyList(),
    val currentTicketId: String? = null,
    val isOnline: Boolean = true,
    val isOnBreak: Boolean = false,
    val todayServedCount: Int = 0
)

data class StaffProfile(
    val id: String,
    val name: String,
    val defaultCounter: Int,
    val roleTitle: String = "Counter Officer",
    val avatarColorHex: Long = 0xFF0D9488
)

data class QueueOverviewStats(
    val totalWaiting: Int,
    val totalServing: Int,
    val totalCompletedToday: Int,
    val avgOverallWaitMinutes: Int
)
