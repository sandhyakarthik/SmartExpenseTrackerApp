package com.sandhya.expensetracker.domain.model

/**
 * Created by Sandhya D on 2/6/2026.
 */
data class Budget(
    val id: Long = 0L,
    val categoryId: Long,
    val limitAmount: Double,
    val month: Int,
    val year: Int
)
