package com.sandhya.expensetracker.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sandhya.expensetracker.data.local.*
import com.sandhya.expensetracker.data.repository.*
import com.sandhya.expensetracker.domain.repository.*
import com.sandhya.expensetracker.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ExpenseDatabase {
        return Room.databaseBuilder(
            context,
            ExpenseDatabase::class.java,
            "smart_expense_db"
        ).fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                        populateCategories(db)
                    }
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                        populateCategories(db)
                    }
                }
            })
            .build()
    }

    private fun populateCategories(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            DefaultCategories.list.forEach { category ->
                db.execSQL(
                    "INSERT OR IGNORE INTO categories (name, iconName, colorHex) " +
                    "VALUES ('${category.name}', '${category.iconName}', '${category.colorHex}')"
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    @Provides
    fun provideExpenseDao(db: ExpenseDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideCategoryDao(db: ExpenseDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideBudgetDao(db: ExpenseDatabase): BudgetDao = db.budgetDao()

    @Provides
    @Singleton
    fun provideExpenseRepository(dao: ExpenseDao): ExpenseRepository = ExpenseRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideCategoryRepository(dao: CategoryDao): CategoryRepository = CategoryRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideBudgetRepository(dao: BudgetDao): BudgetRepository = BudgetRepositoryImpl(dao)

    @Provides
    fun provideAddExpenseUseCase(repo: ExpenseRepository) = AddExpenseUseCase(repo)

    @Provides
    fun provideGetExpensesUseCase(repo: ExpenseRepository) = GetExpensesUseCase(repo)

    @Provides
    fun provideGetExpensesWithCategoryUseCase(repo: ExpenseRepository) = GetExpensesWithCategoryUseCase(repo)

    @Provides
    fun provideGetCategoriesUseCase(repo: CategoryRepository) = GetCategoriesUseCase(repo)

    @Provides
    fun provideDeleteExpenseUseCase(repo: ExpenseRepository) = DeleteExpenseUseCase(repo)

    @Provides
    fun provideGetMonthlySummaryUseCase(repo: ExpenseRepository) = GetMonthlySummaryUseCase(repo)

    @Provides
    fun provideGetTotalSummaryUseCase(repo: ExpenseRepository) = GetTotalSummaryUseCase(repo)

    @Provides
    fun provideGetTotalSpentInRangeUseCase(repo: ExpenseRepository) = GetTotalSpentInRangeUseCase(repo)

    @Provides
    fun provideGetCategorySummaryUseCase(repo: ExpenseRepository) = GetCategorySummaryUseCase(repo)

    @Provides
    fun provideGetMonthlyCategorySummaryUseCase(repo: ExpenseRepository) = GetMonthlyCategorySummaryUseCase(repo)

    @Provides
    fun provideAddBudgetUseCase(repo: BudgetRepository) = AddBudgetUseCase(repo)

    @Provides
    fun provideGetBudgetsForMonthUseCase(repo: BudgetRepository) = GetBudgetsForMonthUseCase(repo)

    @Provides
    fun provideUpdateBudgetUseCase(repo: BudgetRepository) = UpdateBudgetUseCase(repo)

    @Provides
    fun provideDeleteBudgetUseCase(repo: BudgetRepository) = DeleteBudgetUseCase(repo)
}
