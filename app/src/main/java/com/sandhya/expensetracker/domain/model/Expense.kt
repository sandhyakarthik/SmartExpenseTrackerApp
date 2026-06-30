package com.sandhya.expensetracker.domain.model

/**
 * Created by Sandhya D on 1/15/2026.
 */
data class Expense(
    val id: Long = 0L,
    val amount: Double,
    val category: String,
    val categoryId: Long = 1L, // Defaults to first category
    val note: String?,
    val timeStamp: Long
)
