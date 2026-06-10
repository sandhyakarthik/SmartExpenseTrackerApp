package com.sandhya.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.sandhya.expensetracker.data.local.ExpenseDao
import com.sandhya.expensetracker.data.local.ExpenseDatabase
import com.sandhya.expensetracker.data.repository.ExpenseRepositoryImpl
import com.sandhya.expensetracker.domain.repository.ExpenseRepository
import com.sandhya.expensetracker.domain.usecase.AddExpenseUseCase
import com.sandhya.expensetracker.domain.usecase.DeleteExpenseUseCase
import com.sandhya.expensetracker.domain.usecase.GetCategorySummaryUseCase
import com.sandhya.expensetracker.domain.usecase.GetExpensesUseCase
import com.sandhya.expensetracker.domain.usecase.GetMonthlySummaryUseCase
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
            "expense_db"
        ).build()

    @Provides
    fun provideExpenseDao(db: ExpenseDatabase): ExpenseDao =
        db.expenseDao()

    @Provides
    @Singleton
    fun provideExpenseRepository(
        dao: ExpenseDao
    ): ExpenseRepository =
        ExpenseRepositoryImpl(dao)

    @Provides
    fun provideAddExpenseUseCase(repo: ExpenseRepository) =
        AddExpenseUseCase(repo)

    @Provides
    fun provideGetExpensesUseCase(repo: ExpenseRepository) =
        GetExpensesUseCase(repo)

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
    fun provideGetCategorySummaryUseCase(
        repository: ExpenseRepository
    ): GetCategorySummaryUseCase {
        return GetCategorySummaryUseCase(repository)
    }
}