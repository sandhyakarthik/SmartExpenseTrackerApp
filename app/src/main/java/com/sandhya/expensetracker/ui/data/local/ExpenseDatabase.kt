package com.sandhya.expensetracker.ui.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 *Created by  Sandhya D on 2/4/2026.
 */
@Database(
    entities= [ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ExpenseDatabase :RoomDatabase() {

    abstract fun expenseDao():ExpenseDao
}