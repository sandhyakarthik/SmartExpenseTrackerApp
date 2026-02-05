package com.sandhya.expensetracker.data.repository

import com.sandhya.expensetracker.data.local.ExpenseEntity
import com.sandhya.expensetracker.domain.model.Expense

/**
 *Created by  Sandhya D on 2/4/2026.
 */
class ExpenseMapper {
}
// Domain → Entity
fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amount = amount,
        category = category,
        note = note,
        timeStamp = timeStamp
    )
}
// Entity → Domain
fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        amount = amount,
        category = category,
        note = note,
        timeStamp = timeStamp
    )
}