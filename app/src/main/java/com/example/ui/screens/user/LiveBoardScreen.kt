package com.example.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CounterInfo
import com.example.model.QueueTicket
import com.example.model.TicketStatus
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberDark
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.StatusSuccess
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

@Composable
fun LiveBoardScreen(
    counters: List<CounterInfo>,
    tickets: List<QueueTicket>,
    onSelectTicket: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeServingTickets = tickets.filter { it.status == TicketStatus.SERVING }
        .sortedByDescending { it.calledAtMillis ?: 0L }

    val recentWaiting = tickets.filter { it.status == TicketStatus.WAITING || it.status == TicketStatus.ALMOST_TURN }
        .sortedBy { it.issuedAtMillis }
        .take(8)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 700.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Lobby Display Board",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextDark
                                )
                            )
                        }
                        Text(
                            text = "Live counter calls and next numbers in line",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }

                    // Live pulse dot
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(SurfaceWhite, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FiberManualRecord,
                            contentDescription = null,
                            tint = StatusSuccess,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = StatusSuccess
                            )
                        )
                    }
                }
            }

            // Active Counters Serving Grid
            item {
                Text(
                    text = "ACTIVE COUNTER CALLS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )
            }

            items(counters, key = { it.id }) { counter ->
                val currentTicket = tickets.find { it.id == counter.currentTicketId && it.status == TicketStatus.SERVING }
                CounterBoardRow(
                    counter = counter,
                    currentTicket = currentTicket
                )
            }

            // Up Next in Queue
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "UP NEXT IN LINE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )
            }

            items(recentWaiting, key = { it.id }) { ticket ->
                UpNextTicketRow(
                    ticket = ticket,
                    onClick = { onSelectTicket(ticket.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CounterBoardRow(
    counter: CounterInfo,
    currentTicket: QueueTicket?
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (currentTicket != null) SurfaceWhite else SurfaceSubtle
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (currentTicket != null) 2.dp else 1.dp,
            color = if (currentTicket != null) TealPrimary else SurfaceBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (currentTicket != null) 3.dp else 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Counter title & officer
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TealContainer
                    ) {
                        Text(
                            text = "COUNTER ${counter.number}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TealDark
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    if (counter.isOnBreak) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(On Break)",
                            style = MaterialTheme.typography.bodySmall.copy(color = AmberDark)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = counter.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                )
                Text(
                    text = if (counter.assignedStaffName.isNotBlank()) "Officer: ${counter.assignedStaffName}" else "Counter Open",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
            }

            // Ticket being served
            if (currentTicket != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TealDeep,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CALLING",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AmberAccent,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = currentTicket.ticketNumber,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceBorder.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "READY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UpNextTicketRow(
    ticket: QueueTicket,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = ticket.ticketNumber,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = ticket.categoryName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    )
                    Text(
                        text = "Est. ~${ticket.estimatedMinutes} mins",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            if (ticket.status == TicketStatus.ALMOST_TURN) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AmberContainer
                ) {
                    Text(
                        text = "Almost Turn",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AmberDark
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
