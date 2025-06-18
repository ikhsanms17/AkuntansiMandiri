package com.akuntansimandiri.app.presentation.history

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akuntansimandiri.app.data.local.db.AppDatabase
import com.akuntansimandiri.app.domain.model.FilterType
import com.akuntansimandiri.app.utils.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoryViewModel(context: Context) : ViewModel() {
    private val db = AppDatabase.getInstance(context)
    private val transactionDao = db.transactionDao()

    private val _filterType = MutableStateFlow<FilterType>(FilterType.All)
    val filterType: StateFlow<FilterType> = _filterType

    private val _selectedMonth = MutableStateFlow(LocalDate.now().monthValue)
    val selectedMonth: StateFlow<Int> = _selectedMonth

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    fun onMonthSelected(month: Int) {
        val formattedMonth = month.toString().padStart(2, '0')
        _selectedMonth.value = month
        if (_filterType.value is FilterType.Monthly) {
            _filterType.value = FilterType.Monthly(formattedMonth)
        }
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
        _filterType.value = FilterType.Daily(date)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredTransactions = _filterType.flatMapLatest { filter ->
        flow {
            val userId = SessionManager.getUserId(context) ?: -1
            if (userId == -1) {
                emit(emptyList())
                return@flow
            }

            val data = when (filter) {
                is FilterType.All -> transactionDao.getAll(userId)
                is FilterType.Daily -> {
                    val dateStr = filter.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    transactionDao.getByDate(dateStr, userId)
                }
                is FilterType.Weekly -> {
                    val startDate = LocalDate.now().minusDays(6)
                    transactionDao.getAfterDate(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE), userId)
                }
                is FilterType.Monthly -> transactionDao.getByMonth(filter.month, userId)
            }

            Log.d("HistoryViewModel", "Filter: $filter, Result size: ${data.size}")
            emit(data)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onFilterChange(newFilter: FilterType) {
        _filterType.value = newFilter
        Log.d("HistoryViewModel", "Filter changed to: $newFilter")
    }
}

