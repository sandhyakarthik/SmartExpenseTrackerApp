package com.sandhya.expensetracker.ui.screen.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandhya.expensetracker.domain.model.CategorySummary
import com.sandhya.expensetracker.domain.usecase.GetMonthlyCategorySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Created by Sandhya D on 2/6/2026.
 */
@HiltViewModel
class ReportsViewModel @Inject constructor(
    getMonthlyCategorySummaryUseCase: GetMonthlyCategorySummaryUseCase
) : ViewModel() {

    // 1. Tracks the active date range selection (null means fall back to the default current month)
    private val _dateFilter = MutableStateFlow<Pair<Long, Long>?>(null)

    // 2. Reactively switches the data stream whenever _dateFilter emits a new pair of timestamps
    @OptIn(ExperimentalCoroutinesApi::class)
    val monthlyCategorySummaries: StateFlow<List<CategorySummary>> =
        _dateFilter
            .flatMapLatest { range ->
                if (range != null) {
                    // Pass custom date range boundaries to your use case
                    getMonthlyCategorySummaryUseCase(startTimestamp = range.first, endTimestamp = range.second)
                } else {
                    // Default call when no picker range has been chosen yet
                    getMonthlyCategorySummaryUseCase()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    /**
     * Updates the active date filter range, triggering an automatic data refresh.
     */
    fun updateDateFilter(startTimestamp: Long, endTimestamp: Long) {
        _dateFilter.value = Pair(startTimestamp, endTimestamp)
    }
}
