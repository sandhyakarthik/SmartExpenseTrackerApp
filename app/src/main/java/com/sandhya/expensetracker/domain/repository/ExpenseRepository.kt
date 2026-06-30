package com.sandhya.expensetracker.domain.repository

import com.sandhya.expensetracker.data.local.CategorySummaryDto
import com.sandhya.expensetracker.data.local.MonthlySummaryDto
import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.domain.model.ExpenseDetail
import kotlinx.coroutines.flow.Flow

/**
 * Created by Sandhya D on 1/15/2026.
 */
interface ExpenseRepository {
    suspend fun addExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    fun getAllExpenses(): Flow<List<Expense>>
    fun getAllExpensesWithCategory(): Flow<List<ExpenseDetail>>
    fun getMonthlySummary(): Flow<MonthlySummaryDto>
    fun getTotalSummary(): Flow<MonthlySummaryDto>
    fun getCategorySummaries(): Flow<List<CategorySummaryDto>>
    // Existing functions...
    fun getCategorySummariesByDate(startTimestamp: Long, endTimestamp: Long): Flow<List<CategorySummaryDto>>
    fun getDefaultMonthlyCategorySummaries(): Flow<List<CategorySummaryDto>>
    fun getMonthlyCategorySummaries(): Flow<List<CategorySummaryDto>>
}
