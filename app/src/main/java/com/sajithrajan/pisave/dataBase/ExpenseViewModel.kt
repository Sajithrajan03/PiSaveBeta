package com.sajithrajan.pisave.dataBase


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    // LiveData to observe all expenses
    val allExpenses: LiveData<List<Expense>> = repository.getAllExpenses()

    // State management for sorting expenses
    private val _sortType = MutableStateFlow(SortType.DATE)
    private val _state = MutableStateFlow(ExpenseState())

    private val _expenseFlow = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                SortType.TITLE -> repository.getExpensesOrderedByTitle()
                SortType.AMOUNT -> repository.getExpensesOrderedByAmount()
                SortType.DATE -> repository.getExpensesOrderedByDate()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // Combine state and sorted expenses
    val state = combine(_state, _sortType, _expenseFlow) { state, sortType, expenses ->
        state.copy(
            expenselist = expenses,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExpenseState())

    // Insert or update an expense
    fun upsertExpense(expense: Expense) {
        viewModelScope.launch {
            repository.upsert(expense)
        }
    }

    // Handle category fetching
    fun getCategoryIconName(categoryId: Int, callback: (String?) -> Unit) {
        viewModelScope.launch {
            val category = repository.getCategoryById(categoryId)
            callback(category?.iconName)
        }
    }

    // Process various events
    fun onEvent(event: ExpenseEvent) {
        when (event) {
            is ExpenseEvent.DeleteExpense -> {
                viewModelScope.launch {
                    repository.deleteExpense(event.expense)
                }
            }
            ExpenseEvent.HideDialog -> {
                _state.update { it.copy(isAddingExpense = false) }
            }
            ExpenseEvent.SaveExpense -> {
                val title = state.value.title
                val amount = state.value.amount
                if (title.isBlank() || amount <= 0.0) return

                val expense = Expense(
                    title = title,
                    amount = amount,
                    note = state.value.note,
                    category = state.value.category,
                    date = state.value.date
                )

                upsertExpense(expense)
                _state.update { it.copy(isAddingExpense = false, title = "", note = "", amount = 0.0, date = 0L) }
            }
            is ExpenseEvent.SetExpenseAmount -> _state.update { it.copy(amount = event.amount) }
            is ExpenseEvent.SetExpenseTitle -> _state.update { it.copy(title = event.title) }
            is ExpenseEvent.SetExpenseNote -> _state.update { it.copy(note = event.note) }
            is ExpenseEvent.SetExpenseDate -> _state.update { it.copy(date = event.date) }
            ExpenseEvent.ShowDialog -> _state.update { it.copy(isAddingExpense = true) }
            is ExpenseEvent.SortExpense -> _sortType.value = event.sortType
            is ExpenseEvent.SetExpenseCategory -> _state.update { it.copy(category = event.category) }
        }
    }


    fun getCategoryWiseSpendingForMonth(month: String): LiveData<List<CategorySpendingTable>> {
        return repository.getCategoryWiseSpendingForMonth(month)
    }

    fun getCategoryWiseSpendingForYear(year: String): LiveData<List<CategorySpendingTable>> {
        return repository.getCategoryWiseSpendingForYear(year)
    }

    fun getCategoryWiseSpendingForDay(day: String): LiveData<List<CategorySpendingTable>> {
        return repository.getCategoryWiseSpendingForDay(day)
    }

    fun getTotalExpensesForDay(): LiveData<List<DailyExpense>> {
        return repository.getAllDailyExpenses()
    }
}



fun getStartOfDay(timeMillis: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timeMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

fun getEndOfDay(timeMillis: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timeMillis
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    return calendar.timeInMillis
}
