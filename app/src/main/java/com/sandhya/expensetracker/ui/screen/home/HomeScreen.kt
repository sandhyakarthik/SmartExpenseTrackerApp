package com.sandhya.expensetracker.ui.screen.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sandhya.expensetracker.R
import com.sandhya.expensetracker.Screen
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
    val uiState by viewModel.uiState.collectAsState()
    val dashboardState by viewModel.dashboardState.collectAsState()

    Scaffold(
        topBar = {
            ExpenseTopAppBar(title = stringResource(R.string.app_name))
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddExpense.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.desc_add_expense))
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

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is ExpenseUiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is ExpenseUiState.Empty -> {
                        EmptyHomeContent(
                            totalSpent = dashboardState.totalSpent,
                            monthlySpent = dashboardState.monthlySpent
                        )
                    }

                    is ExpenseUiState.Success -> {
                        val expenses = (uiState as ExpenseUiState.Success).expenses
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            item {
                                SummaryCards(
                                    totalSpent = dashboardState.totalSpent,
                                    monthlySpent = dashboardState.monthlySpent
                                )
                            }

                            item {
                                Text(
                                    text = stringResource(R.string.title_recent_expenses),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

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
                                                contentDescription = stringResource(R.string.desc_delete_expense),
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
                        Text(
                            text = (uiState as ExpenseUiState.Error).message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyHomeContent(
    totalSpent: Double,
    monthlySpent: Double
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SummaryCards(
                totalSpent = totalSpent,
                monthlySpent = monthlySpent
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💰",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.msg_no_expenses),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.msg_empty_habits),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}
