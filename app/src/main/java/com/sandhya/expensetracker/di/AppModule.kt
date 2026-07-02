package com.sandhya.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.sandhya.expensetracker.data.local.CategoryDao
import com.sandhya.expensetracker.data.local.DefaultCategories
import com.sandhya.expensetracker.data.local.ExpenseDao
import com.sandhya.expensetracker.data.local.ExpenseDatabase
import com.sandhya.expensetracker.data.repository.CategoryRepositoryImpl
import com.sandhya.expensetracker.data.repository.ExpenseRepositoryImpl
import com.sandhya.expensetracker.domain.repository.CategoryRepository
import com.sandhya.expensetracker.domain.repository.ExpenseRepository
import com.sandhya.expensetracker.domain.usecase.AddExpenseUseCase
import com.sandhya.expensetracker.domain.usecase.DeleteExpenseUseCase
import com.sandhya.expensetracker.domain.usecase.GetCategoriesUseCase
import com.sandhya.expensetracker.domain.usecase.GetCategorySummaryUseCase
import com.sandhya.expensetracker.domain.usecase.GetExpensesUseCase
import com.sandhya.expensetracker.domain.usecase.GetExpensesWithCategoryUseCase
import com.sandhya.expensetracker.domain.usecase.GetMonthlyCategorySummaryUseCase
import com.sandhya.expensetracker.domain.usecase.GetMonthlySummaryUseCase
import com.sandhya.expensetracker.domain.usecase.GetTotalSpentInRangeUseCase
import com.sandhya.expensetracker.domain.usecase.GetTotalSummaryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 *Created by  Sandhya D on 2/4/2026.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ExpenseDatabase =
        Room.databaseBuilder(
            context,
            ExpenseDatabase::class.java,
            "expense_tracker_db"
        ).fallbackToDestructiveMigration()
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    DefaultCategories.list.forEach { category ->
                        db.execSQL(
                            "INSERT INTO categories (name, iconName, colorHex) VALUES ('${category.name}', '${category.iconName}', '${category.colorHex}')"
                        )
                    }
                }
            })
            .build()

    @Provides
    fun provideExpenseDao(db: ExpenseDatabase): ExpenseDao =
        db.expenseDao()

    @Provides
    fun provideCategoryDao(db: ExpenseDatabase): CategoryDao =
        db.categoryDao()

    @Provides
    @Singleton
    fun provideExpenseRepository(
        dao: ExpenseDao
    ): ExpenseRepository =
        ExpenseRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideCategoryRepository(
        dao: CategoryDao
    ): CategoryRepository =
        CategoryRepositoryImpl(dao)

    @Provides
    fun provideAddExpenseUseCase(repo: ExpenseRepository) =
        AddExpenseUseCase(repo)

    @Provides
    fun provideGetExpensesUseCase(repo: ExpenseRepository) =
        GetExpensesUseCase(repo)

    @Provides
    fun provideGetExpensesWithCategoryUseCase(repo: ExpenseRepository) =
        GetExpensesWithCategoryUseCase(repo)

    @Provides
    fun provideGetCategoriesUseCase(repo: CategoryRepository) =
        GetCategoriesUseCase(repo)

    @Provides
    fun provideDeleteExpenseUseCase(repo: ExpenseRepository) =
        DeleteExpenseUseCase(repo)

    @Provides
    fun provideGetMonthlySummaryUseCase(
        repository: ExpenseRepository
    ): GetMonthlySummaryUseCase {
        return GetMonthlySummaryUseCase(repository)
    }

    @Provides
    fun provideGetTotalSummaryUseCase(
        repository: ExpenseRepository
    ): GetTotalSummaryUseCase {
        return GetTotalSummaryUseCase(repository)
    }

    @Provides
    fun provideGetTotalSpentInRangeUseCase(
        repository: ExpenseRepository
    ): GetTotalSpentInRangeUseCase {
        return GetTotalSpentInRangeUseCase(repository)
    }

    @Provides
    fun provideGetCategorySummaryUseCase(
        repository: ExpenseRepository
    ): GetCategorySummaryUseCase {
        return GetCategorySummaryUseCase(repository)
    }

    @Provides
    fun provideGetMonthlyCategorySummaryUseCase(
        repository: ExpenseRepository
    ): GetMonthlyCategorySummaryUseCase {
        return GetMonthlyCategorySummaryUseCase(repository)
    }
}