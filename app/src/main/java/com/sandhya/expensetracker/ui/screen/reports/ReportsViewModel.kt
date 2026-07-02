package com.sandhya.expensetracker.ui.screen.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandhya.expensetracker.domain.model.CategorySummary
import com.sandhya.expensetracker.domain.usecase.GetMonthlyCategorySummaryUseCase
import com.sandhya.expensetracker.domain.usecase.GetTotalSpentInRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Created by Sandhya D on 2/6/2026.
 */

data class SpendingTrend(
    val percentage: Double,
    val isHigher: Boolean,
    val previousMonthName: String,
)

data class QuickStats(
    val totalSpending: Double = 0.0,
    val highestCategory: String = "N/A",
    val transactionCount: Int = 0,
    val averagePerDay: Double = 0.0,
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getMonthlyCategorySummaryUseCase: GetMonthlyCategorySummaryUseCase,
    private val getTotalSpentInRangeUseCase: GetTotalSpentInRangeUseCase,
) : ViewModel() {

    private val _dateFilter = MutableStateFlow<Pair<Long, Long>?>(null)

    private val _dateRangeText = MutableStateFlow(
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    )
    val dateRangeText: StateFlow<String> = _dateRangeText.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val monthlyCategorySummaries: StateFlow<List<CategorySummary>> =
        _dateFilter
            .flatMapLatest { range ->
                if (range != null) {
                    getMonthlyCategorySummaryUseCase(startTimestamp = range.first, endTimestamp = range.second)
                } else {
                    getMonthlyCategorySummaryUseCase()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val quickStats: StateFlow<QuickStats> = combine(
        monthlyCategorySummaries,
        _dateFilter
    ) { summaries, range ->
        val total = summaries.sumOf { it.totalAmount }
        val count = summaries.sumOf { it.transactionCount }
        val highest = summaries.maxByOrNull { it.totalAmount }?.category ?: "N/A"
        
        val days = if (range != null) {
            val diff = range.second - range.first
            (diff / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
        } else {
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toLong()
        }
        
        QuickStats(
            totalSpending = total,
            highestCategory = highest,
            transactionCount = count,
            averagePerDay = total / days
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuickStats()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val spendingTrend: StateFlow<SpendingTrend?> = _dateFilter
        .flatMapLatest { range ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            
            val (currentStart, currentEnd, prevStart, prevEnd) = if (range != null) {
                val duration = range.second - range.first
                listOf(range.first, range.second, range.first - duration, range.first)
            } else {
                // Default: This month vs last month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val cStart = calendar.timeInMillis
                
                calendar.add(Calendar.MONTH, 1)
                val cEnd = calendar.timeInMillis
                
                calendar.add(Calendar.MONTH, -2) // Move to start of prev month
                val pStart = calendar.timeInMillis
                
                calendar.add(Calendar.MONTH, 1) // End of prev month
                val pEnd = calendar.timeInMillis
                
                listOf(cStart, cEnd, pStart, pEnd)
            }

            calendar.timeInMillis = prevStart
            val prevMonthName = SimpleDateFormat("MMMM", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(calendar.time)

            combine(
                getTotalSpentInRangeUseCase(currentStart, currentEnd),
                getTotalSpentInRangeUseCase(prevStart, prevEnd)
            ) { current, prev ->
                if (prev > 0) {
                    val diff = current - prev
                    val percent = (diff / prev) * 100
                     SpendingTrend(abs(percent), diff > 0, prevMonthName)
                } else {
                    null
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updateDateFilter(startTimestamp: Long, endTimestamp: Long, formattedText: String) {
        _dateFilter.value = Pair(startTimestamp, endTimestamp)
        _dateRangeText.value = formattedText
    }
}
