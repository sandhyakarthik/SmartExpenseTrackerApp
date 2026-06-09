package com.sandhya.expensetracker.domain.repository

import com.sandhya.expensetracker.data.local.MonthlySummaryDto
import com.sandhya.expensetracker.domain.model.Expense
import kotlinx.coroutines.flow.Flow

/**
 *Created by  Sandhya D on 1/15/2026.
 */
interface ExpenseRepository {
    suspend fun addExpense(expense: Expense)     // Must be suspend
    suspend fun deleteExpense(expense: Expense)  // Must be suspend
    fun getAllExpenses(): Flow<List<Expense>>
    fun getMonthlySummary(): Flow<MonthlySummaryDto>
}
/*interface ExpenseRepository {
    fun addExpense(expense : Expense)
     fun deleteExpense(expense: Expense)
     fun getAllExpenses() : Flow<List<Expense>>
}*/
