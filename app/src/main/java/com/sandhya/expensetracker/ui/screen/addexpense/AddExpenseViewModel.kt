package com.sandhya.expensetracker.ui.screen.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandhya.expensetracker.data.local.CategoryEntity
import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.domain.usecase.AddExpenseUseCase
import com.sandhya.expensetracker.domain.usecase.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *Created by  Sandhya D on 6/6/2026.
 */
@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpense: AddExpenseUseCase,
    getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> = getCategoriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // In your AddExpenseViewModel.kt
    fun save(amount: Double, category: CategoryEntity, note: String, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            try {
                addExpense(
                    Expense(
                        amount = amount,
                        category = category.name,
                        categoryId = category.id,
                        note = note,
                        timeStamp = timestamp
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
