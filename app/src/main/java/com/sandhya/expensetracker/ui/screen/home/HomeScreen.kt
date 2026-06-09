package com.sandhya.expensetracker.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandhya.expensetracker.Screen
import com.sandhya.expensetracker.domain.model.MonthlySummary
import com.sandhya.expensetracker.ui.component.CategorySummaryItem
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

    // Collect real-time aggregate totals from the ViewModel (Defaults to 0.0, 0)
    val summary by viewModel.monthlySummary.collectAsState(initial = MonthlySummary(0.0, 0))

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

                    // 1. Collect the reactive category list flow from your ViewModel
                    val categories by viewModel.categorySummaries.collectAsState(initial = emptyList())

                    // Added a parent Column container here so the Card stays fixed at the top
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        // ========================================================
                        //  MONTHLY SUMMARY CARD LAYER (DYNAMIC)
                        // ========================================================
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "This Month",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "$${String.format("%.2f", summary.totalSpent)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${summary.totalTransactions} Transactions",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Section Title
                        Text(
                            text = "Spending by Category",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                            color = MaterialTheme.colorScheme.outline
                        )

                        // 2. The scrolling list displaying a Card for each category
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(categories, key = { category -> category.category }) { categorySummary ->
                                CategorySummaryItem(categorySummary = categorySummary)
                            }
                        }

                        // The transaction list takes up all remaining available room beneath the card
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
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
