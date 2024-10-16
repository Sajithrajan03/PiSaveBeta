package com.sajithrajan.pisave


import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import com.exyte.animatednavbar.utils.noRippleClickable
import com.sajithrajan.pisave.ExpenseScreen.ExpenseScreen
import com.sajithrajan.pisave.dataBase.ExpenseViewModel
import com.sajithrajan.pisave.profile.ProfileScreen


data class TabItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)
enum class NavigationBarItems(val icon: ImageVector) {
    Dashboard(icon = Icons.Default.Dashboard),
    Chatbot(icon = Icons.Filled.Memory),
    ExpenseList(icon = Icons.Default.Receipt)
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTopBar(onNavigateToProfile: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "PiSave",
                style = MaterialTheme.typography.headlineLarge
            )
        },
        actions={
            IconButton(
                onClick = onNavigateToProfile,
                modifier = Modifier.align(Alignment.CenterVertically)  // Align delete button vertically
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    )
}
@Composable
fun MainNavigationScreen(viewModel: ExpenseViewModel , onNavigateToProfile: () -> Unit) {
    var selectedTabIndex by remember {
        mutableIntStateOf(1)
    }

    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 3 } // Number of tabs/pages
    )


    val navigationBarItems = remember { NavigationBarItems.values() }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(
            page = selectedTabIndex,
            animationSpec = tween(durationMillis = 150)
        )
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }



    Scaffold(
        topBar = {ScreenTopBar(onNavigateToProfile)},
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(64.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                tonalElevation = 5.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navigationBarItems.forEachIndexed { index, item ->
                        val isSelected = selectedTabIndex == index
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .noRippleClickable { selectedTabIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                // Adding the glow effect
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                            shape = CircleShape
                                        )
                                        .align(Alignment.Center)
                                )
                            }
                            Icon(
                                modifier = Modifier
                                    .size(if (isSelected) 30.dp else 26.dp),
                                imageVector = item.icon,
                                contentDescription = "Bottom Bar Icon",
                                tint = if (isSelected) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.surfaceTint
                                }
                            )
                        }
                    }
                }
            }
        }

    ) { paddingValues ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(rememberNestedScrollInteropConnection())
        ) { page ->

            when (page) {
                0 -> DashBoardMain( )
                1 -> ChatBotScreen( state = state, onEvent = viewModel::onEvent , expenseList = state.expenselist)
                2 -> ExpenseScreen( state = state, onEvent = viewModel::onEvent , expenseList = state.expenselist)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: ExpenseViewModel) {
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> MainNavigationScreen(viewModel = viewModel, onNavigateToProfile = { currentScreen = "profile" })
        "profile" -> ProfileScreen(onNavigateToHome = { currentScreen = "home" })
    }
}