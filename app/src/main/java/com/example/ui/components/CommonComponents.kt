package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TicketStatus
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberDark
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusBlueContainer
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
import com.example.ui.viewmodel.AppRole

@Composable
fun QuickQueueTopBar(
    currentRole: AppRole,
    onRoleChange: (AppRole) -> Unit,
    onResetDemo: () -> Unit,
    activeTicketCount: Int = 0,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceWhite,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Branding
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("app_branding_row")
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(TealPrimary, TealDeep)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = "QuickQueue Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Quick",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        )
                        Text(
                            text = "Queue",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                        )
                    }
                    Text(
                        text = if (currentRole == AppRole.CUSTOMER) "Customer Portal" else "Staff Counter Station",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Role Switcher & Menu
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Role Toggle Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceSubtle,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RolePillOption(
                            text = "User",
                            icon = Icons.Default.Person,
                            isSelected = currentRole == AppRole.CUSTOMER,
                            onClick = { onRoleChange(AppRole.CUSTOMER) },
                            testTag = "role_customer_toggle"
                        )
                        RolePillOption(
                            text = "Staff",
                            icon = Icons.Default.AdminPanelSettings,
                            isSelected = currentRole == AppRole.STAFF,
                            onClick = { onRoleChange(AppRole.STAFF) },
                            testTag = "role_staff_toggle"
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.testTag("menu_more_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings and utilities",
                            tint = TextMedium
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(SurfaceWhite)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Reset Demo Data", color = TextDark) },
                            leadingIcon = {
                                Icon(Icons.Default.RestartAlt, contentDescription = null, tint = TealPrimary)
                            },
                            onClick = {
                                menuExpanded = false
                                onResetDemo()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RolePillOption(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) TealPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else TextMedium,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextMedium,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun StatusBadge(status: TicketStatus, modifier: Modifier = Modifier) {
    val (bg, textColor, icon) = when (status) {
        TicketStatus.WAITING -> Triple(StatusBlueContainer, StatusBlue, Icons.Default.AccessTime)
        TicketStatus.ALMOST_TURN -> Triple(AmberContainer, AmberDark, Icons.Default.HourglassBottom)
        TicketStatus.SERVING -> Triple(TealContainer, TealDark, Icons.Default.NotificationsActive)
        TicketStatus.COMPLETED -> Triple(StatusSuccessContainer, StatusSuccess, Icons.Default.CheckCircle)
        TicketStatus.SKIPPED -> Triple(StatusErrorContainer, StatusError, Icons.Default.PlayArrow)
        TicketStatus.CANCELLED -> Triple(SurfaceSubtle, TextMuted, Icons.Default.RestartAlt)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
fun SubtlePulsingRing(
    isPulsing: Boolean,
    pulseColor: Color = AmberAccent,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (!isPulsing) {
        content()
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // Outer glowing pulse ring
        Box(
            modifier = Modifier
                .matchParentSize()
                .scale(scale)
                .clip(RoundedCornerShape(28.dp))
                .background(pulseColor.copy(alpha = ringAlpha))
        )
        content()
    }
}

fun getCategoryIcon(key: String): ImageVector {
    return when (key.lowercase()) {
        "cashier" -> Icons.Default.CreditCard
        "service" -> Icons.Default.SupportAgent
        "consultation" -> Icons.Default.People
        "express" -> Icons.Default.PlayArrow
        "support" -> Icons.Default.Headphones
        else -> Icons.Default.ConfirmationNumber
    }
}
