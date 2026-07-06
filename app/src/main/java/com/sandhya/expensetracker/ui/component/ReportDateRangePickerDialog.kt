package com.sandhya.expensetracker.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sandhya.expensetracker.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 *Created by  Sandhya D on 6/30/2026.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDateRangePickerDialog(
    onDismiss: () -> Unit,
    onDateRangeSelected: (startDateTimestamp: Long?, endDateTimestamp: Long?) -> Unit
) {
    // 1. Initialize the modern Material 3 Picker State
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.btn_apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    ) {
        // 2. Embed the core M3 DateRangePicker component
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = stringResource(R.string.msg_select_date_range),
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            headline = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).apply {
                        timeZone = java.util.TimeZone.getTimeZone("UTC")
                    }
                    val startText = dateRangePickerState.selectedStartDateMillis?.let { formatter.format(Date(it)) } ?: "Start Date"
                    val endText = dateRangePickerState.selectedEndDateMillis?.let { formatter.format(Date(it)) } ?: "End Date"

                    Text(
                        text = "$startText — $endText",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            showModeToggle = false, // Locks to a clean, un-cluttered calendar grid
            modifier = Modifier.weight(1f)
        )
    }
}
