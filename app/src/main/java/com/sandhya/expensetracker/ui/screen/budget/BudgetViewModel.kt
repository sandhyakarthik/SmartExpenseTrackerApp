package com.sandhya.expensetracker.ui.screen.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandhya.expensetracker.domain.model.Budget
import com.sandhya.expensetracker.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class BudgetSummary(
    val totalBudgeted: Double = 0.0,
    val totalSpent: Double = 0.0,
    val progress: Float = 0f,
    val percentUsed: Int = 0
)

data class BudgetItem(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val budgetedAmount: Double,
    val spentAmount: Double
)

data class BudgetUiState(
    val summary: BudgetSummary = BudgetSummary(),
    val items: List<BudgetItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getBudgetsForMonthUseCase: GetBudgetsForMonthUseCase,
    private val addBudgetUseCase: AddBudgetUseCase,
    private val updateBudgetUseCase: UpdateBudgetUseCase,
    private val getMonthlyCategorySummaryUseCase: GetMonthlyCategorySummaryUseCase
) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val currentMonth = calendar.get(Calendar.MONTH) + 1
    private val currentYear = calendar.get(Calendar.YEAR)

    val uiState: StateFlow<BudgetUiState> = combine(
        getCategoriesUseCase(),
        getBudgetsForMonthUseCase(currentMonth, currentYear),
        getMonthlyCategorySummaryUseCase()
    ) { categories, budgets, summaries ->
        if (categories.isEmpty()) {
            BudgetUiState(isLoading = true)
        } else {
            val items = categories.map { category ->
                val budget = budgets.find { it.categoryId == category.id }
                val summary = summaries.find { it.category == category.name }
                BudgetItem(
                    categoryId = category.id,
                    categoryName = category.name,
                    categoryIcon = category.iconName,
                    categoryColor = category.colorHex,
                    budgetedAmount = budget?.limitAmount ?: 0.0,
                    spentAmount = summary?.totalAmount ?: 0.0
                )
            }

            val totalBudgeted = items.sumOf { it.budgetedAmount }
            val totalSpent = items.sumOf { it.spentAmount }
            val progress = if (totalBudgeted > 0) (totalSpent / totalBudgeted).toFloat() else 0f
            
            BudgetUiState(
                summary = BudgetSummary(
                    totalBudgeted = totalBudgeted,
                    totalSpent = totalSpent,
                    progress = progress,
                    percentUsed = (progress * 100).toInt()
                ),
                items = items,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetUiState(isLoading = true)
    )

    fun onBudgetChanged(categoryId: Long, amount: Double) {
        viewModelScope.launch {
            val budgets = getBudgetsForMonthUseCase(currentMonth, currentYear).first()
            val existingBudget = budgets.find { it.categoryId == categoryId }
            
            if (existingBudget != null) {
                updateBudgetUseCase(existingBudget.copy(limitAmount = amount))
            } else {
                addBudgetUseCase(
                    Budget(
                        categoryId = categoryId,
                        limitAmount = amount,
                        month = currentMonth,
                        year = currentYear
                    )
                )
            }
        }
    }
}
