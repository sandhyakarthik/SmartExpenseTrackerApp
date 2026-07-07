package com.sandhya.expensetracker.domain.usecase

import com.sandhya.expensetracker.domain.model.Budget
import com.sandhya.expensetracker.domain.repository.BudgetRepository
import javax.inject.Inject

/**
 * Created by Sandhya D on 2/6/2026.
 */
class DeleteBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget) = repository.deleteBudget(budget)
}
