package com.example.ui.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CounterInfo
import com.example.model.ServiceCategory
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
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMedium
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriesSetupScreen(
    categories: List<ServiceCategory>,
    counters: List<CounterInfo>,
    onToggleActive: (categoryId: String) -> Unit,
    onAddCategory: (name: String, prefix: String, description: String, avgWait: Int, iconKey: String, counters: List<Int>) -> Unit,
    onUpdateCategory: (ServiceCategory) -> Unit,
    onResetDemoData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<ServiceCategory?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 750.dp)
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
                        Text(
                            text = "Service Categories Setup",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDark
                            )
                        )
                        Text(
                            text = "Manage queue branches, prefix codes, and assigned counters",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }

                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.testTag("add_category_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Service")
                    }
                }
            }

            items(categories, key = { it.id }) { cat ->
                CategorySetupCard(
                    category = cat,
                    allCounters = counters,
                    onToggleActive = { onToggleActive(cat.id) },
                    onEdit = { editingCategory = cat },
                    onCounterToggle = { counterNum ->
                        val updatedList = if (counterNum in cat.assignedCounters) {
                            cat.assignedCounters - counterNum
                        } else {
                            cat.assignedCounters + counterNum
                        }
                        onUpdateCategory(cat.copy(assignedCounters = updatedList.sorted()))
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onResetDemoData,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restore Default Categories & Counters")
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Add Category Dialog
        if (showAddDialog) {
            AddCategoryDialog(
                counters = counters,
                onDismiss = { showAddDialog = false },
                onConfirm = { name, prefix, desc, avgWait, icon, assigned ->
                    onAddCategory(name, prefix, desc, avgWait, icon, assigned)
                    showAddDialog = false
                }
            )
        }

        // Edit Category Dialog
        if (editingCategory != null) {
            EditCategoryDialog(
                category = editingCategory!!,
                counters = counters,
                onDismiss = { editingCategory = null },
                onConfirm = { updated ->
                    onUpdateCategory(updated)
                    editingCategory = null
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySetupCard(
    category: ServiceCategory,
    allCounters: List<CounterInfo>,
    onToggleActive: () -> Unit,
    onEdit: () -> Unit,
    onCounterToggle: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Prefix Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (category.isActive) TealContainer else SurfaceSubtle
                    ) {
                        Text(
                            text = category.prefix,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = if (category.isActive) TealDark else TextMuted
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (category.isActive) TextDark else TextMuted
                            )
                        )
                        Text(
                            text = "${category.avgWaitMinutes} min estimated turn • ${category.description}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                            maxLines = 1
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Category",
                            tint = TextMedium,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Switch(
                        checked = category.isActive,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TealPrimary,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceSubtle
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Assigned Counters Chips
            Text(
                text = "ASSIGNED COUNTERS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allCounters.forEach { counter ->
                    val isAssigned = counter.number in category.assignedCounters
                    FilterChip(
                        selected = isAssigned,
                        onClick = { onCounterToggle(counter.number) },
                        label = { Text("Counter ${counter.number}") },
                        leadingIcon = if (isAssigned) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealContainer,
                            selectedLabelColor = TealDark,
                            selectedLeadingIconColor = TealDark
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun AddCategoryDialog(
    counters: List<CounterInfo>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, prefix: String, desc: String, avgWait: Int, icon: String, assigned: List<Int>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var prefix by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var avgWaitText by remember { mutableStateOf("5") }
    val assignedCounters = remember { mutableStateListOf(1, 2) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Service Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Service Name (e.g. Loans)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = prefix,
                        onValueChange = { prefix = it.uppercase().take(3) },
                        label = { Text("Prefix (e.g. L)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = avgWaitText,
                        onValueChange = { avgWaitText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Avg Mins") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description / Scope") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Assign Counters:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    counters.forEach { counter ->
                        val isAssigned = counter.number in assignedCounters
                        FilterChip(
                            selected = isAssigned,
                            onClick = {
                                if (isAssigned) assignedCounters.remove(counter.number)
                                else assignedCounters.add(counter.number)
                            },
                            label = { Text("C${counter.number}") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && prefix.isNotBlank()) {
                        val waitMinutes = avgWaitText.toIntOrNull() ?: 5
                        onConfirm(name, prefix, desc, waitMinutes, "service", assignedCounters.toList())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Create Category")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditCategoryDialog(
    category: ServiceCategory,
    counters: List<CounterInfo>,
    onDismiss: () -> Unit,
    onConfirm: (ServiceCategory) -> Unit
) {
    var name by remember { mutableStateOf(category.name) }
    var prefix by remember { mutableStateOf(category.prefix) }
    var desc by remember { mutableStateOf(category.description) }
    var avgWaitText by remember { mutableStateOf(category.avgWaitMinutes.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Service Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Service Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = prefix,
                        onValueChange = { prefix = it.uppercase().take(3) },
                        label = { Text("Prefix") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = avgWaitText,
                        onValueChange = { avgWaitText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Avg Mins") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val waitMinutes = avgWaitText.toIntOrNull() ?: 5
                    onConfirm(
                        category.copy(
                            name = name.trim(),
                            prefix = prefix.trim(),
                            description = desc.trim(),
                            avgWaitMinutes = waitMinutes
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
