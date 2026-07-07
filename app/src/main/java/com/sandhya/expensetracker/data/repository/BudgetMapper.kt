package com.sandhya.expensetracker.data.repository

import com.sandhya.expensetracker.data.local.BudgetEntity
import com.sandhya.expensetracker.domain.model.Budget

/**
 * Created by Sandhya D on 2/6/2026.
 */

fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = id,
        categoryId = categoryId,
        limitAmount = limitAmount,
        month = month,
        year = year
    )
}

fun BudgetEntity.toDomain(): Budget {
    return Budget(
        id = id,
        categoryId = categoryId,
        limitAmount = limitAmount,
        month = month,
        year = year
    )
}
