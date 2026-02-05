package com.sandhya.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 *Created by  Sandhya D on 1/15/2026.
 */
@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense : ExpenseEntity)
    @Delete()
    suspend fun deleteExpense(expense: ExpenseEntity)
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    suspend fun getAllExpenses():List<ExpenseEntity>
}