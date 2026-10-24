package com.example.data

import com.example.model.CounterInfo
import com.example.model.QueueOverviewStats
import com.example.model.QueueTicket
import com.example.model.ServiceCategory
import com.example.model.StaffProfile
import com.example.model.TicketStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

object QueueRepository {

    // Default predefined staff profiles for fast testing
    val sampleStaffList = listOf(
        StaffProfile("staff_1", "Sarah Jenkins", 1, "Senior Cashier", 0xFF0D9488),
        StaffProfile("staff_2", "Alex Rivera", 2, "Customer Care Specialist", 0xFF0284C7),
        StaffProfile("staff_3", "Elena Rostova", 3, "Account Relations Officer", 0xFF7C3AED),
        StaffProfile("staff_4", "Marcus Vance", 4, "Consultation Manager", 0xFFD97706),
        StaffProfile("staff_5", "David Chen", 5, "Technical Operations", 0xFF059669)
    )

    private val initialCategories = listOf(
        ServiceCategory(
            id = "cat_cashier",
            name = "Cashier & Payments",
            prefix = "A",
            description = "Bill deposits, cash transfers, fee payments & receipts",
            avgWaitMinutes = 4,
            iconKey = "cashier",
            assignedCounters = listOf(1, 2),
            isActive = true
        ),
        ServiceCategory(
            id = "cat_service",
            name = "Customer Service",
            prefix = "CS",
            description = "General inquiries, card services, account changes & disputes",
            avgWaitMinutes = 8,
            iconKey = "service",
            assignedCounters = listOf(2, 3),
            isActive = true
        ),
        ServiceCategory(
            id = "cat_consultation",
            name = "Consultation & Accounts",
            prefix = "C",
            description = "Loan applications, business advisory, wealth & opening accounts",
            avgWaitMinutes = 15,
            iconKey = "consultation",
            assignedCounters = listOf(4),
            isActive = true
        ),
        ServiceCategory(
            id = "cat_express",
            name = "Express Inquiries",
            prefix = "E",
            description = "Quick document drop-off, form validation & pass pickups",
            avgWaitMinutes = 3,
            iconKey = "express",
            assignedCounters = listOf(1),
            isActive = true
        ),
        ServiceCategory(
            id = "cat_tech",
            name = "Technical Support",
            prefix = "TS",
            description = "Digital portal setup, authentication tokens & mobile banking help",
            avgWaitMinutes = 12,
            iconKey = "support",
            assignedCounters = listOf(5),
            isActive = true
        )
    )

    private val initialCounters = listOf(
        CounterInfo("c_1", 1, "Counter 1 (Express & Cashier)", "Sarah Jenkins", listOf("cat_cashier", "cat_express"), "t_a101", isOnline = true, isOnBreak = false, todayServedCount = 18),
        CounterInfo("c_2", 2, "Counter 2 (Customer Service)", "Alex Rivera", listOf("cat_cashier", "cat_service"), "t_cs201", isOnline = true, isOnBreak = false, todayServedCount = 14),
        CounterInfo("c_3", 3, "Counter 3 (Service & Relations)", "Elena Rostova", listOf("cat_service"), null, isOnline = true, isOnBreak = false, todayServedCount = 12),
        CounterInfo("c_4", 4, "Counter 4 (Consultation Suites)", "Marcus Vance", listOf("cat_consultation"), "t_c301", isOnline = true, isOnBreak = false, todayServedCount = 9),
        CounterInfo("c_5", 5, "Counter 5 (Tech Desk)", "David Chen", listOf("cat_tech"), null, isOnline = true, isOnBreak = false, todayServedCount = 11)
    )

