package com.sandhya.expensetracker.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sandhya.expensetracker.Screen
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandhya.expensetracker.domain.model.Expense
import com.sandhya.expensetracker.ui.component.ExpenseItem
import com.sandhya.expensetracker.ui.state.ExpenseUiState

/**
 *Created by  Sandhya D on 1/9/2026.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddExpense.route) }
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->

        when (state) {
            is ExpenseUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(32.dp))
            }

            is ExpenseUiState.Empty -> {
                Text("No expenses yet", modifier = Modifier.padding(32.dp))
            }

            is ExpenseUiState.Success -> {
                val expenses = (state as ExpenseUiState.Success).expenses

                LazyColumn(
                    modifier = Modifier.padding(padding)
                ) {
                    items(expenses) { expense ->
                        ExpenseItem(
                            expense = expense,
                            onDelete = { viewModel.delete(expense) }
                        )
                    }
                }

            }

            is ExpenseUiState.Error -> {
                Text("Something went wrong")
            }
        }
    }
}
   /* Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddExpense.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "My Expenses",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No expenses yet",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }*/
