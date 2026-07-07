package com.sandhya.expensetracker.ui.screen.addexpense

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sandhya.expensetracker.R
import com.sandhya.expensetracker.data.local.CategoryEntity
import com.sandhya.expensetracker.ui.component.ExpenseTopAppBar
import com.sandhya.expensetracker.ui.component.getCategoryEmoji
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val scope = rememberCoroutineScope()

    var showDatePicker by remember { mutableStateOf(false) }
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
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.btn_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.btn_cancel)) }
            }
        ) { DatePicker(state = datePickerState) }
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
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.label_title)) },
                    placeholder = { Text(stringResource(R.string.hint_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.label_amount)) },
                    prefix = { Text("$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: stringResource(R.string.msg_select_category),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_category)) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { showCategorySheet = true })
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedDateFormatted.value,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.label_date)) },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.DateRange, null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(stringResource(R.string.label_notes)) },
                    placeholder = { Text(stringResource(R.string.hint_notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Button(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    enabled = amount.isNotBlank() && selectedCategory != null && title.isNotBlank(),
                    onClick = {
                        val amountVal = amount.toDoubleOrNull() ?: 0.0
                        val date = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        viewModel.save(amountVal, selectedCategory!!, title + " " + note, date)
                        navController.popBackStack()
                    }
                ) {
                    Text(stringResource(R.string.btn_save), fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            sheetState = sheetState
        ) {
            Text(
                text = stringResource(R.string.msg_select_category),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            HorizontalDivider()
            
            if (categories.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(48.dp), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(Modifier.size(32.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("Loading Categories...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                LazyColumn(Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                    items(categories) { category ->
                        ListItem(
                            headlineContent = { Text(category.name) },
                            leadingContent = {
                                Box(
                                    modifier = Modifier.size(40.dp).background(
                                        Color(android.graphics.Color.parseColor(category.colorHex)).copy(alpha = 0.1f), CircleShape
                                    ),
                                    contentAlignment = Alignment.Center
                                ) { Text(getCategoryEmoji(category.name), fontSize = 20.sp) }
                            },
                            modifier = Modifier.clickable {
                                selectedCategory = category
                                showCategorySheet = false
                            }
                        )
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}
