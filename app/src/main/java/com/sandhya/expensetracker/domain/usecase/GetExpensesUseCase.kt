package com.sandhya.expensetracker.domain.usecase

import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
/**
 *Created by  Sandhya D on 2/6/2026.
 */
class GetExpensesUseCase @Inject constructor(
    private val repository: ExpenseRepository) // or whatever your repository name is
     {
         //  Make sure this returns Flow<List<Expense>>
     operator fun invoke(): Flow<List<Expense>> =
        repository.getAllExpenses()

}
