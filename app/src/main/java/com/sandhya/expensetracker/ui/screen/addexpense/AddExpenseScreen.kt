package com.sandhya.expensetracker.ui.screen.addexpense

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sandhya.expensetracker.R
import com.sandhya.expensetracker.data.local.CategoryEntity
import com.sandhya.expensetracker.ui.component.ExpenseTopAppBar
import com.sandhya.expensetracker.ui.component.getCategoryEmoji
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by Sandhya D on 1/9/2026.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(navController: NavController) {
    val viewModel: AddExpenseViewModel = hiltViewModel()

    val categories by viewModel.categories.collectAsState()
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var showCategorySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Date Picker State
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Aligns the initial date to the start of the day in UTC to match DatePicker's behavior
    val initialDate = remember {
        val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        calendar.timeInMillis
    }
    
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)
    val selectedDateFormatted = remember {
        derivedStateOf {
            val date = datePickerState.selectedDateMillis?.let { Date(it) } ?: Date()
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).apply { 
                timeZone = java.util.TimeZone.getTimeZone("UTC") 
            }.format(date)
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            ExpenseTopAppBar(
                title = stringResource(R.string.nav_add_expense),
                canNavigateBack = true,
                navigateUp = { navController.popBackStack() },
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.label_title)) },
                placeholder = { Text(stringResource(R.string.hint_title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 2. Amount Field
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text(stringResource(R.string.label_amount)) },
                prefix = { Text("$ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 3. Category Selection (Professional Bottom Sheet)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: stringResource(R.string.msg_select_category),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_category)) },
                    trailingIcon = { 
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown, 
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showCategorySheet = true }
                )
            }

            // Category Bottom Sheet
            if (showCategorySheet) {
                ModalBottomSheet(
                    onDismissRequest = { showCategorySheet = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.msg_select_category),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                        
                        if (categories.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(stringResource(R.string.msg_no_categories), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        categories.forEach { category ->
                            ListItem(
                                headlineContent = { Text(category.name) },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                color = Color(android.graphics.Color.parseColor(category.colorHex)).copy(alpha = 0.2f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(getCategoryEmoji(category.name), style = MaterialTheme.typography.titleMedium)
                                    }
                                },
                                modifier = Modifier.clickable {
                                    selectedCategory = category
                                    showCategorySheet = false
                                }
                            )
                        }
                    }
                }
            }

            // 4. Date Picker Field
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedDateFormatted.value,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.label_date)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            // 5. Notes Field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.label_notes)) },
                placeholder = { Text(stringResource(R.string.hint_notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = amount.isNotBlank() && selectedCategory != null && title.isNotBlank(),
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: return@Button
                    val category = selectedCategory ?: return@Button
                    val dateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()

                    val combinedNote = if (note.isBlank()) title else "$title - $note"
                    viewModel.save(amountValue, category, combinedNote, dateMillis)
                    navController.popBackStack()
                }
            ) {
                Text(
                    stringResource(R.string.btn_save),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

