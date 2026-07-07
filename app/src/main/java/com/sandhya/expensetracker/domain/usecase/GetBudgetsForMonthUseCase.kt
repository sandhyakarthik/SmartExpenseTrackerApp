package com.sandhya.expensetracker.domain.usecase

import com.sandhya.expensetracker.domain.model.Budget
import com.sandhya.expensetracker.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Sandhya D on 2/6/2026.
 */
class GetBudgetsForMonthUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    operator fun invoke(month: Int, year: Int): Flow<List<Budget>> = 
        repository.getBudgetsForMonth(month, year)
}
