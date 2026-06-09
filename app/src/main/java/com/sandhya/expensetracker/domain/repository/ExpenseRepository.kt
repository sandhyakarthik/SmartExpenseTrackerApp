package com.sandhya.expensetracker.domain.repository

import com.sandhya.expensetracker.domain.model.Expense
import kotlinx.coroutines.flow.Flow

/**
 *Created by  Sandhya D on 1/15/2026.
 */
interface ExpenseRepository {
    fun addExpense(expense : Expense)
     fun deleteExpense(expense: Expense)
     fun getAllExpenses() : Flow<List<Expense>>
}
