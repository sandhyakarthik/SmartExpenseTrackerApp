package com.sandhya.expensetracker.domain.usecase

import com.sandhya.expensetracker.domain.model.CategorySummary
import com.sandhya.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by Sandhya D on 2/6/2026.
 */
class GetMonthlyCategorySummaryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<CategorySummary>> {
        return repository.getMonthlyCategorySummaries().map { list ->
            list.map { CategorySummary(it.category, it.totalAmount) }
        }
    }
}
