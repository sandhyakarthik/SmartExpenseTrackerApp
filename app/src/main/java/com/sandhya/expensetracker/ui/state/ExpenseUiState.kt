package com.sandhya.expensetracker.ui.state

import com.sandhya.expensetracker.domain.model.ExpenseDetail

/**
 * Created by Sandhya D on 2/6/2026.
 */
sealed class ExpenseUiState {
    object Loading : ExpenseUiState()
    data class Success(val expenses: List<ExpenseDetail>) : ExpenseUiState()
    object Empty : ExpenseUiState()
    data class Error(val message: String) : ExpenseUiState()
}
