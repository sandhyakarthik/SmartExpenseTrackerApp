package com.sandhya.expensetracker.ui.component

import androidx.compose.ui.graphics.Color

fun getCategoryColor(category: String): Color {
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

fun getCategoryEmoji(category: String): String {
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