    private val initialTickets = listOf(
        // Currently Serving
        QueueTicket("t_a101", "A-101", "cat_cashier", "Cashier & Payments", "Maria Gonzalez", "", System.currentTimeMillis() - 400000, TicketStatus.SERVING, 1, System.currentTimeMillis() - 240000, null, 4),
        QueueTicket("t_cs201", "CS-201", "cat_service", "Customer Service", "James Wilson", "", System.currentTimeMillis() - 500000, TicketStatus.SERVING, 2, System.currentTimeMillis() - 180000, null, 8),
        QueueTicket("t_c301", "C-301", "cat_consultation", "Consultation & Accounts", "Sophia Martinez", "", System.currentTimeMillis() - 600000, TicketStatus.SERVING, 4, System.currentTimeMillis() - 360000, null, 15),

        // Waiting Cashier
        QueueTicket("t_a102", "A-102", "cat_cashier", "Cashier & Payments", "Liam Patel", "", System.currentTimeMillis() - 300000, TicketStatus.ALMOST_TURN, null, null, null, 4),
        QueueTicket("t_a103", "A-103", "cat_cashier", "Cashier & Payments", "Emily Zhang", "", System.currentTimeMillis() - 250000, TicketStatus.WAITING, null, null, null, 8),
        QueueTicket("t_a104", "A-104", "cat_cashier", "Cashier & Payments", "Noah Brown", "", System.currentTimeMillis() - 150000, TicketStatus.WAITING, null, null, null, 12),

        // Waiting Customer Service
        QueueTicket("t_cs202", "CS-202", "cat_service", "Customer Service", "Olivia Davies", "", System.currentTimeMillis() - 380000, TicketStatus.ALMOST_TURN, null, null, null, 8),
        QueueTicket("t_cs203", "CS-203", "cat_service", "Customer Service", "Daniel Miller", "", System.currentTimeMillis() - 280000, TicketStatus.WAITING, null, null, null, 16),
        QueueTicket("t_cs204", "CS-204", "cat_service", "Customer Service", "Chloe Taylor", "", System.currentTimeMillis() - 120000, TicketStatus.WAITING, null, null, null, 24),

        // Waiting Consultation
        QueueTicket("t_c302", "C-302", "cat_consultation", "Consultation & Accounts", "Lucas Bennett", "", System.currentTimeMillis() - 320000, TicketStatus.WAITING, null, null, null, 15),
        QueueTicket("t_c303", "C-303", "cat_consultation", "Consultation & Accounts", "Ava Walker", "", System.currentTimeMillis() - 100000, TicketStatus.WAITING, null, null, null, 30),

        // Waiting Express & Tech
        QueueTicket("t_e401", "E-401", "cat_express", "Express Inquiries", "Benjamin Ross", "", System.currentTimeMillis() - 110000, TicketStatus.ALMOST_TURN, null, null, null, 3),
        QueueTicket("t_ts501", "TS-501", "cat_tech", "Technical Support", "Harper Young", "", System.currentTimeMillis() - 90000, TicketStatus.WAITING, null, null, null, 12)
    )

    private val _categories = MutableStateFlow<List<ServiceCategory>>(initialCategories)
    val categories: StateFlow<List<ServiceCategory>> = _categories.asStateFlow()

    private val _counters = MutableStateFlow<List<CounterInfo>>(initialCounters)
    val counters: StateFlow<List<CounterInfo>> = _counters.asStateFlow()

    private val _tickets = MutableStateFlow<List<QueueTicket>>(initialTickets)
    val tickets: StateFlow<List<QueueTicket>> = _tickets.asStateFlow()

    private val _currentStaff = MutableStateFlow<StaffProfile?>(sampleStaffList[1]) // Alex Rivera at Counter 2 by default
    val currentStaff: StateFlow<StaffProfile?> = _currentStaff.asStateFlow()

    // The user's active ticket on this device
    private val _myActiveTicketId = MutableStateFlow<String?>("t_cs202")
    val myActiveTicketId: StateFlow<String?> = _myActiveTicketId.asStateFlow()

    // Counter number counters sequences for ticket IDs
    private val serialCounters = mutableMapOf(
        "A" to 105,
        "CS" to 205,
        "C" to 304,
        "E" to 402,
        "TS" to 502
    )

    fun loginStaff(staff: StaffProfile, counterNumber: Int) {
        val updatedStaff = staff.copy(defaultCounter = counterNumber)
        _currentStaff.value = updatedStaff
        _counters.update { list ->
            list.map { counter ->
                if (counter.number == counterNumber) {
                    counter.copy(assignedStaffName = staff.name, isOnline = true, isOnBreak = false)
                } else counter
            }
        }
    }

    fun logoutStaff() {
        val current = _currentStaff.value
        if (current != null) {
            _counters.update { list ->
                list.map { counter ->
                    if (counter.number == current.defaultCounter) {
                        counter.copy(isOnline = false)
                    } else counter
                }
            }
        }
        _currentStaff.value = null
    }

    fun setStaffCounter(counterNumber: Int) {
        val current = _currentStaff.value ?: return
        loginStaff(current, counterNumber)
    }

    fun toggleCounterBreak(counterNumber: Int) {
        _counters.update { list ->
            list.map { counter ->
                if (counter.number == counterNumber) {
                    counter.copy(isOnBreak = !counter.isOnBreak)
                } else counter
            }
        }
    }

