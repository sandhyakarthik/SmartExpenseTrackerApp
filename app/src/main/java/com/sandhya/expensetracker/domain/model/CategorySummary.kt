package com.sandhya.expensetracker.domain.model

/**
 * Created by Sandhya D on 6/9/2026.
 */
data class CategorySummary(
    val category: String,
    val totalAmount: Double,
    val transactionCount: Int
)
