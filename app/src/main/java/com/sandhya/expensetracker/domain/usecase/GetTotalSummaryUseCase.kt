package com.sandhya.expensetracker.domain.usecase

import com.sandhya.expensetracker.domain.model.MonthlySummary
import com.sandhya.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by Sandhya D on 2/6/2026.
 */
class GetTotalSummaryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<MonthlySummary> {
        return repository.getTotalSummary().map {
            MonthlySummary(it.totalSpent, it.totalTransactions)
        }
    }
}
