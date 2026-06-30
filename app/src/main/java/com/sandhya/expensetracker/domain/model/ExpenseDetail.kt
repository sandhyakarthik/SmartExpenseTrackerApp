package com.sandhya.expensetracker.domain.model

/**
 * Created by Sandhya D on 2/6/2026.
 */
data class ExpenseDetail(
    val id: Long,
    val amount: Double,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val note: String?,
    val timeStamp: Long
)
