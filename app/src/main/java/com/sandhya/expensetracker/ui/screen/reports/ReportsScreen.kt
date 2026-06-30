package com.sandhya.expensetracker.ui.screen.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandhya.expensetracker.domain.model.CategorySummary
import com.sandhya.expensetracker.ui.component.ExpenseTopAppBar
import java.util.Locale
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import com.sandhya.expensetracker.ui.component.ReportDateRangePickerDialog
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by Sandhya D on 2/6/2026.
 */

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateRangeText by remember { mutableStateOf("June 2026") }

    val summaries by viewModel.monthlyCategorySummaries.collectAsState()
    val totalAmount = summaries.sumOf { it.totalAmount }

    Scaffold(
        topBar = {
            ExpenseTopAppBar(title = "Monthly Summary")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            // Place this right underneath your summary cards to match the design:
            InputChip(
                selected = true,
                onClick = { showDatePicker = true },
                label = { Text(text = "Date Range: $selectedDateRangeText") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Dates"
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (summaries.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No data for this month", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(summaries) { summary ->
                        val percentage = if (totalAmount > 0) (summary.totalAmount / totalAmount * 100) else 0.0
                        CategoryReportItem(summary = summary, percentage = percentage)
                    }
                }
            }
        }
    }
    // Handle displaying the dialog overlay conditionally
    if (showDatePicker) {
        ReportDateRangePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateRangeSelected = { startTimestamp, endTimestamp ->
                if (startTimestamp != null && endTimestamp != null) {
                    val formatter = SimpleDateFormat("MMM d", Locale.getDefault()).apply {
                        timeZone = java.util.TimeZone.getTimeZone("UTC")
                    }
                    val start = formatter.format(Date(startTimestamp))
                    val end = formatter.format(Date(endTimestamp))
                    selectedDateRangeText = "$start—$end, 2026"

                    // TODO: Pass timestamps to your ViewModel to update your Room query!
                     viewModel.updateDateFilter(startTimestamp, endTimestamp)
                }
            }
        )
    }
}

@Composable
fun CategoryReportItem(summary: CategorySummary, percentage: Double) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(getCategoryColor(summary.category))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = summary.category,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = String.format(Locale.getDefault(), "%.0f%%", percentage),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (percentage / 100).toFloat())
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(getCategoryColor(summary.category))
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = String.format(Locale.getDefault(), "$%.2f", summary.totalAmount),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category) {
        "Food & Drinks" -> Color(0xFFFF9800)
        "Transportation" -> Color(0xFF2196F3)
        "Shopping" -> Color(0xFF4CAF50)
        "Entertainment" -> Color(0xFF9C27B0)
        "Bills & Utilities" -> Color(0xFF607D8B)
        "Health" -> Color(0xFFF44336)
        "Travel" -> Color(0xFF00BCD4)
        "Education" -> Color(0xFF795548)
        else -> Color(0xFF9E9E9E)
    }
}
