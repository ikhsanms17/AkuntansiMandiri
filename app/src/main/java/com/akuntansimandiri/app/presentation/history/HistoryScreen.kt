package com.akuntansimandiri.app.presentation.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.akuntansimandiri.app.domain.model.FilterType
import com.akuntansimandiri.app.ui.component.FilterDropdown
import com.akuntansimandiri.app.ui.component.TransactionCard
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val viewModel = remember { HistoryViewModel(context) }
    val transactions by viewModel.filteredTransactions.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()



    Column(modifier = Modifier.padding(16.dp)) {
        FilterDropdown(
            selectedFilter = filterType,
            selectedMonth = selectedMonth,
            onFilterChange = viewModel::onFilterChange,
            onMonthSelected = viewModel::onMonthSelected,
            onDateSelected = { viewModel.onDateSelected(it) }
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (filterType is FilterType.Daily) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📅 Menampilkan transaksi tanggal ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id")))}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            if (transactions.isEmpty()) {
                item {
                    Text(
                        text = "Tidak ada transaksi",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                items(transactions) { txn ->
                    TransactionCard(txn)
                }
            }
        }
    }
}

