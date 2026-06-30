package com.sandhya.expensetracker.domain.usecase

import org.junit.Assert.*
import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by  Sandhya D on 6/10/2026.
 */
class GetCategorySummaryUseCaseTest {

    // 1. Create a fake/mocked instance of the repository dependency
    private val repository: ExpenseRepository = mockk()

    // 2. Instantiate the Use Case we want to test, passing in our mock
    private val getCategorySummaryUseCase = GetCategorySummaryUseCase(repository)

    @Test
    fun `invoke should group expenses by category and sum their amounts correctly`() = runTest {
        // ARRANGE: Set up explicit test data mimicking database records
        val dummyExpenses = listOf(
            Expense(id = 1 , amount = 12.50,category = "Food", note = "Lunch", timeStamp = 100L),
            Expense(id = 2,  amount = 7.50,category = "Food", note = "Coffee", timeStamp = 105L),
            Expense(id = 3,  amount = 50.00, category = "Shopping",note = "Shirt", timeStamp = 110L),
            Expense(id = 4,  amount = 20.00,category = "Entertainment", note = "Movies", timeStamp = 120L)
        )

        // Tell our mock repository to return this fake list inside a Flow stream
        coEvery { repository.getAllExpenses() } returns flowOf(dummyExpenses)

        // ACT: Call the invocation function we want to check
        val resultList = getCategorySummaryUseCase().first()

        // ASSERT: Check if the calculations match reality
        assertEquals(3, resultList.size) // Should reduce 4 items down to 3 unique categories

        // Verify "Food" total combined calculation (12.50 + 7.50 = 20.00)
        val foodSummary = resultList.find { it.category == "Food" }
        assertEquals(20.00, foodSummary?.totalAmount ?: 0.0, 0.01)

        // Verify "Shopping" total stays untouched at 50.00
        val shoppingSummary = resultList.find { it.category == "Shopping" }
        assertEquals(50.00, shoppingSummary?.totalAmount ?: 0.0, 0.01)
    }
}