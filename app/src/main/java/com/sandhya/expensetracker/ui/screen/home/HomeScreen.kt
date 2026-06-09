package com.sandhya.expensetracker.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandhya.expensetracker.Screen
import com.sandhya.expensetracker.ui.component.ExpenseItem
import com.sandhya.expensetracker.ui.state.ExpenseUiState

/**
 * Created by Sandhya D on 1/9/2026.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {

    // Safely collect the reactive UI state pipeline from our ViewModel
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddExpense.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->

        // Box container ensures loading and empty indicators stay perfectly centered
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is ExpenseUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is ExpenseUiState.Empty -> {
                    Text(
                        text = "No expenses yet",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                is ExpenseUiState.Success -> {
                    val expenses = (state as ExpenseUiState.Success).expenses

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Added stable key mapping to fix list rendering bugs
                        items(expenses, key = { expense -> expense.id }) { expense ->
                            ExpenseItem(
                                expense = expense,
                                onDelete = { viewModel.delete(expense) }
                            )
                        }
                    }
                }

                is ExpenseUiState.Error -> {
                    val errorMessage = (state as ExpenseUiState.Error).message ?: "Something went wrong"
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}