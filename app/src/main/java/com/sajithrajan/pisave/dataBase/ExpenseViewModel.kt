package com.sajithrajan.pisave.dataBase


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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


    val state = combine(_state, _sortType, _expenseFlow) { state, sortType, expenses ->
        state.copy(
            expenselist = expenses,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExpenseState())


    fun upsertExpense(expense: Expense) {
        viewModelScope.launch {
            repository.upsert(expense)
        }
    }


    fun getCategoryIconName(categoryId: Int, callback: (String?) -> Unit) {
        viewModelScope.launch {
            val category = repository.getCategoryById(categoryId)
            callback(category?.iconName)
        }
    }


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

    fun addExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upsert(expense)
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
    fun getTopExpensesForCurrentDay(): LiveData<List<Expense>> {
        return repository.getTopExpensesForCurrentDay()
    }

    // LiveData for top expenses for the current month
    fun getTopExpensesForCurrentMonth(): LiveData<List<Expense>> {
        return repository.getTopExpensesForCurrentMonth()
    }

    // LiveData for top expenses for the current year
    fun getTopExpensesForCurrentYear(): LiveData<List<Expense>> {
        return repository.getTopExpensesForCurrentYear()
    }
    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactions: StateFlow<List<TransactionEntity>> = _transactions


    fun fetchTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            val transactionList = repository.getAllTransactions()
            _transactions.value = transactionList
        }
    }

    fun addTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTransaction(transaction)
            fetchTransactions() // Refresh the list after adding
        }
    }
    fun isDuplicateTransaction(transaction: TransactionEntity): Boolean {
        val currentTransactions = _transactions.value
        return currentTransactions.any {
            it.title == transaction.title &&
                    it.date == transaction.date &&
                    it.amount == transaction.amount
        }

    }
    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(transaction)
            fetchTransactions() // Refresh the list after deletion
        }
    }

    fun deleteAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTransactions()
            fetchTransactions() // Refresh the list after deletion
        }
    }
    fun uploadTransactionAsExpense(transaction: TransactionEntity) {
        val expense = Expense(
            title = transaction.title,
            category = transaction.category,
            note = transaction.note,
            amount = transaction.amount,
            currency = transaction.currency,
            date = transaction.date
        )
        addExpense(expense)
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateExpense(expense)
        }
    }
    fun saveSplitExpenses(expenseId: Int, splitData: Map<String, Double>) {
        viewModelScope.launch {
            repository.saveSplitExpenses(expenseId, splitData)
        }
    }

    suspend fun getSplitExpenses(expenseId: Int): List<SplitExpenseEntity> {
        return repository.getSplitExpensesForExpense(expenseId)
    }

    fun deleteSplitExpenses(expenseId: Int) {
        viewModelScope.launch {
            repository.deleteSplitExpensesForExpense(expenseId)
        }
    }
    fun insertReceipt(receipt: ReceiptEntity) {
        viewModelScope.launch {
            repository.insertReceipt(receipt)
        }
    }

    suspend fun getReceiptsForExpense(expenseId: Int): List<ReceiptEntity> {
        return repository.getReceiptsForExpense(expenseId)
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

