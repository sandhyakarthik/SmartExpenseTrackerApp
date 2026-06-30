package com.sandhya.expensetracker.domain.usecase

import com.sandhya.expensetracker.data.local.CategoryEntity
import com.sandhya.expensetracker.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Sandhya D on 2/6/2026.
 */
class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<CategoryEntity>> = repository.getAllCategories()
}
