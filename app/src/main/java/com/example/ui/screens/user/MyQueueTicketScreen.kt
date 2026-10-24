package com.example.ui.screens.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QueueTicket
import com.example.model.TicketStatus
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubtlePulsingRing
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberDark
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.SlateBackground
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

@Composable
fun MyQueueTicketScreen(
    ticket: QueueTicket?,
    currentlyServingTicket: QueueTicket?,
    peopleAheadCount: Int,
    onBackToJoin: () -> Unit,
    onCancelTicket: () -> Unit,
    onPlayChimeTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    if (ticket == null) {
        EmptyTicketState(onBackToJoin = onBackToJoin, modifier = modifier)
        return
    }

    val isAlmostTurn = ticket.status == TicketStatus.ALMOST_TURN || (ticket.status == TicketStatus.WAITING && peopleAheadCount <= 1)
    val isServing = ticket.status == TicketStatus.SERVING
    val isCompleted = ticket.status == TicketStatus.COMPLETED

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Navigation row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onBackToJoin,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to categories",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                StatusBadge(status = ticket.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Urgent Notification Banners
            if (isServing) {
                ServingAnnouncementBanner(
                    counterNumber = ticket.assignedCounter ?: 1,
                    onPlayChime = onPlayChimeTest
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else if (isAlmostTurn) {
                AlmostTurnBanner()
                Spacer(modifier = Modifier.height(16.dp))
            }

            // HUGE Queue Ticket Center Card
            SubtlePulsingRing(
                isPulsing = isAlmostTurn || isServing,
                pulseColor = if (isServing) TealPrimary else AmberAccent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isServing) 2.5.dp else if (isAlmostTurn) 2.dp else 1.dp,
                        color = if (isServing) TealPrimary else if (isAlmostTurn) AmberAccent else SurfaceBorder
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("my_queue_ticket_card")
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Category Label
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TealContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = ticket.categoryName.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TealDark,
                                    letterSpacing = 1.2.sp
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "YOUR QUEUE NUMBER",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextMuted,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // HUGE Glanceable Queue Number
                        Text(
                            text = ticket.ticketNumber,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 64.sp,
                                color = if (isServing) TealPrimary else if (isAlmostTurn) AmberDark else TextDark,
                                letterSpacing = (-1).sp
                            ),
                            modifier = Modifier.testTag("huge_queue_number_text")
                        )

                        // Customer Nickname
                        if (ticket.customerName.isNotBlank() && ticket.customerName != "Guest") {
                            Text(
                                text = "Guest: ${ticket.customerName}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = SurfaceBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(20.dp))

                        // Live Metrics Row: Serving Now, People Ahead, Estimated Wait Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Serving Now
                            MetricColumn(
                                label = "NOW SERVING",
                                value = currentlyServingTicket?.ticketNumber ?: "None",
                                highlightColor = TealPrimary,
                                subtext = if (currentlyServingTicket != null && currentlyServingTicket.assignedCounter != null) "at Counter ${currentlyServingTicket.assignedCounter}" else "Idle"
                            )

                            // People Ahead
                            MetricColumn(
                                label = "PEOPLE AHEAD",
                                value = if (isServing) "0" else peopleAheadCount.toString(),
                                highlightColor = if (peopleAheadCount <= 1) AmberDark else TextDark,
                                subtext = if (isServing) "It's your turn!" else if (peopleAheadCount == 1) "Next in line" else "in this queue"
                            )

                            // Est. Wait Time
                            MetricColumn(
                                label = "EST. WAIT",
                                value = if (isServing) "Now" else "~${ticket.estimatedMinutes}m",
                                highlightColor = if (isServing) TealPrimary else TextDark,
                                subtext = if (isServing) "Proceed" else "mins approx"
                            )
                        }

                        // Window / Counter Assignment Highlight
                        if (ticket.assignedCounter != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isServing) TealContainer else SurfaceSubtle,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsWalk,
                                        contentDescription = null,
                                        tint = if (isServing) TealDark else TextDark,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Assigned to Counter ${ticket.assignedCounter}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isServing) TealDark else TextDark
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            if (isCompleted) {
                Button(
                    onClick = onBackToJoin,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Service Finished • Take New Ticket", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onPlayChimeTest,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Chime", color = TextDark)
                    }

                    Button(
                        onClick = { showCancelDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceSubtle,
                            contentColor = TextDark
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("leave_queue_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Leave Queue")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Minimalist Tips Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Live Status Updates",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        )
                        Text(
                            text = "Your screen will flash and sound a chime when your number is called. Keep this screen open.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Cancel Confirmation Dialog
        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Leave the Queue?") },
                text = {
                    Text("Your position for ticket ${ticket.ticketNumber} will be cancelled and given to the next person.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showCancelDialog = false
                            onCancelTicket()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Yes, Leave Queue")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) {
                        Text("Stay in Queue")
                    }
                }
            )
        }
    }
}

@Composable
fun ServingAnnouncementBanner(
    counterNumber: Int,
    onPlayChime: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TealDeep),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("now_serving_callout_banner")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = AmberAccent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NOW SERVING YOUR TICKET!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "PLEASE PROCEED TO COUNTER $counterNumber",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = TealDeep,
                        fontWeight = FontWeight.Black
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Officer is waiting for you now.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
            )
        }
    }
}

@Composable
fun AlmostTurnBanner() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = AmberContainer,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberAccent),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("almost_turn_banner")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AmberAccent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Almost your turn!",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnAmberContainer
                    )
                )
                Text(
                    text = "You are next in line. Please get ready near the service counters.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AmberDark
                    )
                )
            }
        }
    }
}

@Composable
fun MetricColumn(
    label: String,
    value: String,
    highlightColor: Color,
    subtext: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = highlightColor
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtext,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextMedium,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun EmptyTicketState(
    onBackToJoin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Active Queue Ticket",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You don't have an active queue ticket right now. Choose a service category to get one.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextMuted,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBackToJoin,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Join a Queue", fontWeight = FontWeight.Bold)
            }
        }
    }
}
