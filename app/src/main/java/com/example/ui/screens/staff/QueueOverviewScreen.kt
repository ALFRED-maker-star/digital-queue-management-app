package com.example.ui.screens.staff

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CounterInfo
import com.example.model.QueueOverviewStats
import com.example.model.QueueTicket
import com.example.model.ServiceCategory
import com.example.model.TicketStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberDark
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
fun QueueOverviewScreen(
    stats: QueueOverviewStats,
    categories: List<ServiceCategory>,
    tickets: List<QueueTicket>,
    counters: List<CounterInfo>,
    modifier: Modifier = Modifier
) {
    var selectedFilterStatus by remember { mutableStateOf<TicketStatus?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredTickets = tickets.filter { ticket ->
        val matchesStatus = selectedFilterStatus == null || ticket.status == selectedFilterStatus
        val matchesCategory = selectedCategoryFilter == null || ticket.categoryId == selectedCategoryFilter
        val matchesSearch = searchQuery.isBlank() ||
                ticket.ticketNumber.contains(searchQuery, ignoreCase = true) ||
                ticket.customerName.contains(searchQuery, ignoreCase = true) ||
                ticket.categoryName.contains(searchQuery, ignoreCase = true)
        matchesStatus && matchesCategory && matchesSearch
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 800.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "System Queue Overview",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )
                )
                Text(
                    text = "Real-time metrics, category wait times, and global ticket logs",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
            }

            // Top Glanceable Metrics Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OverviewStatCard(
                        title = "TOTAL WAITING",
                        value = "${stats.totalWaiting}",
                        subtitle = "across all queues",
                        color = if (stats.totalWaiting > 10) AmberDark else TealPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    OverviewStatCard(
                        title = "NOW SERVING",
                        value = "${stats.totalServing}",
                        subtitle = "at active counters",
                        color = TealDark,
                        modifier = Modifier.weight(1f)
                    )
                    OverviewStatCard(
                        title = "SERVED TODAY",
                        value = "${stats.totalCompletedToday}",
                        subtitle = "successful shifts",
                        color = StatusSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    OverviewStatCard(
                        title = "AVG WAIT TIME",
                        value = "${stats.avgOverallWaitMinutes}m",
                        subtitle = "per customer",
                        color = TextDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Average Wait Time Per Category Breakdown Cards
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "AVERAGE WAIT TIME PER CATEGORY",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories, key = { it.id }) { cat ->
                        val waitingInCat = tickets.count {
                            it.categoryId == cat.id && (it.status == TicketStatus.WAITING || it.status == TicketStatus.ALMOST_TURN)
                        }
                        val servingInCat = tickets.find {
                            it.categoryId == cat.id && it.status == TicketStatus.SERVING
                        }
                        val isFiltered = selectedCategoryFilter == cat.id

                        CategoryWaitTimeCard(
                            category = cat,
                            waitingCount = waitingInCat,
                            currentlyServingTicket = servingInCat?.ticketNumber,
                            isSelected = isFiltered,
                            onClick = {
                                selectedCategoryFilter = if (isFiltered) null else cat.id
                            }
                        )
                    }
                }
            }

            // Search and Status Filters
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "QUEUE TICKETS (${filteredTickets.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 1.sp
                        )
                    )

                    if (selectedCategoryFilter != null || selectedFilterStatus != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TealContainer,
                            modifier = Modifier.clickable {
                                selectedCategoryFilter = null
                                selectedFilterStatus = null
                            }
                        ) {
                            Text(
                                text = "Clear Filters ✕",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TealDark,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by ticket #, customer name, or category...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("queue_overview_search")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Status Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedFilterStatus == null,
                            onClick = { selectedFilterStatus = null },
                            label = { Text("All Status") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilterStatus == TicketStatus.WAITING,
                            onClick = { selectedFilterStatus = TicketStatus.WAITING },
                            label = { Text("Waiting") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilterStatus == TicketStatus.SERVING,
                            onClick = { selectedFilterStatus = TicketStatus.SERVING },
                            label = { Text("Now Serving") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilterStatus == TicketStatus.COMPLETED,
                            onClick = { selectedFilterStatus = TicketStatus.COMPLETED },
                            label = { Text("Completed") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilterStatus == TicketStatus.SKIPPED,
                            onClick = { selectedFilterStatus = TicketStatus.SKIPPED },
                            label = { Text("Skipped") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Ticket List
            if (filteredTickets.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        Text(
                            text = "No tickets match the selected filters.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                            modifier = Modifier.padding(24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredTickets, key = { it.id }) { ticket ->
                    QueueOverviewTicketRow(ticket = ticket)
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun OverviewStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    fontSize = 9.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = color
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun CategoryWaitTimeCard(
    category: ServiceCategory,
    waitingCount: Int,
    currentlyServingTicket: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) TealPrimary else SurfaceBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TealContainer
                ) {
                    Text(
                        text = category.prefix,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TealDark
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "$waitingCount waiting",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (waitingCount > 3) AmberDark else TextMedium
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = category.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Avg: ~${category.avgWaitMinutes} mins",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMedium,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (currentlyServingTicket != null) "Serving: $currentlyServingTicket" else "Counter Idle",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (currentlyServingTicket != null) TealDark else TextMuted,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun QueueOverviewTicketRow(ticket: QueueTicket) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Ticket Number Bold Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (ticket.status == TicketStatus.SERVING) TealDeep else SurfaceSubtle
                ) {
                    Text(
                        text = ticket.ticketNumber,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (ticket.status == TicketStatus.SERVING) Color.White else TextDark
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = ticket.categoryName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    )
                    Text(
                        text = "Guest: ${ticket.customerName}${if (ticket.assignedCounter != null) " • Counter ${ticket.assignedCounter}" else ""}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            StatusBadge(status = ticket.status)
        }
    }
}
