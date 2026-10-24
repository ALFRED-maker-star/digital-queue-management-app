package com.example.ui.screens.staff

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CounterInfo
import com.example.model.QueueTicket
import com.example.model.ServiceCategory
import com.example.model.StaffProfile
import com.example.model.TicketStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberDark
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusErrorContainer
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessContainer
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealDeep
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMedium
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.delay

@Composable
fun CounterDashboardScreen(
    currentStaff: StaffProfile?,
    counters: List<CounterInfo>,
    tickets: List<QueueTicket>,
    categories: List<ServiceCategory>,
    onCallNext: (counterNumber: Int) -> Unit,
    onRecall: (counterNumber: Int) -> Unit,
    onSkip: (counterNumber: Int) -> Unit,
    onComplete: (counterNumber: Int) -> Unit,
    onToggleBreak: (counterNumber: Int) -> Unit,
    onChangeCounter: (counterNumber: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeCounterNumber = currentStaff?.defaultCounter ?: 2
    val activeCounter = counters.find { it.number == activeCounterNumber }
        ?: counters.firstOrNull() ?: CounterInfo("c_1", 1, "Counter 1")

    val currentServingTicket = tickets.find {
        it.id == activeCounter.currentTicketId && it.status == TicketStatus.SERVING
    }

    // Waiting queue eligible for this counter
    val waitingTickets = tickets.filter { ticket ->
        (ticket.status == TicketStatus.WAITING || ticket.status == TicketStatus.ALMOST_TURN) &&
                (activeCounter.activeCategoryIds.isEmpty() || ticket.categoryId in activeCounter.activeCategoryIds)
    }.sortedBy { it.issuedAtMillis }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            // Adaptive 2-Column Layout for Tablet/Desktop
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Left Column: Counter Station Controls (55%)
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CounterHeaderCard(
                        counter = activeCounter,
                        staff = currentStaff,
                        onToggleBreak = { onToggleBreak(activeCounterNumber) }
                    )

                    CurrentServingPanel(
                        currentTicket = currentServingTicket,
                        counterNumber = activeCounterNumber,
                        onCallNext = { onCallNext(activeCounterNumber) },
                        onRecall = { onRecall(activeCounterNumber) },
                        onSkip = { onSkip(activeCounterNumber) },
                        onComplete = { onComplete(activeCounterNumber) }
                    )

                    CounterStatsSummary(
                        servedToday = activeCounter.todayServedCount,
                        waitingCount = waitingTickets.size
                    )
                }

                // Right Column: Up Next Queue Pane (45%)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Up Next in Line",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TealContainer
                            ) {
                                Text(
                                    text = "${waitingTickets.size} waiting",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TealDark
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = SurfaceBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        if (waitingTickets.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No tickets currently waiting in this counter queue.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(waitingTickets, key = { it.id }) { ticket ->
                                    WaitingTicketCard(ticket = ticket)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Mobile-first single column vertical flow
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CounterHeaderCard(
                    counter = activeCounter,
                    staff = currentStaff,
                    onToggleBreak = { onToggleBreak(activeCounterNumber) }
                )

                CurrentServingPanel(
                    currentTicket = currentServingTicket,
                    counterNumber = activeCounterNumber,
                    onCallNext = { onCallNext(activeCounterNumber) },
                    onRecall = { onRecall(activeCounterNumber) },
                    onSkip = { onSkip(activeCounterNumber) },
                    onComplete = { onComplete(activeCounterNumber) }
                )

                CounterStatsSummary(
                    servedToday = activeCounter.todayServedCount,
                    waitingCount = waitingTickets.size
                )

                // Mobile Next in Line Section
                Text(
                    text = "NEXT IN LINE (${waitingTickets.size} WAITING)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )

                if (waitingTickets.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No waiting customers at this counter right now.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    waitingTickets.take(5).forEach { ticket ->
                        WaitingTicketCard(ticket = ticket)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CounterHeaderCard(
    counter: CounterInfo,
    staff: StaffProfile?,
    onToggleBreak: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (counter.isOnBreak) AmberContainer else TealContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (counter.isOnBreak) Icons.Default.Coffee else Icons.Default.Storefront,
                        contentDescription = null,
                        tint = if (counter.isOnBreak) AmberDark else TealDark,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Counter ${counter.number}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDark
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (counter.isOnBreak) AmberContainer else StatusSuccessContainer
                        ) {
                            Text(
                                text = if (counter.isOnBreak) "ON BREAK" else "ONLINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (counter.isOnBreak) AmberDark else StatusSuccess
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (staff != null) "Officer: ${staff.name}" else counter.name,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            // Break toggle button
            OutlinedButton(
                onClick = onToggleBreak,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (counter.isOnBreak) AmberDark else TextMedium
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (counter.isOnBreak) AmberAccent else SurfaceBorder
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Coffee,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (counter.isOnBreak) "Resume" else "Break",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun CurrentServingPanel(
    currentTicket: QueueTicket?,
    counterNumber: Int,
    onCallNext: () -> Unit,
    onRecall: () -> Unit,
    onSkip: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(
            width = if (currentTicket != null) 2.dp else 1.dp,
            color = if (currentTicket != null) TealPrimary else SurfaceBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("counter_serving_panel")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CURRENTLY SERVING AT THIS COUNTER",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (currentTicket != null) {
                // Large Glanceable Ticket Number
                Text(
                    text = currentTicket.ticketNumber,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 58.sp,
                        color = TealPrimary,
                        letterSpacing = (-1).sp
                    ),
                    modifier = Modifier.testTag("counter_current_ticket_number")
                )

                Text(
                    text = "${currentTicket.categoryName} • Guest: ${currentTicket.customerName}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Service duration elapsed
                ServiceTimerBadge(calledAtMillis = currentTicket.calledAtMillis ?: System.currentTimeMillis())

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = SurfaceBorder)
                Spacer(modifier = Modifier.height(18.dp))

                // PRIMARY BUTTON: Call Next
                Button(
                    onClick = onCallNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("call_next_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Call Next Customer",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SECONDARY BUTTONS: Skip, Recall, Complete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Recall (ring chime again)
                    OutlinedButton(
                        onClick = onRecall,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("recall_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Recall", color = TextDark, fontWeight = FontWeight.SemiBold)
                    }

                    // Skip
                    OutlinedButton(
                        onClick = onSkip,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("skip_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = null,
                            tint = StatusError,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Skip", color = StatusError, fontWeight = FontWeight.SemiBold)
                    }

                    // Complete
                    Button(
                        onClick = onComplete,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("finish_serving_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusSuccess,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Finish", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Counter Idle State
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SurfaceSubtle),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Counter Idle",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                )
                Text(
                    text = "Press 'Call Next' to bring the next waiting customer to this counter.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onCallNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("call_next_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Call Next Customer",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceTimerBadge(calledAtMillis: Long) {
    var elapsedSeconds by remember(calledAtMillis) {
        mutableLongStateOf((System.currentTimeMillis() - calledAtMillis) / 1000)
    }

    LaunchedEffect(calledAtMillis) {
        while (true) {
            delay(1000)
            elapsedSeconds = (System.currentTimeMillis() - calledAtMillis) / 1000
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SurfaceSubtle
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = TealDark,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Service Duration: $timeFormatted",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            )
        }
    }
}

@Composable
fun CounterStatsSummary(servedToday: Int, waitingCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Today's Served Count Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier
                .weight(1f)
                .testTag("today_served_count_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "TODAY'S SERVED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$servedToday",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = TealPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "tickets",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }

        // Waiting in Queue Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "QUEUE BACKLOG",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$waitingCount",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (waitingCount > 5) AmberDark else TextDark
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "waiting",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WaitingTicketCard(ticket: QueueTicket) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (ticket.status == TicketStatus.ALMOST_TURN) AmberContainer else SurfaceSubtle
                ) {
                    Text(
                        text = ticket.ticketNumber,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (ticket.status == TicketStatus.ALMOST_TURN) AmberDark else TextDark
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = ticket.categoryName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    )
                    Text(
                        text = "Guest: ${ticket.customerName}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            Text(
                text = "~${ticket.estimatedMinutes}m",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextMedium
                )
            )
        }
    }
}
