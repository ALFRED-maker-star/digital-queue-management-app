package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.QueueRepository
import com.example.model.CounterInfo
import com.example.model.QueueOverviewStats
import com.example.model.QueueTicket
import com.example.model.ServiceCategory
import com.example.model.StaffProfile
import com.example.model.TicketStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppRole {
    CUSTOMER,
    STAFF
}

enum class CustomerScreen {
    JOIN_QUEUE,
    MY_TICKET,
    LIVE_BOARD
}

enum class StaffScreen {
    LOGIN,
    DASHBOARD,
    OVERVIEW,
    CATEGORIES_SETUP
}

data class QueueUiState(
    val role: AppRole = AppRole.CUSTOMER,
    val customerScreen: CustomerScreen = CustomerScreen.JOIN_QUEUE,
    val staffScreen: StaffScreen = StaffScreen.DASHBOARD,
    val alertMessage: String? = null,
    val alertToneTimestamp: Long? = null
)

class QueueViewModel : ViewModel() {

    private val repository = QueueRepository

    val categories: StateFlow<List<ServiceCategory>> = repository.categories
    val counters: StateFlow<List<CounterInfo>> = repository.counters
    val tickets: StateFlow<List<QueueTicket>> = repository.tickets
    val currentStaff: StateFlow<StaffProfile?> = repository.currentStaff

    private val _uiState = MutableStateFlow(QueueUiState())
    val uiState: StateFlow<QueueUiState> = _uiState.asStateFlow()

    // Resolved active ticket for the customer
    val myActiveTicket: StateFlow<QueueTicket?> = combine(
        repository.myActiveTicketId,
        repository.tickets
    ) { activeId, ticketList ->
        if (activeId == null) null else ticketList.find { it.id == activeId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // If an active ticket exists initially, start on MY_TICKET screen
        viewModelScope.launch {
            if (repository.myActiveTicketId.value != null) {
                _uiState.value = _uiState.value.copy(customerScreen = CustomerScreen.MY_TICKET)
            }
        }
    }

    fun setRole(role: AppRole) {
        _uiState.value = _uiState.value.copy(role = role)
    }

    fun setCustomerScreen(screen: CustomerScreen) {
        _uiState.value = _uiState.value.copy(customerScreen = screen)
    }

    fun setStaffScreen(screen: StaffScreen) {
        _uiState.value = _uiState.value.copy(staffScreen = screen)
    }

    fun joinQueue(categoryId: String, customerName: String, notes: String = "") {
        val ticket = repository.joinQueue(categoryId, customerName, notes)
        _uiState.value = _uiState.value.copy(
            customerScreen = CustomerScreen.MY_TICKET,
            alertMessage = "Queue Ticket ${ticket.ticketNumber} Generated!"
        )
    }

    fun cancelMyTicket() {
        val current = myActiveTicket.value ?: return
        repository.cancelTicket(current.id)
        _uiState.value = _uiState.value.copy(
            customerScreen = CustomerScreen.JOIN_QUEUE,
            alertMessage = "Ticket ${current.ticketNumber} cancelled"
        )
    }

    fun selectExistingTicketAsMine(ticketId: String) {
        repository.selectMyTicket(ticketId)
        _uiState.value = _uiState.value.copy(customerScreen = CustomerScreen.MY_TICKET)
    }

    fun callNextTicket(counterNumber: Int) {
        val called = repository.callNext(counterNumber)
        if (called != null) {
            _uiState.value = _uiState.value.copy(
                alertMessage = "Calling Ticket ${called.ticketNumber} to Counter $counterNumber",
                alertToneTimestamp = System.currentTimeMillis()
            )
        } else {
            _uiState.value = _uiState.value.copy(
                alertMessage = "No waiting tickets in queue for Counter $counterNumber"
            )
        }
    }

    fun recallCurrentTicket(counterNumber: Int) {
        val ticket = repository.recallCurrent(counterNumber)
        if (ticket != null) {
            _uiState.value = _uiState.value.copy(
                alertMessage = "Recalled Ticket ${ticket.ticketNumber} to Counter $counterNumber",
                alertToneTimestamp = System.currentTimeMillis()
            )
        }
    }

    fun skipCurrentTicket(counterNumber: Int) {
        val ticket = repository.skipCurrent(counterNumber)
        if (ticket != null) {
            _uiState.value = _uiState.value.copy(
                alertMessage = "Skipped Ticket ${ticket.ticketNumber}"
            )
        }
    }

    fun completeCurrentTicket(counterNumber: Int) {
        val ticket = repository.completeCurrent(counterNumber)
        if (ticket != null) {
            _uiState.value = _uiState.value.copy(
                alertMessage = "Ticket ${ticket.ticketNumber} completed successfully"
            )
        }
    }

    fun loginStaff(staff: StaffProfile, counterNumber: Int) {
        repository.loginStaff(staff, counterNumber)
        _uiState.value = _uiState.value.copy(
            staffScreen = StaffScreen.DASHBOARD,
            alertMessage = "Welcome ${staff.name}! Counter $counterNumber is active."
        )
    }

    fun logoutStaff() {
        repository.logoutStaff()
        _uiState.value = _uiState.value.copy(
            staffScreen = StaffScreen.LOGIN,
            alertMessage = "Staff signed out."
        )
    }

    fun setStaffCounter(counterNumber: Int) {
        repository.setStaffCounter(counterNumber)
    }

    fun toggleBreak(counterNumber: Int) {
        repository.toggleCounterBreak(counterNumber)
    }

    fun addCategory(
        name: String,
        prefix: String,
        description: String,
        avgWaitMinutes: Int,
        iconKey: String,
        assignedCounters: List<Int>
    ) {
        repository.addCategory(name, prefix, description, avgWaitMinutes, iconKey, assignedCounters)
        _uiState.value = _uiState.value.copy(alertMessage = "Added category $name ($prefix)")
    }

    fun toggleCategoryActive(categoryId: String) {
        repository.toggleCategoryActive(categoryId)
    }

    fun updateCategory(category: ServiceCategory) {
        repository.updateCategory(category)
        _uiState.value = _uiState.value.copy(alertMessage = "Category updated")
    }

    fun resetDemoData() {
        repository.resetDemoQueues()
        _uiState.value = _uiState.value.copy(
            alertMessage = "Reset demo queues with fresh realistic sample data"
        )
    }

    fun dismissAlert() {
        _uiState.value = _uiState.value.copy(alertMessage = null)
    }

    fun getPeopleAhead(ticket: QueueTicket): Int = repository.getPeopleAheadCount(ticket)

    fun getCurrentServingForCategory(categoryId: String): QueueTicket? =
        repository.getCurrentServingForCategory(categoryId)

    fun getOverviewStats(): QueueOverviewStats = repository.getOverviewStats()
}
