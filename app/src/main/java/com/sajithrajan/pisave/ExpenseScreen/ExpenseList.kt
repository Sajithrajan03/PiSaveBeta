package com.sajithrajan.pisave.ExpenseScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseEvent
import com.sajithrajan.pisave.dataBase.ExpenseState
import com.sajithrajan.pisave.expenses
import com.sajithrajan.pisave.ui.theme.DarkBlue
import com.sajithrajan.pisave.ui.theme.LightBlue
import com.sajithrajan.pisave.ui.theme.White


@Composable
fun ExpenseList(expenseList: List<Expense> = expenses,
                state: ExpenseState,
                onEvent: (ExpenseEvent) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF263252))
    ) {
        items(expenseList.size) { index ->
            ExpenseItem(expense = expenseList[index],state,onEvent)
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense,
                state: ExpenseState,
                onEvent: (ExpenseEvent) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = White,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f).padding(start = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Card(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(DarkBlue)

                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize().background(DarkBlue) // Ensure the Box fills the entire Card
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shop,
                            contentDescription = "Icon",
                            tint = Color.White,
                            modifier = Modifier.size(35.dp) // Size of the Icon
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = expense.category,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DarkBlue,
                    )
                    expense.note?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = LightBlue
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.align(Alignment.CenterVertically).padding(end=16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
//                        horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${expense.currency} ${expense.amount}",
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue,
                    fontSize = 20.sp
                )
                Text(
                    text = "${expense.date}",
                    fontSize = 12.sp,
                    color = DarkBlue,
                )
            }
            IconButton(onClick = {
                onEvent(ExpenseEvent.DeleteExpense(expense))
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier.size(35.dp))
            }
        }
    }
}
