package com.sandhya.expensetracker.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.domain.usecase.DeleteExpenseUseCase
import com.sandhya.expensetracker.domain.usecase.GetExpensesUseCase
import com.sandhya.expensetracker.ui.state.ExpenseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 *Created by  Sandhya D on 2/6/2026.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val getExpenses: GetExpensesUseCase, private val deleteExpense: DeleteExpenseUseCase) : ViewModel(){


    private val _uiState = MutableStateFlow<ExpenseUiState>(ExpenseUiState.Loading)
    val uiState: StateFlow<ExpenseUiState> = _uiState

    init {
        loadExpenses()
    }

    fun loadExpenses() {
        viewModelScope.launch {
            try {
                val expenses = getExpenses()
                _uiState.value =
                    if (expenses.isEmpty()) ExpenseUiState.Empty
                    else ExpenseUiState.Success(expenses)
            } catch (e: Exception) {
                _uiState.value = ExpenseUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun delete(expense: Expense) {
        viewModelScope.launch {
            deleteExpense(expense)
            loadExpenses()
        }
    }
}