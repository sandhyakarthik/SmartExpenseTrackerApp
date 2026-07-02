package com.sandhya.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Created by Sandhya D on 1/15/2026.
 */
@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY timeStamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Transaction
    @Query("SELECT * FROM expenses ORDER BY timeStamp DESC")
    fun getAllExpensesWithCategory(): Flow<List<ExpenseWithCategory>>

    // Combined aggregate query (Fixed table name to match 'expenses')
    @Query("SELECT TOTAL(amount) as totalSpent, COUNT(*) as totalTransactions FROM expenses")
    fun getTotalSummary(): Flow<MonthlySummaryDto>

    @Query("SELECT TOTAL(amount) as totalSpent, COUNT(*) as totalTransactions FROM expenses WHERE strftime('%m', datetime(timeStamp/1000, 'unixepoch')) = strftime('%m', 'now') AND strftime('%Y', datetime(timeStamp/1000, 'unixepoch')) = strftime('%Y', 'now')")
    fun getMonthlySummary(): Flow<MonthlySummaryDto>

    @Query("SELECT category, TOTAL(amount) as totalAmount, COUNT(*) as transactionCount FROM expenses GROUP BY category ORDER BY totalAmount DESC")
    fun getCategorySummaries(): Flow<List<CategorySummaryDto>>

    @Query("SELECT category, TOTAL(amount) as totalAmount, COUNT(*) as transactionCount FROM expenses WHERE strftime('%m', datetime(timeStamp/1000, 'unixepoch')) = strftime('%m', 'now') AND strftime('%Y', datetime(timeStamp/1000, 'unixepoch')) = strftime('%Y', 'now') GROUP BY category ORDER BY totalAmount DESC")
    fun getMonthlyCategorySummaries(): Flow<List<CategorySummaryDto>>

    // ─── ADDED FOR THE DATE RANGE PICKER ───
    /**
     * Fetches category summary aggregates filtered dynamically between two millisecond timestamps.
     */
    @Query("""
        SELECT category, TOTAL(amount) as totalAmount, COUNT(*) as transactionCount
        FROM expenses 
        WHERE timeStamp >= :startTimestamp AND timeStamp <= :endTimestamp 
        GROUP BY category 
        ORDER BY totalAmount DESC
    """)
    fun getCategorySummariesByDate(startTimestamp: Long, endTimestamp: Long): Flow<List<CategorySummaryDto>>

    @Query("SELECT TOTAL(amount) FROM expenses WHERE timeStamp >= :start AND timeStamp <= :end")
    fun getTotalSpentInRange(start: Long, end: Long): Flow<Double?>
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
    val totalAmount: Double,
    val transactionCount: Int
)