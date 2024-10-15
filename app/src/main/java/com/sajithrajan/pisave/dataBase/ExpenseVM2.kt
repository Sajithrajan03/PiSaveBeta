package com.sajithrajan.pisave.dataBase

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExpenseViewModel2(private val repository: ExpenseRepository) : ViewModel() {

    val allExpenses: LiveData<List<Expense>> = repository.getAllExpenses()

    // Insert or update expense via the repository
    fun upsertExpense(expense: Expense) {
        viewModelScope.launch {
            repository.upsert(expense)
        }
    }
}