    fun joinQueue(categoryId: String, customerName: String, notes: String = ""): QueueTicket {
        val category = _categories.value.find { it.id == categoryId }
            ?: _categories.value.first()
        val prefix = category.prefix
        val currentNum = serialCounters.getOrDefault(prefix, 100)
        serialCounters[prefix] = currentNum + 1
        val ticketNumber = "$prefix-$currentNum"
        val ticketId = "t_${UUID.randomUUID().toString().take(8)}"

        // Count how many ahead
        val waitingAhead = _tickets.value.count {
            it.categoryId == categoryId && (it.status == TicketStatus.WAITING || it.status == TicketStatus.ALMOST_TURN)
        }
        val estimatedWait = (waitingAhead + 1) * category.avgWaitMinutes
        val status = if (waitingAhead == 0) TicketStatus.ALMOST_TURN else TicketStatus.WAITING

        val newTicket = QueueTicket(
            id = ticketId,
            ticketNumber = ticketNumber,
            categoryId = category.id,
            categoryName = category.name,
            customerName = if (customerName.isBlank()) "Guest" else customerName.trim(),
            notes = notes.trim(),
            issuedAtMillis = System.currentTimeMillis(),
            status = status,
            estimatedMinutes = estimatedWait
        )

        _tickets.update { listOf(newTicket) + it }
        _myActiveTicketId.value = newTicket.id
        return newTicket
    }

    fun cancelTicket(ticketId: String) {
        _tickets.update { list ->
            list.map { ticket ->
                if (ticket.id == ticketId) {
                    ticket.copy(status = TicketStatus.CANCELLED)
                } else ticket
            }
        }
        if (_myActiveTicketId.value == ticketId) {
            _myActiveTicketId.value = null
        }
        refreshWaitCounts()
    }

    fun callNext(counterNumber: Int): QueueTicket? {
        val counter = _counters.value.find { it.number == counterNumber } ?: return null
        val activeCatIds = counter.activeCategoryIds

        // First, if there was a currently serving ticket at this counter, mark completed
        val currentTicketId = counter.currentTicketId
        var updatedTickets = _tickets.value.map { t ->
            if (t.id == currentTicketId && t.status == TicketStatus.SERVING) {
                t.copy(status = TicketStatus.COMPLETED, completedAtMillis = System.currentTimeMillis())
            } else t
        }

        // Find next eligible ticket in WAITING or ALMOST_TURN
        val nextTicket = updatedTickets
            .filter { (it.status == TicketStatus.WAITING || it.status == TicketStatus.ALMOST_TURN) && (activeCatIds.isEmpty() || it.categoryId in activeCatIds) }
            .minByOrNull { it.issuedAtMillis }

        if (nextTicket != null) {
            updatedTickets = updatedTickets.map { t ->
                if (t.id == nextTicket.id) {
                    t.copy(
                        status = TicketStatus.SERVING,
                        assignedCounter = counterNumber,
                        calledAtMillis = System.currentTimeMillis()
                    )
                } else t
            }

            _tickets.value = updatedTickets

            _counters.update { list ->
                list.map { c ->
                    if (c.number == counterNumber) {
                        c.copy(
                            currentTicketId = nextTicket.id,
                            todayServedCount = c.todayServedCount + (if (currentTicketId != null) 1 else 0),
                            isOnBreak = false
                        )
                    } else c
                }
            }
        } else {
            // No next ticket
            _tickets.value = updatedTickets
            _counters.update { list ->
                list.map { c ->
                    if (c.number == counterNumber) {
                        c.copy(
                            currentTicketId = null,
                            todayServedCount = c.todayServedCount + (if (currentTicketId != null) 1 else 0)
                        )
                    } else c
                }
            }
        }

        refreshWaitCounts()
        return nextTicket
    }

    fun recallCurrent(counterNumber: Int): QueueTicket? {
        val counter = _counters.value.find { it.number == counterNumber } ?: return null
        val ticketId = counter.currentTicketId ?: return null
        var foundTicket: QueueTicket? = null

        _tickets.update { list ->
            list.map { ticket ->
                if (ticket.id == ticketId) {
                    val updated = ticket.copy(calledAtMillis = System.currentTimeMillis())
                    foundTicket = updated
                    updated
                } else ticket
            }
        }
        return foundTicket
    }

    fun skipCurrent(counterNumber: Int): QueueTicket? {
        val counter = _counters.value.find { it.number == counterNumber } ?: return null
        val ticketId = counter.currentTicketId ?: return null
        var skippedTicket: QueueTicket? = null

        _tickets.update { list ->
            list.map { ticket ->
                if (ticket.id == ticketId) {
                    val updated = ticket.copy(status = TicketStatus.SKIPPED)
                    skippedTicket = updated
                    updated
                } else ticket
            }
        }

        _counters.update { list ->
            list.map { c ->
                if (c.number == counterNumber) {
                    c.copy(currentTicketId = null)
                } else c
            }
        }

        refreshWaitCounts()
        return skippedTicket
    }

