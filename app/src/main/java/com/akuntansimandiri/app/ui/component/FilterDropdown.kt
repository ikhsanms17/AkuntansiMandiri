package com.akuntansimandiri.app.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.akuntansimandiri.app.domain.model.FilterType
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    selectedFilter: FilterType,
    selectedMonth: Int,
    onFilterChange: (FilterType) -> Unit,
    onMonthSelected: (Int) -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val filterOptions = listOf("Semua", "Harian", "Mingguan", "Bulanan")

    val monthOptions = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = when (selectedFilter) {
                    is FilterType.All -> "Semua"
                    is FilterType.Daily -> "Harian"
                    is FilterType.Weekly -> "Mingguan"
                    is FilterType.Monthly -> "Bulanan"
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("Filter") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                filterOptions.forEachIndexed { index, label ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            expanded = false
                            when (index) {
                                0 -> onFilterChange(FilterType.All)
                                1 -> {
                                    showDatePicker(context) { selected ->
                                        onDateSelected(selected)
                                    }
                                }
                                2 -> onFilterChange(FilterType.Weekly)
                                3 -> {
                                    val formattedMonth = selectedMonth.toString().padStart(2, '0')
                                    onFilterChange(FilterType.Monthly(formattedMonth))
                                }
                            }
                        }
                    )
                }
            }

        }

        if (selectedFilter is FilterType.Monthly) {
            Spacer(modifier = Modifier.height(8.dp))

            var monthExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = monthExpanded,
                onExpandedChange = { monthExpanded = !monthExpanded }
            ) {
                OutlinedTextField(
                    value = monthOptions[selectedMonth - 1],
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Bulan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(monthExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                )

                ExposedDropdownMenu(expanded = monthExpanded, onDismissRequest = { monthExpanded = false }) {
                    monthOptions.forEachIndexed { index, name ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                monthExpanded = false
                                onMonthSelected(index + 1)
                            }
                        )
                    }
                }
            }
        }
    }
}

