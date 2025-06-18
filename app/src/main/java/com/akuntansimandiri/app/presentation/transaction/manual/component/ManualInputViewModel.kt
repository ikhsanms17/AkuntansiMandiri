package com.akuntansimandiri.app.presentation.transaction.manual.component

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akuntansimandiri.app.data.local.db.AppDatabase
import com.akuntansimandiri.app.data.local.entity.TransactionEntity
import com.akuntansimandiri.app.utils.Logger
import com.akuntansimandiri.app.utils.SessionManager
import com.akuntansimandiri.app.utils.convertToIsoDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TransactionFormState(
    val name: String = "",
    val quantity: String = "",
    val price: String = "",
    val place: String = "",
    val date: String = "",
    val userId: Int = - 1
)

class ManualInputViewModel : ViewModel() {
    var uiState by mutableStateOf(ManualInputUiState())
        private set

    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value)
    }

    fun onQuantityChange(value: String) {
        uiState = uiState.copy(quantity = value)
    }

    fun onPriceChange(value: String) {
        uiState = uiState.copy(price = value)
    }

    fun onPlaceChange(value: String) {
        uiState = uiState.copy(place = value)
    }

    fun onDateChange(value: String) {
        uiState = uiState.copy(date = value)
    }

    fun saveTransaction(
        context: Context,
        onSuccess: () -> Unit,
        onFailed: (String) -> Unit
    ) {
        val db = AppDatabase.getInstance(context)
        val dao = db.transactionDao()
        val isoDate = convertToIsoDate(uiState.date)

        CoroutineScope(Dispatchers.IO).launch {
            val userId = SessionManager.getUserId(context) ?: -1
            if (userId == -1) {
                withContext(Dispatchers.Main) {
                    onFailed("Gagal menyimpan transaksi: pengguna tidak ditemukan.")
                }
                return@launch
            }

            // Validasi input kosong
            if (uiState.name.isBlank() || uiState.quantity.isBlank() ||
                uiState.price.isBlank() || uiState.place.isBlank()
            ) {
                withContext(Dispatchers.Main) {
                    onFailed("Semua data wajib diisi.")
                }
                return@launch
            }

            val transaction = TransactionEntity(
                name = uiState.name,
                quantity = uiState.quantity.toIntOrNull() ?: 0,
                price = uiState.price.toIntOrNull() ?: 0,
                place = uiState.place,
                date = isoDate,
                userId = userId
            )

            dao.insertTransaction(transaction)

            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }


//    fun saveTransaction(context: Context) {
//        val db = AppDatabase.getInstance(context)
//        val dao = db.transactionDao()
//        val isoDate = convertToIsoDate(uiState.date)
//
//        val transaction = TransactionEntity(
//            name = uiState.name,
//            quantity = uiState.quantity.toIntOrNull() ?: 0,
//            price = uiState.price.toIntOrNull() ?: 0,
//            place = uiState.place,
//            date = isoDate,
//            userId =
//        )
//
//        viewModelScope.launch {
//            dao.insertTransaction(transaction)
//            Logger.d("TransactionInput", "Transaksi tersimpan: $transaction")
//        }
//    }
}