    fun completeCurrent(counterNumber: Int): QueueTicket? {
        val counter = _counters.value.find { it.number == counterNumber } ?: return null
        val ticketId = counter.currentTicketId ?: return null
        var completedTicket: QueueTicket? = null

        _tickets.update { list ->
            list.map { ticket ->
                if (ticket.id == ticketId) {
                    val updated = ticket.copy(
                        status = TicketStatus.COMPLETED,
                        completedAtMillis = System.currentTimeMillis()
                    )
                    completedTicket = updated
                    updated
                } else ticket
            }
        }

        _counters.update { list ->
            list.map { c ->
                if (c.number == counterNumber) {
                    c.copy(
                        currentTicketId = null,
                        todayServedCount = c.todayServedCount + 1
                    )
                } else c
            }
        }

        refreshWaitCounts()
        return completedTicket
    }

    private fun refreshWaitCounts() {
        val categories = _categories.value
        val allTickets = _tickets.value

        val updated = allTickets.map { ticket ->
            if (ticket.status == TicketStatus.WAITING || ticket.status == TicketStatus.ALMOST_TURN) {
                val cat = categories.find { it.id == ticket.categoryId }
                val avgWait = cat?.avgWaitMinutes ?: 5
                val peopleAhead = allTickets.count { other ->
                    other.categoryId == ticket.categoryId &&
                            (other.status == TicketStatus.WAITING || other.status == TicketStatus.ALMOST_TURN) &&
                            other.issuedAtMillis < ticket.issuedAtMillis
                }
                val newStatus = if (peopleAhead <= 1) TicketStatus.ALMOST_TURN else TicketStatus.WAITING
                val newEst = (peopleAhead + 1) * avgWait
                ticket.copy(status = newStatus, estimatedMinutes = newEst)
            } else ticket
        }
        _tickets.value = updated
    }

    fun selectMyTicket(ticketId: String) {
        _myActiveTicketId.value = ticketId
    }

    fun clearMyTicket() {
        _myActiveTicketId.value = null
    }

    fun addCategory(
        name: String,
        prefix: String,
        description: String,
        avgWaitMinutes: Int,
        iconKey: String,
        assignedCounters: List<Int>
    ) {
        val newCat = ServiceCategory(
            id = "cat_${UUID.randomUUID().toString().take(6)}",
            name = name.trim(),
            prefix = prefix.uppercase().trim(),
            description = description.trim(),
            avgWaitMinutes = avgWaitMinutes.coerceAtLeast(1),
            iconKey = iconKey,
            assignedCounters = assignedCounters,
            isActive = true
        )
        serialCounters[newCat.prefix] = 101
        _categories.update { it + newCat }
    }

    fun updateCategory(updated: ServiceCategory) {
        _categories.update { list ->
            list.map { if (it.id == updated.id) updated else it }
        }
    }

    fun toggleCategoryActive(categoryId: String) {
        _categories.update { list ->
            list.map { if (it.id == categoryId) it.copy(isActive = !it.isActive) else it }
        }
    }

    fun resetDemoQueues() {
        _categories.value = initialCategories
        _counters.value = initialCounters
        _tickets.value = initialTickets
        _myActiveTicketId.value = "t_cs202"
        serialCounters["A"] = 105
        serialCounters["CS"] = 205
        serialCounters["C"] = 304
        serialCounters["E"] = 402
        serialCounters["TS"] = 502
    }

    fun getPeopleAheadCount(ticket: QueueTicket): Int {
        if (ticket.status == TicketStatus.SERVING) return 0
        if (ticket.status == TicketStatus.COMPLETED || ticket.status == TicketStatus.SKIPPED || ticket.status == TicketStatus.CANCELLED) return 0
        return _tickets.value.count { other ->
            other.categoryId == ticket.categoryId &&
                    (other.status == TicketStatus.WAITING || other.status == TicketStatus.ALMOST_TURN) &&
                    other.issuedAtMillis < ticket.issuedAtMillis
        }
    }

    fun getCurrentServingForCategory(categoryId: String): QueueTicket? {
        return _tickets.value.find { it.categoryId == categoryId && it.status == TicketStatus.SERVING }
    }

    fun getOverviewStats(): QueueOverviewStats {
        val all = _tickets.value
        val waiting = all.count { it.status == TicketStatus.WAITING || it.status == TicketStatus.ALMOST_TURN }
        val serving = all.count { it.status == TicketStatus.SERVING }
        val completed = all.count { it.status == TicketStatus.COMPLETED }
        val avgWait = if (waiting > 0) {
            all.filter { it.status == TicketStatus.WAITING || it.status == TicketStatus.ALMOST_TURN }
                .map { it.estimatedMinutes }
                .average()
                .toInt()
        } else 0

        return QueueOverviewStats(
            totalWaiting = waiting,
            totalServing = serving,
            totalCompletedToday = completed + 42, // Includes historical shifts today
            avgOverallWaitMinutes = if (avgWait > 0) avgWait else 8
        )
    }
}
