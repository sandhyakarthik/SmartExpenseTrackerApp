package com.sandhya.expensetracker.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandhya.expensetracker.domain.model.CategorySummary
import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.domain.model.ExpenseDetail
import com.sandhya.expensetracker.domain.model.MonthlySummary
import com.sandhya.expensetracker.domain.usecase.DeleteExpenseUseCase
import com.sandhya.expensetracker.domain.usecase.GetCategorySummaryUseCase
import com.sandhya.expensetracker.domain.usecase.GetExpensesUseCase
import com.sandhya.expensetracker.domain.usecase.GetExpensesWithCategoryUseCase
import com.sandhya.expensetracker.domain.usecase.GetMonthlySummaryUseCase
import com.sandhya.expensetracker.domain.usecase.GetTotalSummaryUseCase
import com.sandhya.expensetracker.ui.state.ExpenseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext


/**
 *Created by  Sandhya D on 2/6/2026.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getExpenses: GetExpensesUseCase,
    private val getExpensesWithCategory: GetExpensesWithCategoryUseCase,
    private val deleteExpense: DeleteExpenseUseCase,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val getTotalSummaryUseCase: GetTotalSummaryUseCase,
    private val getCategorySummaryUseCase: GetCategorySummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExpenseUiState>(ExpenseUiState.Loading)
    val uiState: StateFlow<ExpenseUiState> = _uiState

    val monthlySummary: Flow<MonthlySummary> = getMonthlySummaryUseCase()
    val totalSummary: Flow<MonthlySummary> = getTotalSummaryUseCase()
    val categorySummaries: Flow<List<CategorySummary>> = getCategorySummaryUseCase()

    init {
        observeExpenses()
    }

    private fun observeExpenses() {
        // 1. You must open a coroutine scope first
        viewModelScope.launch {
            try {
                // 2. Now you can safely call and collect the Flow stream
                getExpensesWithCategory().collect { expenses ->
                    _uiState.value = if (expenses.isEmpty()) {
                        ExpenseUiState.Empty
                    } else {
                        ExpenseUiState.Success(expenses)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ExpenseUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun delete(expense: ExpenseDetail) {
        viewModelScope.launch {
            try {
                // Offload the database delete operation to the background thread
                withContext(Dispatchers.IO) {
                    val domainExpense = Expense(
                        id = expense.id,
                        amount = expense.amount,
                        category = expense.categoryName,
                        note = expense.note,
                        timeStamp = expense.timeStamp
                    )
                    deleteExpense(domainExpense)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
