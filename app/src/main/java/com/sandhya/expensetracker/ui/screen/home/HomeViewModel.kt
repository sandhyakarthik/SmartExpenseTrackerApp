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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class HomeDashboardState(
    val totalSpent: Double = 0.0,
    val monthlySpent: Double = 0.0,
    val categorySummaries: List<CategorySummary> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getExpensesWithCategory: GetExpensesWithCategoryUseCase,
    private val deleteExpense: DeleteExpenseUseCase,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val getTotalSummaryUseCase: GetTotalSummaryUseCase,
    private val getCategorySummaryUseCase: GetCategorySummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExpenseUiState>(ExpenseUiState.Loading)
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    // Consolidate dashboard data into a single state flow
    val dashboardState: StateFlow<HomeDashboardState> = combine(
        getTotalSummaryUseCase(),
        getMonthlySummaryUseCase(),
        getCategorySummaryUseCase()
    ) { total, monthly, categories ->
        HomeDashboardState(
            totalSpent = total.totalSpent,
            monthlySpent = monthly.totalSpent,
            categorySummaries = categories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeDashboardState()
    )

    init {
        observeExpenses()
    }

    private fun observeExpenses() {
        viewModelScope.launch {
            try {
                getExpensesWithCategory().collect { expenses ->
                    _uiState.value = if (expenses.isEmpty()) {
                        ExpenseUiState.Empty
                    } else {
                        ExpenseUiState.Success(expenses)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ExpenseUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun delete(expense: ExpenseDetail) {
        viewModelScope.launch {
            try {
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
                // Log error
            }
        }
    }
}
