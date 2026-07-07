package com.sandhya.expensetracker.ui.screen.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandhya.expensetracker.R
import com.sandhya.expensetracker.ui.component.ExpenseTopAppBar

/**
 * Created by Sandhya D on 2/6/2026.
 */

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    val selectedDateRangeText by viewModel.dateRangeText.collectAsState()

    val summaries by viewModel.monthlyCategorySummaries.collectAsState()
    val totalAmount = summaries.sumOf { it.totalAmount }
    val trend by viewModel.spendingTrend.collectAsState()

    Scaffold(
        topBar = {
            ExpenseTopAppBar(title = stringResource(R.string.nav_reports))
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // 1.Date Picker (FilterChip)
                    FilterChip(
                        selected = true,
                        onClick = { showDatePicker = true },
                        label = {
                            Text(
                                text = "$selectedDateRangeText ▼",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        modifier = Modifier.padding(vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp)
                    )
                }

                item {
                    //2. Top Summary Card
                    ReportSummaryCard(
                        totalAmount = totalAmount,
                        selectedRange = selectedDateRangeText,
                        trend = trend,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                if (summaries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "📊",
                                    style = MaterialTheme.typography.displayLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.msg_no_expenses),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.msg_add_first_expense),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.title_spending_dist),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            //3.PIE CHART
                            ExpensePieChart(
                                summaries = summaries,
                                totalAmount = totalAmount
                            )
                        }
                    }
                    item {
                        Text(
                            text = stringResource(R.string.title_category_details),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(summaries) { summary ->
                        val percentage = if (totalAmount > 0) (summary.totalAmount / totalAmount * 100) else 0.0
                        CategoryReportItem(summary = summary, percentage = percentage)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }

    if (showDatePicker) {
        ReportDateRangePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateRangeSelected = { startTimestamp, endTimestamp ->
                if (startTimestamp != null && endTimestamp != null) {
                    viewModel.updateDateFilter(startTimestamp, endTimestamp)
                }
            }
        )
    }
}
