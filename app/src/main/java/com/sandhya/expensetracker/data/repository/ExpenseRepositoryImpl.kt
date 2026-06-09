package com.sandhya.expensetracker.data.repository

import com.sandhya.expensetracker.data.local.CategorySummaryDto
import com.sandhya.expensetracker.data.local.ExpenseDao
import com.sandhya.expensetracker.data.local.MonthlySummaryDto
import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 *Created by  Sandhya D on 2/4/2026.
 */
class ExpenseRepositoryImpl( private val dao: ExpenseDao): ExpenseRepository
{
    override suspend fun addExpense(expense: Expense) {
        dao.insertExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense.toEntity())
    }

    override fun getAllExpenses(): Flow<List<Expense>> {
        return dao.getAllExpenses().map { entityList ->
            // This loops through the List inside the Flow container
            entityList.map { entity -> entity.toDomain() }
        }
    }

    override fun getMonthlySummary(): Flow<MonthlySummaryDto> {
        return dao.getMonthlySummary()
    }

    override fun getCategorySummaries(): Flow<List<CategorySummaryDto>> {
        return dao.getCategorySummaries()
    }
}