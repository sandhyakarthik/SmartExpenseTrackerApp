package com.sandhya.expensetracker.ui.screen.reports

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sandhya.expensetracker.R
import com.sandhya.expensetracker.domain.model.CategorySummary
import com.sandhya.expensetracker.ui.component.getCategoryColor
import com.sandhya.expensetracker.ui.component.getCategoryEmoji
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

@Composable
fun ExpensePieChart(
    summaries: List<CategorySummary>,
    totalAmount: Double,
    modifier: Modifier = Modifier
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(summaries) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(220.dp)
                .padding(24.dp)
        ) {
            val strokeWidth = 28.dp.toPx()
            
            // Background Circle (Donut appearance)
            drawArc(
                color = Color.LightGray.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            if (totalAmount > 0) {
                var startAngle = -90f
                summaries.forEach { summary ->
                    val sweepAngle = (summary.totalAmount / totalAmount * 360f).toFloat()
                    val percentage = (summary.totalAmount / totalAmount * 100).toInt()
                    val gap = 3f 
                    
                    if (sweepAngle > gap) {
                        drawArc(
                            color = getCategoryColor(summary.category),
                            startAngle = startAngle + gap / 2f,
                            sweepAngle = (sweepAngle - gap) * animationProgress.value,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Draw Percentage Label if slice is large enough
                        if (sweepAngle > 25f && animationProgress.value > 0.9f) {
                            val middleAngle = startAngle + (sweepAngle / 2)
                            val angleInRadians = (middleAngle * PI / 180f).toFloat()
                            
                            // Position the text in the middle of the stroke
                            val textRadius = (size.width / 2)
                            val x = (size.width / 2) + textRadius * cos(angleInRadians)
                            val y = (size.height / 2) + textRadius * sin(angleInRadians)

                            drawContext.canvas.nativeCanvas.apply {
                                drawText(
                                    "$percentage%",
                                    x,
                                    y + 12f, // vertical centering adjustment
                                    Paint().apply {
                                        color = android.graphics.Color.WHITE
                                        textSize = 26f
                                        textAlign = Paint.Align.CENTER
                                        isFakeBoldText = true
                                        typeface = Typeface.DEFAULT_BOLD
                                        // Add a subtle shadow for better legibility on all colors
                                        setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
                                    }
                                )
                            }
                        }
                    }
                    startAngle += sweepAngle
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.title_total_spent),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            val locale = LocalConfiguration.current.locales[0]
            val amountText = remember(totalAmount, locale) { String.format(locale, "$%.2f", totalAmount) }
            Text(
                text = amountText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDateRangePickerDialog(
    onDismiss: () -> Unit,
    onDateRangeSelected: (startDateTimestamp: Long?, endDateTimestamp: Long?) -> Unit
) {
    // 1. Initialize the modern Material 3 Picker State
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.btn_apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    ) {
        // 2. Embed the core M3 DateRangePicker component
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = stringResource(R.string.msg_select_date_range),
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            headline = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                    val startText = dateRangePickerState.selectedStartDateMillis?.let { formatter.format(Date(it)) } ?: "Start Date"
                    val endText = dateRangePickerState.selectedEndDateMillis?.let { formatter.format(Date(it)) } ?: "End Date"

                    Text(
                        text = "$startText — $endText",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            showModeToggle = false, // Locks to a clean, un-cluttered calendar grid
            modifier = Modifier.weight(1f)
        )
    }
}
