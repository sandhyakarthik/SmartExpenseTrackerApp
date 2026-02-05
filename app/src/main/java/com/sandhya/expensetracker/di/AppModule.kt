package com.sandhya.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.sandhya.expensetracker.data.local.ExpenseDao
import com.sandhya.expensetracker.data.local.ExpenseDatabase
import com.sandhya.expensetracker.data.repository.ExpenseRepositoryImpl
import com.sandhya.expensetracker.domain.repository.ExpenseRepository
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
}