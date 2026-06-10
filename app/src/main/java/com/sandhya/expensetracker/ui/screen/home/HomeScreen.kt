package com.sandhya.expensetracker.ui.screen.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.animation.animateColorAsState // Required for background color transitions

/**
 * Created by Sandhya D on 1/9/2026.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {

    val state by viewModel.uiState.collectAsState()
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
                    val categories by viewModel.categorySummaries.collectAsState(initial = emptyList())

                    // Combined everything into one LazyColumn for a smoother scrolling experience
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        // 1. Monthly Summary Card
                        item {
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
                        }

                        // 2. Category Section Header
                        if (categories.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Spending by Category",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            // 3. Category Cards
                            items(categories, key = { category -> "cat_${category.category}" }) { categorySummary ->
                                CategorySummaryItem(categorySummary = categorySummary)
                            }
                        }

                        // 4. Recent Transactions Section Header
                        item {
                            Text(
                                text = "Recent Transactions",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        // 5. Swipe-to-Delete Transaction Items
                        items(expenses, key = { expense -> expense.id }) { expense ->

                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                        viewModel.delete(expense)
                                        true
                                    } else {
                                        false
                                    }
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false, // Drag right-to-left only
                                backgroundContent = {
                                    val backgroundColor by animateColorAsState(
                                        targetValue = when (dismissState.targetValue) {
                                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                            else -> MaterialTheme.colorScheme.surface
                                        },
                                        label = "DismissBackground"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(backgroundColor)
                                            .padding(horizontal = 24.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Expense",
                                            tint = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                },
                                content = {
                                    ExpenseItem(
                                        expense = expense,
                                        onDelete = { viewModel.delete(expense) }
                                    )
                                }
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