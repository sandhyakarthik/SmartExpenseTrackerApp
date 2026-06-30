package com.sandhya.expensetracker.domain.repository

import com.sandhya.expensetracker.data.local.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Sandhya D on 2/6/2026.
 */
interface CategoryRepository {
    fun getAllCategories(): Flow<List<CategoryEntity>>
    suspend fun insertCategory(category: CategoryEntity)
    suspend fun deleteCategory(category: CategoryEntity)
}
