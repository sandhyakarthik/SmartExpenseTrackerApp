package com.sandhya.expensetracker.data.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Created by Sandhya D on 6/25/2026.
 */
data class ExpenseWithCategory(
    @Embedded val expense: ExpenseEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)
