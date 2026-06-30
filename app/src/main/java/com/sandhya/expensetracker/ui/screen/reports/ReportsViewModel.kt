package com.sandhya.expensetracker.ui.screen.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandhya.expensetracker.domain.model.CategorySummary
import com.sandhya.expensetracker.domain.usecase.GetMonthlyCategorySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Created by Sandhya D on 2/6/2026.
 */
@HiltViewModel
class ReportsViewModel @Inject constructor(
    getMonthlyCategorySummaryUseCase: GetMonthlyCategorySummaryUseCase
) : ViewModel() {

    val monthlyCategorySummaries: StateFlow<List<CategorySummary>> =
        getMonthlyCategorySummaryUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
