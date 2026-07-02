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
    operator fun invoke(startTimestamp: Long? = null,
                        endTimestamp: Long? = null): Flow<List<CategorySummary>>
    {
       /* return repository.getMonthlyCategorySummaries().map { list ->
            list.map { CategorySummary(it.category, it.totalAmount)
            }
        }*/
        val flow = if (startTimestamp != null && endTimestamp != null) {
            repository.getCategorySummariesByDate(startTimestamp, endTimestamp)
        } else {
            repository.getDefaultMonthlyCategorySummaries()
        }

        // Map your database DTO to your clean UI Domain Model if needed
        return flow.map { dtoList ->
            dtoList.map { CategorySummary(category = it.category, totalAmount = it.totalAmount, transactionCount = it.transactionCount) }
        }
    }
}
