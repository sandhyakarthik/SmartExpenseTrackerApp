package com.sandhya.expensetracker.ui.screen.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sandhya.expensetracker.Screen
import com.sandhya.expensetracker.domain.model.MonthlySummary
import com.sandhya.expensetracker.ui.component.CategorySummaryItem
import com.sandhya.expensetracker.ui.component.ExpenseItem
import com.sandhya.expensetracker.ui.component.ExpenseTopAppBar
import com.sandhya.expensetracker.ui.component.SummaryCards
import com.sandhya.expensetracker.ui.state.ExpenseUiState

/**
 * Created by Sandhya D on 1/9/2026.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val monthlySummary by viewModel.monthlySummary.collectAsState(initial = MonthlySummary(0.0, 0))
    val totalSummary by viewModel.totalSummary.collectAsState(initial = MonthlySummary(0.0, 0))

    Scaffold(
        topBar = {
            ExpenseTopAppBar(title = "SmartExpenseTracker")
        },
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
                .fillMaxSize()
                .padding(padding)
        ) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Dashboard Summary Cards
            SummaryCards(
                totalSpent = totalSummary.totalSpent,
                monthlySpent = monthlySummary.totalSpent
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    is ExpenseUiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is ExpenseUiState.Empty -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "💰",
                                style = MaterialTheme.typography.displayMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "No expenses yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Start tracking your spending\nto build better financial habits.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    is ExpenseUiState.Success -> {
                        val expenses = (state as ExpenseUiState.Success).expenses
                        val categories by viewModel.categorySummaries.collectAsState(initial = emptyList())

                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // 1. Category Section Header
                           /* if (categories.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Spending by Category",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 12.dp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                // 2. Category Cards
                                items(categories, key = { category -> "cat_${category.category}" }) { categorySummary ->
                                    CategorySummaryItem(categorySummary = categorySummary)
                                }
                            }*/

                            // 3. Recent Transactions Section Header
                            item {
                                Text(
                                    text = "Recent Expenses",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // 4. Swipe-to-Delete Transaction Items
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
                                    enableDismissFromStartToEnd = false,
                                    modifier = Modifier.animateItem(
                                        fadeInSpec = tween(durationMillis = 300),
                                        fadeOutSpec = tween(durationMillis = 300),
                                        placementSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                    ),
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
                        val errorMessage = (state as ExpenseUiState.Error).message
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
