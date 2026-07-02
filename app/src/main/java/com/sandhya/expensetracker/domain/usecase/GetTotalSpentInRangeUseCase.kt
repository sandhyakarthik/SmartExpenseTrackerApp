package com.sandhya.expensetracker.domain.usecase

import com.sandhya.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Sandhya D on 2/6/2026.
 */
class GetTotalSpentInRangeUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(start: Long, end: Long): Flow<Double> =
        repository.getTotalSpentInRange(start, end)
}
