package com.akuntansimandiri.app.presentation.report

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akuntansimandiri.app.data.local.db.AppDatabase
import com.akuntansimandiri.app.domain.model.NameSummary
import com.akuntansimandiri.app.domain.model.PlaceSummary
import com.akuntansimandiri.app.ui.component.generatePdfAsByteArray
import com.akuntansimandiri.app.utils.SessionManager
import com.akuntansimandiri.app.utils.saveReceivedFileToDownload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReportViewModel(context: Context) : ViewModel() {
    private val dao = AppDatabase.getInstance(context).transactionDao()

    var total by mutableIntStateOf(0)
        private set
    var nameSummary by mutableStateOf(emptyList<NameSummary>())
        private set
    var placeSummary by mutableStateOf(emptyList<PlaceSummary>())
        private set

    private var userId: Int = -1

    init {
        viewModelScope.launch {
            userId = SessionManager.getUserId(context) ?: -1
            loadAll() // opsional: auto-load saat userId sudah siap
        }
    }

    fun loadAll() {
        viewModelScope.launch {
            total = dao.getTotal(userId) ?: 0
            nameSummary = dao.getSummaryByName(userId)
            placeSummary = dao.getSummaryByPlace(userId)
        }
    }

    fun loadDaily(date: String) {
        viewModelScope.launch {
            total = dao.getTotalByDate(date, userId) ?: 0
            nameSummary = dao.getSummaryByNameByDate(date, userId)
            placeSummary = dao.getSummaryByPlaceByDate(date, userId)
        }
    }

    fun loadWeekly(start: String, end: String) {
        viewModelScope.launch {
            total = dao.getTotalBetween(start, end, userId) ?: 0
            nameSummary = dao.getSummaryByNameBetween(start, end, userId)
            placeSummary = dao.getSummaryByPlaceBetween(start, end, userId)
        }
    }

    fun loadMonthly(month: Int) {
        val monthStr = month.toString().padStart(2, '0')
        viewModelScope.launch {
            total = dao.getTotalByMonth(monthStr, userId) ?: 0
            nameSummary = dao.getSummaryByNameByMonth(monthStr, userId)
            placeSummary = dao.getSummaryByPlaceByMonth(monthStr, userId)
        }
    }

    fun generatePdf(
        context: Context,
        userName: String,
        userEmail: String,
        sessionManager: SessionManager
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTimeMillis = System.currentTimeMillis()
            val userId = sessionManager.getUserId(context) ?: return@launch
            val startDate = LocalDate.now().minusDays(6).format(DateTimeFormatter.ISO_LOCAL_DATE)

            val all = dao.getAll(userId)
            val daily = dao.getTransactionsToday(userId)
            val weekly = dao.getAfterDate(startDate, userId)
            val monthly = dao.getTransactionsThisMonth(userId)

            val file = generatePdfAsByteArray(
                context, userName, userEmail, all, daily, weekly, monthly
            )
            saveReceivedFileToDownload(
                context, file, "Akuntansi_Mandiri", "LaporanKeunagan-$currentTimeMillis.pdf",
                onSuccess = {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "PDF berhasil tersimpan di Download/Akuntansi_Mandiri",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                onFail = {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "PDF gagal disimpan",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }
}



