package com.sandhya.expensetracker.data.repository

import com.sandhya.expensetracker.data.local.BudgetDao
import com.sandhya.expensetracker.domain.model.Budget
import com.sandhya.expensetracker.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by Sandhya D on 2/6/2026.
 */
class BudgetRepositoryImpl @Inject constructor(
    private val dao: BudgetDao
) : BudgetRepository {

    override suspend fun insertBudget(budget: Budget) {
        dao.insertBudget(budget.toEntity())
    }

    override suspend fun updateBudget(budget: Budget) {
        dao.updateBudget(budget.toEntity())
    }

    override suspend fun deleteBudget(budget: Budget) {
        dao.deleteBudget(budget.toEntity())
    }

    override fun getBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>> {
        return dao.getBudgetsForMonth(month, year).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getBudgetByCategoryAndMonth(
        categoryId: Long,
        month: Int,
        year: Int
    ): Budget? {
        return dao.getBudgetByCategoryAndMonth(categoryId, month, year)?.toDomain()
    }

    override fun getAllBudgets(): Flow<List<Budget>> {
        return dao.getAllBudgets().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
