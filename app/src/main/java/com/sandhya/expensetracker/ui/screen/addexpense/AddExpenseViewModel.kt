package com.sandhya.expensetracker.ui.screen.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.domain.usecase.AddExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 *Created by  Sandhya D on 6/6/2026.
 */
@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpense: AddExpenseUseCase
) : ViewModel() {

    // In your AddExpenseViewModel.kt
    fun save(amount: Double, category: String, note: String) {
        viewModelScope.launch {
            try {
                // Force the database execution context over to a background IO thread
                withContext(Dispatchers.IO) {
                    //addExpenseUseCase(amount, category, note)
                    addExpense(Expense(amount = amount, category = category, note = note, timeStamp = System.currentTimeMillis()))

                }
            } catch (e: Exception) {
                // Handle any unexpected errors gracefully
                e.printStackTrace()
            }
        }
    }
}