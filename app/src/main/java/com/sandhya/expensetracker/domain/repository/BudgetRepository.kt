package com.sandhya.expensetracker.domain.repository

import com.sandhya.expensetracker.domain.model.Budget
import kotlinx.coroutines.flow.Flow

/**
 * Created by Sandhya D on 2/6/2026.
 */
interface BudgetRepository {
    suspend fun insertBudget(budget: Budget)
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>>
    suspend fun getBudgetByCategoryAndMonth(categoryId: Long, month: Int, year: Int): Budget?
    fun getAllBudgets(): Flow<List<Budget>>
}
