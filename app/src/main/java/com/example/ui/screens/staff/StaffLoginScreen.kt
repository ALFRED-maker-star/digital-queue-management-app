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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Countertops
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.QueueRepository
import com.example.model.CounterInfo
import com.example.model.StaffProfile
import com.example.ui.theme.AmberAccent
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
fun StaffLoginScreen(
    counters: List<CounterInfo>,
    currentStaff: StaffProfile?,
    onLogin: (StaffProfile, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleStaff = QueueRepository.sampleStaffList
    var selectedStaff by remember(currentStaff) {
        mutableStateOf(currentStaff ?: sampleStaff[1]) // Alex Rivera default
    }
    var selectedCounterNumber by remember(currentStaff) {
        mutableIntStateOf(currentStaff?.defaultCounter ?: 2)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TealContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Counter Officer Login",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        )
                        Text(
                            text = "Sign in to manage your counter and call tickets",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }
            }

            // Staff Profile Picker
            item {
                Text(
                    text = "SELECT OFFICER PROFILE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )
            }

            items(sampleStaff, key = { it.id }) { staff ->
                val isSelected = staff.id == selectedStaff.id
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) TealPrimary else SurfaceBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("staff_select_${staff.id}")
                        .clickable {
                            selectedStaff = staff
                            selectedCounterNumber = staff.defaultCounter
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(staff.avatarColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = staff.name.split(" ").map { it.take(1) }.joinToString(""),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = staff.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                )
                                Text(
                                    text = "${staff.roleTitle} • Default Counter ${staff.defaultCounter}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = TealPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Assigned Counter Selector
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ASSIGNED COUNTER STATION",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    counters.forEach { counter ->
                        val isSelected = counter.number == selectedCounterNumber
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) TealPrimary else SurfaceWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) TealPrimary else SurfaceBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("counter_pill_${counter.number}")
                                .clickable { selectedCounterNumber = counter.number }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Counter",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextMuted,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = "${counter.number}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) Color.White else TextDark
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Open Counter Button
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        onLogin(selectedStaff, selectedCounterNumber)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("open_counter_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Open Counter $selectedCounterNumber & Start Serving",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
