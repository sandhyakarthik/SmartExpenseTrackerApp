package com.sandhya.expensetracker.ui.state

import com.sandhya.expensetracker.domain.model.Expense

/**
 *Created by  Sandhya D on 2/6/2026.
 */
sealed class ExpenseUiState {
    object Loading : ExpenseUiState()
    data class Success(val expenses: List<Expense>) : ExpenseUiState()
    object Empty : ExpenseUiState()
    data class Error(val message: String) : ExpenseUiState()
}