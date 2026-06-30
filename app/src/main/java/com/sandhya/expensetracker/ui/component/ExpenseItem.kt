package com.sandhya.expensetracker.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sandhya.expensetracker.domain.model.ExpenseDetail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by Sandhya D on 2/6/2026.
 */
@Composable
fun ExpenseItem(
    expense: ExpenseDetail,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Category Icon Circle with Emoji
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(expense.categoryColor)).copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getCategoryEmoji(expense.categoryName),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Info Block: Category Name and Note,date
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (expense.note.isNullOrBlank()) "No description" else expense.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(expense.timeStamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 3. Amount Block
            Column(horizontalAlignment = Alignment.End) {
               /* Text(
                    text = formatDate(expense.timeStamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )*/
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(Locale.getDefault(), "$%.2f", expense.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.error
                )
            }

           /* // 4. Delete Action (Optional, usually handled by swipe but kept for manual triggers)
            IconButton(
                onClick = onDelete,
                modifier = Modifier.padding(start = 8.dp).size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }*/
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).apply {
        timeZone = java.util.TimeZone.getTimeZone("UTC")
    }
    return formatter.format(date)
}

private fun getCategoryEmoji(category: String): String {
    return when (category) {
        "Food & Drinks" -> "🍔"
        "Transportation" -> "🚗"
        "Shopping" -> "🛍️"
        "Entertainment" -> "🎬"
        "Bills & Utilities" -> "💡"
        "Health" -> "🏥"
        "Travel" -> "✈️"
        "Education" -> "📚"
        else -> "💰"
    }
}
