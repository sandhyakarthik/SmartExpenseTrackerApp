package com.sandhya.expensetracker.data.local

/**
 * Created by Sandhya D on 2/6/2026.
 *
 * A clean configuration object containing the default starter categories for the app.
 */
object DefaultCategories {
    val list = listOf(
        CategoryEntity(
            name = "Food & Drinks",
            iconName = "restaurant",
            colorHex = "#FF9800" // Orange
        ),
        CategoryEntity(
            name = "Transportation",
            iconName = "directions_bus",
            colorHex = "#2196F3" // Blue
        ),
        CategoryEntity(
            name = "Shopping",
            iconName = "shopping_bag",
            colorHex = "#4CAF50" // Green (Updated from Pink to Green as requested)
        ),
        CategoryEntity(
            name = "Entertainment",
            iconName = "movie",
            colorHex = "#9C27B0" // Purple
        ),
        CategoryEntity(
            name = "Bills & Utilities",
            iconName = "receipt",
            colorHex = "#607D8B" // Blue Grey
        ),
        CategoryEntity(
            name = "Health",
            iconName = "medical_services",
            colorHex = "#F44336" // Red
        ),
        CategoryEntity(
            name = "Travel",
            iconName = "flight",
            colorHex = "#00BCD4" // Cyan
        ),
        CategoryEntity(
            name = "Education",
            iconName = "school",
            colorHex = "#795548" // Brown
        ),
        CategoryEntity(
            name = "Others",
            iconName = "category",
            colorHex = "#9E9E9E" // Grey
        )
    )
}
