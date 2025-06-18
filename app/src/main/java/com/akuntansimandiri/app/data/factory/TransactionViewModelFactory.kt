package com.akuntansimandiri.app.data.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akuntansimandiri.app.data.local.db.AppDatabase
import com.akuntansimandiri.app.presentation.transaction.TransactionViewModel

class TransactionViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
