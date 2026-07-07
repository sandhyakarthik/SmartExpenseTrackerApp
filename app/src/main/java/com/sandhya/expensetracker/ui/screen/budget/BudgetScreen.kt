package com.sandhya.expensetracker.ui.screen.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sandhya.expensetracker.R
import com.sandhya.expensetracker.ui.component.ExpenseTopAppBar

@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedItem by remember { mutableStateOf<BudgetItem?>(null) }

    Scaffold(
        topBar = {
            ExpenseTopAppBar(title = "💰 ${stringResource(R.string.nav_budget)}")
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        MonthlyBudgetCard(summary = uiState.summary)
                    }

                    item {
                        Text(
                            text = "Category Budgets",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    items(uiState.items) { item ->
                        CategoryBudgetListItem(
                            item = item,
                            onClick = { selectedItem = item }
                        )
                    }

                    item {
                        OutlinedButton(
                            onClick = { 
                                val firstEmpty = uiState.items.find { it.budgetedAmount == 0.0 }
                                selectedItem = firstEmpty ?: uiState.items.firstOrNull()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Budget", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }

    selectedItem?.let { item ->
        SetBudgetDialog(
            categoryName = item.categoryName,
            initialAmount = if (item.budgetedAmount > 0) item.budgetedAmount.toString() else "",
            onDismiss = { selectedItem = null },
            onConfirm = { amount ->
                viewModel.onBudgetChanged(item.categoryId, amount)
                selectedItem = null
            }
        )
    }
}
