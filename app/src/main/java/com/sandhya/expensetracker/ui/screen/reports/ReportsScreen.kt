package com.sandhya.expensetracker.ui.screen.reports

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandhya.expensetracker.R
import com.sandhya.expensetracker.domain.model.CategorySummary
import com.sandhya.expensetracker.ui.component.*
import java.text.SimpleDateFormat
import java.util.*

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

@Composable
fun ReportSummaryCard(
    totalAmount: Double,
    selectedRange: String,
    trend: SpendingTrend?,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales[0]
    val amountText = remember(totalAmount, locale) { String.format(locale, "$%.2f", totalAmount) }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.title_total_spending),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Text(
                text = amountText,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            if (trend != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val trendIcon = if (trend.isHigher) "▲" else "▼"
                    val trendText = if (trend.isHigher) "higher" else "lower"
                    val trendInfo = remember(trend, locale) {
                        String.format(locale, "%s %.0f%% %s than %s", 
                            trendIcon, trend.percentage, trendText, trend.previousMonthName)
                    }
                    Text(
                        text = trendInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryReportItem(summary: CategorySummary, percentage: Double) {
    val locale = LocalConfiguration.current.locales[0]
    val amountText = remember(summary.totalAmount, locale) { String.format(locale, "$%.2f", summary.totalAmount) }
    val percentText = remember(percentage, locale) { String.format(locale, "%.0f%%", percentage) }

    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) (percentage / 100).toFloat().coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "ProgressBarAnimation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = getCategoryEmoji(summary.category),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = summary.category,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Text(
                    text = amountText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = percentText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "${summary.transactionCount} Transactions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = animatedProgress)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(getCategoryColor(summary.category))
                )
            }
        }
    }
}
