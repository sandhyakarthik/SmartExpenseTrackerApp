package com.sandhya.expensetracker.ui.domain.repository

import com.sandhya.expensetracker.ui.domain.model.Expense

/**
 *Created by  Sandhya D on 1/15/2026.
 */
interface ExpenseRepository {
   suspend fun addExpense(expense : Expense)
    suspend fun deleteExpense(expense: Expense)
    suspend fun getAllExpenses() : List<Expense>
}