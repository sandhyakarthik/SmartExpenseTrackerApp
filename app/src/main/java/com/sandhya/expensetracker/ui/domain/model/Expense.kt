package com.sandhya.expensetracker.ui.domain.model

/**
 *Created by  Sandhya D on 1/15/2026.
 */
data class Expense(
    val id : Long = 0L,
    val amount : Double,
    val category : String,
    val note : String?,
    val timeStamp : Long
)