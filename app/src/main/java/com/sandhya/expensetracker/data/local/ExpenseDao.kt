package com.sandhya.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 *Created by  Sandhya D on 1/15/2026.
 */
@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertExpense(expense : ExpenseEntity)

    @Delete()
     fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
     fun getAllExpenses(): Flow<List<ExpenseEntity>>

    // Combined aggregate query (Fixed table name to match 'expenses')
    @Query("SELECT TOTAL(amount) as totalSpent, COUNT(*) as totalTransactions FROM expenses")
    fun getMonthlySummary(): Flow<MonthlySummaryDto>
    // Note: You can also add a WHERE clause here later to filter by the current month!

    @Query("SELECT category, TOTAL(amount) as totalAmount FROM expenses GROUP BY category ORDER BY totalAmount DESC")
    fun getCategorySummaries(): Flow<List<CategorySummaryDto>>
}
/**
 * Data holder class for Room to map the aggregate query results into.
 */
data class MonthlySummaryDto(
    val totalSpent: Double,
    val totalTransactions: Int
)
// Data Transfer Object for Room mapping
data class CategorySummaryDto(
    val category: String,
    val totalAmount: Double
)