package com.sajithrajan.pisave.dataBase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val dao: ExpenseDAO,
) : ViewModel() {

    private val _sortType = MutableStateFlow(SortType.DATE)
    private val _state = MutableStateFlow(ExpenseState())
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _expense = _sortType
        .flatMapLatest { sortType->
            when(sortType){
                SortType.TITLE -> dao.getExpensesOrderedByTitle()
                SortType.AMOUNT -> dao.getExpensesOrderedByAmount()
                SortType.DATE -> dao.getExpensesOrderedByDate()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList() )

    val state = combine(_state,_sortType,_expense){state,sortType,expense ->
        state.copy(
            expenselist = expense,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExpenseState())


    fun onEvent(event: ExpenseEvent) {
        when (event) {
            is ExpenseEvent.DeleteExpense -> {
                viewModelScope.launch {
                    dao.deleteExpense(event.expense)
                }
            }
            ExpenseEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingExpense = false
                ) }
            }
            ExpenseEvent.SaveExpense -> {
                val title = state.value.title
//                val categoryIcon = state.value.categoryIcon
                val category = state.value.category
                val note = state.value.note
                val amount = state.value.amount
                val date = System.currentTimeMillis() // Capture the current time as the date

                // Validation check to ensure mandatory fields are not blank or invalid
                if (title.isBlank() || category.isBlank() || amount <= 0.0) {
                    return
                }

                // Create an Expense object with the given values
                val expense = Expense(
                    title = title,
//                    categoryIcon = null,
                    category = category,
                    note = note,
                    amount = amount,
                    date = date
                )
                viewModelScope.launch {
                    dao.upsertExpense(expense)
                }

                _state.update { it.copy(
                    isAddingExpense = false,
                    title = "",
                    category = "",
//                    categoryIcon = null, // Set an appropriate default
                    note = "",
                    amount = 0.0,
                    date = 0L
                ) }
            }
            is ExpenseEvent.SetExpenseAmount -> {
                _state.update { it.copy(
                    amount = event.amount
                ) }
            }
            is ExpenseEvent.SetExpenseTitle ->  {
                _state.update { it.copy(
                    title = event.title
                ) }
            }
            ExpenseEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingExpense = true
                ) }
            }
            is ExpenseEvent.SortExpense -> {
                _sortType.value = event.sortType
            }
            is ExpenseEvent.SetExpenseCategory -> {
                _state.update { it.copy(
                     category =  event.category
                ) }
            }
        }
    }
}