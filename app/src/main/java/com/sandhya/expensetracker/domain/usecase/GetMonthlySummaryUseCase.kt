package com.sandhya.expensetracker.domain.usecase

import com.sandhya.expensetracker.domain.model.MonthlySummary
import com.sandhya.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 *Created by  Sandhya D on 6/9/2026.
 */
class GetMonthlySummaryUseCase @Inject constructor(// 2. Add @Inject constructor here
    private val repository: ExpenseRepository
) {
    // 1. Remove 'suspend' because returning a Flow is instantaneous
    // 2. Wrap the return type in a Flow
    operator fun invoke(): Flow<MonthlySummary> {

        // 3. Map the DTO coming from the repository into your Domain Model
        return repository.getMonthlySummary().map { dto ->
            MonthlySummary(
                totalSpent = dto.totalSpent,
                totalTransactions = dto.totalTransactions
            )
        }
    }
}