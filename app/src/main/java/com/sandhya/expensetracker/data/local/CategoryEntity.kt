package com.sandhya.expensetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Sandhya D on 2/6/2026.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val iconName: String, // String representation of the icon,Store Emojis like "🍔" or "🚗"
    val colorHex: String  // Hex color code for the category,For future charts and UI color coding
)
