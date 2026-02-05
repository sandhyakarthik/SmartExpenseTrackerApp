package com.sandhya.expensetracker.data.repository

import com.sandhya.expensetracker.data.local.ExpenseDao
import com.sandhya.expensetracker.data.local.ExpenseEntity
//import com.sandhya.expensetracker.data.repository.ExpenseMapper
import com.sandhya.expensetracker.data.repository.toEntity
import com.sandhya.expensetracker.data.repository.toDomain
import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.domain.repository.ExpenseRepository

/**
 *Created by  Sandhya D on 2/4/2026.
 */
class ExpenseRepositoryImpl( private val dao: ExpenseDao): ExpenseRepository
{
    override suspend fun addExpense(expense: Expense) {
        dao.insertExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense.toEntity())
    }

    override suspend fun getAllExpenses(): List<Expense> {
        return dao.getAllExpenses().map { it.toDomain() }
        return emptyList();
    }
}