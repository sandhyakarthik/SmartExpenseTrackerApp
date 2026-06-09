package com.sandhya.expensetracker.domain.usecase

import com.sandhya.expensetracker.domain.model.CategorySummary
import com.sandhya.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 *Created by  Sandhya D on 6/9/2026.
 */
class GetCategorySummaryUseCase @Inject constructor
    (private val repository: ExpenseRepository)
{
    // 1. No suspend keyword needed because returning a Flow stream reference is instant
    // 2. Maps the database DTO list directly into your clean domain models
    operator fun invoke(): Flow<List<CategorySummary>> {
        return repository.getCategorySummaries().map { dtoList ->
            dtoList.map { dto ->
                CategorySummary(
                    category = dto.category,
                    totalAmount = dto.totalAmount
                )
            }
        }
    }
}