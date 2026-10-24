package com.example.ui.screens.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CounterInfo
import com.example.model.QueueTicket
import com.example.model.ServiceCategory
import com.example.model.TicketStatus
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberDark
import com.example.ui.theme.SlateBackground
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
fun JoinQueueScreen(
    categories: List<ServiceCategory>,
    tickets: List<QueueTicket>,
    counters: List<CounterInfo>,
    myActiveTicket: QueueTicket?,
    onJoinQueue: (categoryId: String, customerName: String, notes: String) -> Unit,
    onViewMyTicket: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryId by remember(categories) {
        mutableStateOf(categories.firstOrNull { it.isActive }?.id ?: "")
    }
    var customerName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val activeCategories = categories.filter { it.isActive }
    val selectedCategory = activeCategories.find { it.id == selectedCategoryId }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Existing ticket banner if user already has one
                if (myActiveTicket != null && (myActiveTicket.status == TicketStatus.WAITING || myActiveTicket.status == TicketStatus.ALMOST_TURN || myActiveTicket.status == TicketStatus.SERVING)) {
                    ActiveTicketFloatingNotice(
                        ticket = myActiveTicket,
                        onClick = onViewMyTicket
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Minimalist Hero Header
                Text(
                    text = "Welcome to QuickQueue",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select a Service",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = TextDark,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = "Choose your destination counter to get a queue number instantly.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextMuted
                    )
                )
            }

            // Category Selection Cards
            items(activeCategories, key = { it.id }) { cat ->
                val isSelected = cat.id == selectedCategoryId
                val waitingCount = tickets.count {
                    it.categoryId == cat.id && (it.status == TicketStatus.WAITING || it.status == TicketStatus.ALMOST_TURN)
                }
                val currentlyServing = tickets.find {
                    it.categoryId == cat.id && it.status == TicketStatus.SERVING
                }

                CategorySelectionCard(
                    category = cat,
                    isSelected = isSelected,
                    waitingCount = waitingCount,
                    currentlyServingTicket = currentlyServing?.ticketNumber,
                    onClick = {
                        selectedCategoryId = cat.id
                    }
                )
            }

            // Customer Details Input (Optional)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Your Name or Nickname (Optional)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDark
                                )
                            )
                        }

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            placeholder = { Text("e.g. Alex M. (Default: Guest)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = SurfaceBorder,
                                focusedContainerColor = SurfaceWhite,
                                unfocusedContainerColor = SurfaceWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("customer_name_input")
                        )
                    }
                }
            }

            // Big Get Queue Number Action Button
            item {
                Button(
                    onClick = {
                        if (selectedCategory != null) {
                            focusManager.clearFocus()
                            onJoinQueue(selectedCategory.id, customerName, "")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .testTag("get_queue_number_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Get Queue Number",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom glanceable system pulse note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${counters.count { it.isOnline && !it.isOnBreak }} counters active now",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CategorySelectionCard(
    category: ServiceCategory,
    isSelected: Boolean,
    waitingCount: Int,
    currentlyServingTicket: String?,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceWhite else SurfaceWhite
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) TealPrimary else SurfaceBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 3.dp else 1.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("category_card_${category.prefix}")
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category Prefix Badge
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) TealContainer else SurfaceSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.prefix,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) TealDark else TextDark,
                                fontSize = 18.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        )
                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 12.sp
                            ),
                            maxLines = 1
                        )
                    }
                }

                // Selected Checkmark
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(TealPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats row (waiting count, est wait time, current serving)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceSubtle)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // People Waiting
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = if (waitingCount > 3) AmberDark else TealPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$waitingCount waiting",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    )
                }

                // Avg Wait Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "~${category.avgWaitMinutes} min/turn",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMedium
                        )
                    )
                }

                // Serving Now
                Text(
                    text = if (currentlyServingTicket != null) "Now: $currentlyServingTicket" else "Ready",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (currentlyServingTicket != null) TealDark else TextMuted
                    )
                )
            }
        }
    }
}

@Composable
fun ActiveTicketFloatingNotice(
    ticket: QueueTicket,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = TealContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("active_ticket_notice")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint = TealDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "You are currently in queue",
                        style = MaterialTheme.typography.labelSmall.copy(color = TealDark)
                    )
                    Text(
                        text = "Ticket ${ticket.ticketNumber} • ${ticket.status.label}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    )
                }
            }

            Text(
                text = "View Ticket →",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
            )
        }
    }
}
