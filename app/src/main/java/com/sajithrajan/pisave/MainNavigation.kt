package com.sajithrajan.pisave

import LightControlChat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Adb
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.utils.noRippleClickable
import com.sajithrajan.pisave.ExpenseScreen.ExpenseScreen
import com.sajithrajan.pisave.dataBase.ExpenseViewModel


data class TabItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)
enum class NavigationBarItems(val icon: ImageVector) {
    Person(icon = Icons.Outlined.Dashboard,),
    Call(icon =  Icons.Outlined.Adb),
    Settings(icon = Icons.Outlined.Receipt)
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

@Composable
fun MainNavigationScreen(viewModel: ExpenseViewModel ) {
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
        bottomBar = {
            AnimatedNavigationBar(
                modifier = Modifier.height(64.dp),
                selectedIndex = selectedTabIndex,
//                cornerRadius = shapeCornerRadius(cornerRadius = 34.dp),
                ballAnimation = Parabolic(tween(durationMillis = 1)),
                indentAnimation = Height(tween(durationMillis = 1)),
                barColor = MaterialTheme.colorScheme.surface,
                ballColor = MaterialTheme.colorScheme.primary
            ) {
                navigationBarItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable { selectedTabIndex = item.ordinal },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(26.dp),
                            imageVector = item.icon,
                            contentDescription = "Bottom Bar Icon",
                            tint = if (selectedTabIndex == item.ordinal) MaterialTheme.colorScheme.secondary else {
                                MaterialTheme.colorScheme.surfaceTint
                            }

                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
        ) { page ->

            when (page) {
                0 -> LightControlChat()
                1 -> ChatBotScreen(expenses = expenses)
                2 -> ExpenseScreen( state = state, onEvent = viewModel::onEvent , expenseList = state.expenselist)
            }
        }
    }
}
