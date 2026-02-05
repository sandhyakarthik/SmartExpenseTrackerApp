package com.sandhya.expensetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 *Created by  Sandhya D on 1/15/2026.
 */
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,
    val amount : Double,
    val category : String,
    val note : String?,
    val timeStamp : Long
)