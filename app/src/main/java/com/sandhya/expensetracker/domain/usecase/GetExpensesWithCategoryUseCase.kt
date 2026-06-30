package com.sandhya.expensetracker.domain.usecase

import com.sandhya.expensetracker.domain.model.ExpenseDetail
import com.sandhya.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Sandhya D on 2/6/2026.
 */
class GetExpensesWithCategoryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<ExpenseDetail>> =
        repository.getAllExpensesWithCategory()
}
