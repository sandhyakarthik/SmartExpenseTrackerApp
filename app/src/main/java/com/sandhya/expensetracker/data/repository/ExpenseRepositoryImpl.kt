package com.sandhya.expensetracker.data.repository

import com.sandhya.expensetracker.data.local.CategorySummaryDto
import com.sandhya.expensetracker.data.local.ExpenseDao
import com.sandhya.expensetracker.data.local.MonthlySummaryDto
import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.domain.model.ExpenseDetail
import com.sandhya.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by Sandhya D on 2/4/2026.
 */
class ExpenseRepositoryImpl(private val dao: ExpenseDao) : ExpenseRepository {
    override suspend fun addExpense(expense: Expense) {
        dao.insertExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense.toEntity())
    }

    override fun getAllExpenses(): Flow<List<Expense>> {
        return dao.getAllExpenses().map { entityList ->
            entityList.map { entity -> entity.toDomain() }
        }
    }

    override fun getAllExpensesWithCategory(): Flow<List<ExpenseDetail>> {
        return dao.getAllExpensesWithCategory().map { list ->
            list.map { it.toDetailDomain() }
        }
    }

    override fun getMonthlySummary(): Flow<MonthlySummaryDto> {
        return dao.getMonthlySummary()
    }

    override fun getTotalSummary(): Flow<MonthlySummaryDto> {
        return dao.getTotalSummary()
    }

    override fun getCategorySummaries(): Flow<List<CategorySummaryDto>> {
        return dao.getCategorySummaries()
    }

   override fun getCategorySummariesByDate(startTimestamp: Long, endTimestamp: Long): Flow<List<CategorySummaryDto>> {
       return dao.getCategorySummariesByDate(startTimestamp, endTimestamp)
   }

    override fun getDefaultMonthlyCategorySummaries(): Flow<List<CategorySummaryDto>> {
        return dao.getMonthlyCategorySummaries() // Maps to your current-month DAO query
    }

    override fun getMonthlyCategorySummaries(): Flow<List<CategorySummaryDto>> {
        return dao.getMonthlyCategorySummaries()
    }

    override fun getTotalSpentInRange(start: Long, end: Long): Flow<Double> {
        return dao.getTotalSpentInRange(start, end).map { it ?: 0.0 }
    }
}
