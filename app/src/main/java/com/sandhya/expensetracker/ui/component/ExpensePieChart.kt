package com.sandhya.expensetracker.ui.component

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sandhya.expensetracker.R
import com.sandhya.expensetracker.domain.model.CategorySummary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

