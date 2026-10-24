package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.TicketStatus
import com.example.ui.components.QuickQueueTopBar
import com.example.ui.screens.staff.CategoriesSetupScreen
import com.example.ui.screens.staff.CounterDashboardScreen
import com.example.ui.screens.staff.QueueOverviewScreen
import com.example.ui.screens.staff.StaffLoginScreen
import com.example.ui.screens.user.JoinQueueScreen
import com.example.ui.screens.user.LiveBoardScreen
import com.example.ui.screens.user.MyQueueTicketScreen
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberDark
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealDeep
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.AppRole
import com.example.ui.viewmodel.CustomerScreen
import com.example.ui.viewmodel.QueueViewModel
import com.example.ui.viewmodel.StaffScreen
import com.example.util.QueueSoundEffects

@Composable
fun QuickQueueApp(
    viewModel: QueueViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val counters by viewModel.counters.collectAsStateWithLifecycle()
    val tickets by viewModel.tickets.collectAsStateWithLifecycle()
    val currentStaff by viewModel.currentStaff.collectAsStateWithLifecycle()
    val myActiveTicket by viewModel.myActiveTicket.collectAsStateWithLifecycle()

    // Sound chime reaction
    LaunchedEffect(uiState.alertToneTimestamp) {
        if (uiState.alertToneTimestamp != null) {
            QueueSoundEffects.playChime(context)
        }
    }

    // Auto-dismiss alert banner after 4 seconds
    LaunchedEffect(uiState.alertMessage) {
        if (uiState.alertMessage != null) {
            kotlinx.coroutines.delay(4000)
            viewModel.dismissAlert()
        }
    }

    // BackHandler routing
    if (uiState.role == AppRole.CUSTOMER && uiState.customerScreen != CustomerScreen.JOIN_QUEUE) {
        BackHandler {
            viewModel.setCustomerScreen(CustomerScreen.JOIN_QUEUE)
        }
    } else if (uiState.role == AppRole.STAFF && uiState.staffScreen != StaffScreen.DASHBOARD && uiState.staffScreen != StaffScreen.LOGIN) {
        BackHandler {
            viewModel.setStaffScreen(StaffScreen.DASHBOARD)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        val isTabletOrDesktop = maxWidth >= 700.dp

        Scaffold(
            topBar = {
                QuickQueueTopBar(
                    currentRole = uiState.role,
                    onRoleChange = { newRole -> viewModel.setRole(newRole) },
                    onResetDemo = { viewModel.resetDemoData() },
                    activeTicketCount = if (myActiveTicket != null) 1 else 0,
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                if (!isTabletOrDesktop) {
                    AppBottomNavigationBar(
                        role = uiState.role,
                        customerScreen = uiState.customerScreen,
                        staffScreen = uiState.staffScreen,
                        hasActiveTicket = myActiveTicket != null,
                        onSelectCustomerScreen = { viewModel.setCustomerScreen(it) },
                        onSelectStaffScreen = { viewModel.setStaffScreen(it) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isTabletOrDesktop) {
                    // Desktop/Tablet split with Navigation Rail on left
                    Row(modifier = Modifier.fillMaxSize()) {
                        AppNavigationRail(
                            role = uiState.role,
                            customerScreen = uiState.customerScreen,
                            staffScreen = uiState.staffScreen,
                            hasActiveTicket = myActiveTicket != null,
                            onSelectCustomerScreen = { viewModel.setCustomerScreen(it) },
                            onSelectStaffScreen = { viewModel.setStaffScreen(it) }
                        )

                        Box(modifier = Modifier.weight(1f)) {
                            MainContentSwitch(
                                viewModel = viewModel,
                                uiState = uiState,
                                categories = categories,
                                counters = counters,
                                tickets = tickets,
                                currentStaff = currentStaff,
                                myActiveTicket = myActiveTicket,
                                context = context
                            )
                        }
                    }
                } else {
                    // Mobile-first content
                    MainContentSwitch(
                        viewModel = viewModel,
                        uiState = uiState,
                        categories = categories,
                        counters = counters,
                        tickets = tickets,
                        currentStaff = currentStaff,
                        myActiveTicket = myActiveTicket,
                        context = context
                    )
                }

                // Global Top Alert Banner
                AnimatedVisibility(
                    visible = uiState.alertMessage != null,
                    enter = slideInVertically(initialOffsetY = { -it }),
                    exit = slideOutVertically(targetOffsetY = { -it }),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    uiState.alertMessage?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TealDeep,
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = AmberAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                IconButton(
                                    onClick = { viewModel.dismissAlert() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainContentSwitch(
    viewModel: QueueViewModel,
    uiState: com.example.ui.viewmodel.QueueUiState,
    categories: List<com.example.model.ServiceCategory>,
    counters: List<com.example.model.CounterInfo>,
    tickets: List<com.example.model.QueueTicket>,
    currentStaff: com.example.model.StaffProfile?,
    myActiveTicket: com.example.model.QueueTicket?,
    context: android.content.Context
) {
    if (uiState.role == AppRole.CUSTOMER) {
        when (uiState.customerScreen) {
            CustomerScreen.JOIN_QUEUE -> {
                JoinQueueScreen(
                    categories = categories,
                    tickets = tickets,
                    counters = counters,
                    myActiveTicket = myActiveTicket,
                    onJoinQueue = { catId, name, notes ->
                        viewModel.joinQueue(catId, name, notes)
                    },
                    onViewMyTicket = {
                        viewModel.setCustomerScreen(CustomerScreen.MY_TICKET)
                    }
                )
            }
            CustomerScreen.MY_TICKET -> {
                val servingInMyCategory = if (myActiveTicket != null) {
                    viewModel.getCurrentServingForCategory(myActiveTicket.categoryId)
                } else null
                val peopleAhead = if (myActiveTicket != null) {
                    viewModel.getPeopleAhead(myActiveTicket)
                } else 0

                MyQueueTicketScreen(
                    ticket = myActiveTicket,
                    currentlyServingTicket = servingInMyCategory,
                    peopleAheadCount = peopleAhead,
                    onBackToJoin = { viewModel.setCustomerScreen(CustomerScreen.JOIN_QUEUE) },
                    onCancelTicket = { viewModel.cancelMyTicket() },
                    onPlayChimeTest = { QueueSoundEffects.playChime(context) }
                )
            }
            CustomerScreen.LIVE_BOARD -> {
                LiveBoardScreen(
                    counters = counters,
                    tickets = tickets,
                    onSelectTicket = { ticketId ->
                        viewModel.selectExistingTicketAsMine(ticketId)
                    }
                )
            }
        }
    } else {
        // Staff Role
        when (uiState.staffScreen) {
            StaffScreen.LOGIN -> {
                StaffLoginScreen(
                    counters = counters,
                    currentStaff = currentStaff,
                    onLogin = { staff, counterNum ->
                        viewModel.loginStaff(staff, counterNum)
                    }
                )
            }
            StaffScreen.DASHBOARD -> {
                CounterDashboardScreen(
                    currentStaff = currentStaff,
                    counters = counters,
                    tickets = tickets,
                    categories = categories,
                    onCallNext = { counterNum -> viewModel.callNextTicket(counterNum) },
                    onRecall = { counterNum -> viewModel.recallCurrentTicket(counterNum) },
                    onSkip = { counterNum -> viewModel.skipCurrentTicket(counterNum) },
                    onComplete = { counterNum -> viewModel.completeCurrentTicket(counterNum) },
                    onToggleBreak = { counterNum -> viewModel.toggleBreak(counterNum) },
                    onChangeCounter = { counterNum -> viewModel.setStaffCounter(counterNum) }
                )
            }
            StaffScreen.OVERVIEW -> {
                QueueOverviewScreen(
                    stats = viewModel.getOverviewStats(),
                    categories = categories,
                    tickets = tickets,
                    counters = counters
                )
            }
            StaffScreen.CATEGORIES_SETUP -> {
                CategoriesSetupScreen(
                    categories = categories,
                    counters = counters,
                    onToggleActive = { catId -> viewModel.toggleCategoryActive(catId) },
                    onAddCategory = { name, prefix, desc, avgWait, icon, assigned ->
                        viewModel.addCategory(name, prefix, desc, avgWait, icon, assigned)
                    },
                    onUpdateCategory = { cat -> viewModel.updateCategory(cat) },
                    onResetDemoData = { viewModel.resetDemoData() }
                )
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(
    role: AppRole,
    customerScreen: CustomerScreen,
    staffScreen: StaffScreen,
    hasActiveTicket: Boolean,
    onSelectCustomerScreen: (CustomerScreen) -> Unit,
    onSelectStaffScreen: (StaffScreen) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceWhite,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("app_bottom_navigation")
    ) {
        if (role == AppRole.CUSTOMER) {
            NavigationBarItem(
                selected = customerScreen == CustomerScreen.JOIN_QUEUE,
                onClick = { onSelectCustomerScreen(CustomerScreen.JOIN_QUEUE) },
                icon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = "Join Queue") },
                label = { Text("Join Queue") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                ),
                modifier = Modifier.testTag("nav_join_queue")
            )

            NavigationBarItem(
                selected = customerScreen == CustomerScreen.MY_TICKET,
                onClick = { onSelectCustomerScreen(CustomerScreen.MY_TICKET) },
                icon = {
                    if (hasActiveTicket) {
                        BadgedBox(badge = { Badge(containerColor = AmberAccent) }) {
                            Icon(Icons.Default.HourglassBottom, contentDescription = "My Ticket")
                        }
                    } else {
                        Icon(Icons.Default.HourglassBottom, contentDescription = "My Ticket")
                    }
                },
                label = { Text("My Ticket") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                ),
                modifier = Modifier.testTag("nav_my_ticket")
            )

            NavigationBarItem(
                selected = customerScreen == CustomerScreen.LIVE_BOARD,
                onClick = { onSelectCustomerScreen(CustomerScreen.LIVE_BOARD) },
                icon = { Icon(Icons.Default.Campaign, contentDescription = "Lobby Display") },
                label = { Text("Live Board") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                ),
                modifier = Modifier.testTag("nav_live_board")
            )
        } else {
            // Staff Navigation
            NavigationBarItem(
                selected = staffScreen == StaffScreen.DASHBOARD,
                onClick = { onSelectStaffScreen(StaffScreen.DASHBOARD) },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Counter Station") },
                label = { Text("Counter") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                ),
                modifier = Modifier.testTag("nav_counter_dashboard")
            )

            NavigationBarItem(
                selected = staffScreen == StaffScreen.OVERVIEW,
                onClick = { onSelectStaffScreen(StaffScreen.OVERVIEW) },
                icon = { Icon(Icons.Default.ListAlt, contentDescription = "Queue Overview") },
                label = { Text("Overview") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                ),
                modifier = Modifier.testTag("nav_queue_overview")
            )

            NavigationBarItem(
                selected = staffScreen == StaffScreen.CATEGORIES_SETUP,
                onClick = { onSelectStaffScreen(StaffScreen.CATEGORIES_SETUP) },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Service Categories") },
                label = { Text("Services") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                ),
                modifier = Modifier.testTag("nav_categories_setup")
            )

            NavigationBarItem(
                selected = staffScreen == StaffScreen.LOGIN,
                onClick = { onSelectStaffScreen(StaffScreen.LOGIN) },
                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Officer Login") },
                label = { Text("Officer") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                ),
                modifier = Modifier.testTag("nav_staff_login")
            )
        }
    }
}

@Composable
fun AppNavigationRail(
    role: AppRole,
    customerScreen: CustomerScreen,
    staffScreen: StaffScreen,
    hasActiveTicket: Boolean,
    onSelectCustomerScreen: (CustomerScreen) -> Unit,
    onSelectStaffScreen: (StaffScreen) -> Unit
) {
    NavigationRail(
        containerColor = SurfaceWhite,
        modifier = Modifier.testTag("app_navigation_rail")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        if (role == AppRole.CUSTOMER) {
            NavigationRailItem(
                selected = customerScreen == CustomerScreen.JOIN_QUEUE,
                onClick = { onSelectCustomerScreen(CustomerScreen.JOIN_QUEUE) },
                icon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = "Join Queue") },
                label = { Text("Join Queue") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                )
            )

            NavigationRailItem(
                selected = customerScreen == CustomerScreen.MY_TICKET,
                onClick = { onSelectCustomerScreen(CustomerScreen.MY_TICKET) },
                icon = {
                    if (hasActiveTicket) {
                        BadgedBox(badge = { Badge(containerColor = AmberAccent) }) {
                            Icon(Icons.Default.HourglassBottom, contentDescription = "My Ticket")
                        }
                    } else {
                        Icon(Icons.Default.HourglassBottom, contentDescription = "My Ticket")
                    }
                },
                label = { Text("My Ticket") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                )
            )

            NavigationRailItem(
                selected = customerScreen == CustomerScreen.LIVE_BOARD,
                onClick = { onSelectCustomerScreen(CustomerScreen.LIVE_BOARD) },
                icon = { Icon(Icons.Default.Campaign, contentDescription = "Lobby Display") },
                label = { Text("Live Board") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                )
            )
        } else {
            NavigationRailItem(
                selected = staffScreen == StaffScreen.DASHBOARD,
                onClick = { onSelectStaffScreen(StaffScreen.DASHBOARD) },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Counter Station") },
                label = { Text("Counter") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                )
            )

            NavigationRailItem(
                selected = staffScreen == StaffScreen.OVERVIEW,
                onClick = { onSelectStaffScreen(StaffScreen.OVERVIEW) },
                icon = { Icon(Icons.Default.ListAlt, contentDescription = "Queue Overview") },
                label = { Text("Overview") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                )
            )

            NavigationRailItem(
                selected = staffScreen == StaffScreen.CATEGORIES_SETUP,
                onClick = { onSelectStaffScreen(StaffScreen.CATEGORIES_SETUP) },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Service Categories") },
                label = { Text("Services") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                )
            )

            NavigationRailItem(
                selected = staffScreen == StaffScreen.LOGIN,
                onClick = { onSelectStaffScreen(StaffScreen.LOGIN) },
                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Officer Login") },
                label = { Text("Officer") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer
                )
            )
        }
    }
}
