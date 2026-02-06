package com.sandhya.expensetracker.domain.usecase

import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.domain.repository.ExpenseRepository

/**
 *Created by  Sandhya D on 2/6/2026.
 */
class GetExpensesUseCase(private val repository: ExpenseRepository) {
    suspend operator fun invoke(): List<Expense> =
        repository.getAllExpenses()

}