package com.akuntansimandiri.app.presentation.report

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.akuntansimandiri.app.data.local.db.AppDatabase
import com.akuntansimandiri.app.domain.model.FilterType
import com.akuntansimandiri.app.ui.component.FilterDropdown
import com.akuntansimandiri.app.utils.SessionManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReportScreen(
    sessionManager: SessionManager
) {
    val context = LocalContext.current
    val viewModel = remember { ReportViewModel(context) }
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf<FilterType>(FilterType.All) }
    var selectedMonth by remember { mutableIntStateOf(LocalDate.now().monthValue) }
    val coroutineScope = rememberCoroutineScope()
    val startDate = LocalDate.now().minusDays(6).format(DateTimeFormatter.ISO_LOCAL_DATE)

    LaunchedEffect(filter) {
        when (filter) {
            is FilterType.All -> viewModel.loadAll()
            is FilterType.Daily -> {
                val date = (filter as FilterType.Daily).date
                viewModel.loadDaily(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            }
            is FilterType.Weekly -> {
                val end = LocalDate.now()
                val start = end.minusDays(6)
                viewModel.loadWeekly(
                    start.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    end.format(DateTimeFormatter.ISO_LOCAL_DATE)
                )
            }
            is FilterType.Monthly -> {
                val month = (filter as FilterType.Monthly).month.toIntOrNull() ?: LocalDate.now().monthValue
                viewModel.loadMonthly(month)
            }
        }
    }

    LaunchedEffect(Unit) {
        userName = SessionManager.getUserName(context) ?: "Pengguna"
        userEmail = SessionManager.getUserEmail(context) ?: "-"
    }

    Column(Modifier.padding(16.dp)) {
        FilterDropdown(
            selectedFilter = filter,
            selectedMonth = selectedMonth,
            onFilterChange = { filter = it },
            onMonthSelected = { selectedMonth = it; filter = FilterType.Monthly(it.toString()) },
            onDateSelected = { filter = FilterType.Daily(it) }
        )

        Spacer(Modifier.height(16.dp))

        Text("Total Pengeluaran: Rp${viewModel.total}", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(24.dp))
        Text("Pengeluaran per Barang", style = MaterialTheme.typography.titleMedium)
        viewModel.nameSummary.forEach {
            Text("${it.name}: Rp${it.total}")
        }

        Spacer(Modifier.height(24.dp))
        Text("Pengeluaran per Tempat", style = MaterialTheme.typography.titleMedium)
        viewModel.placeSummary.forEach {
            Text("${it.place}: Rp${it.total}")
        }
        
        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            viewModel.generatePdf(
                context, userName, userEmail, sessionManager
            )
        }) {
            Text("Generate PDF")
        }
    }
}



