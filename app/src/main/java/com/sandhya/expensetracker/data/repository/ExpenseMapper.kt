package com.sandhya.expensetracker.data.repository

import com.sandhya.expensetracker.data.local.ExpenseEntity
import com.sandhya.expensetracker.data.local.ExpenseWithCategory
import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.domain.model.ExpenseDetail

/**
 * Created by Sandhya D on 2/4/2026.
 */
class ExpenseMapper {
}

// Domain → Entity
fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amount = amount,
        category = category,
        categoryId = categoryId,
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
        categoryId = categoryId,
        note = note,
        timeStamp = timeStamp
    )
}

// Entity Relation → Domain Detail
fun ExpenseWithCategory.toDetailDomain(): ExpenseDetail {
    return ExpenseDetail(
        id = expense.id,
        amount = expense.amount,
        categoryName = category.name,
        categoryIcon = category.iconName,
        categoryColor = category.colorHex,
        note = expense.note,
        timeStamp = expense.timeStamp
    )
}
