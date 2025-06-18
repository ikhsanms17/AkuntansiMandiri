package com.akuntansimandiri.app.presentation.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.akuntansimandiri.app.data.local.db.AppDatabase
import com.akuntansimandiri.app.data.local.entity.TransactionEntity
import com.akuntansimandiri.app.ui.component.TransactionCard
import com.akuntansimandiri.app.utils.Logger
import com.akuntansimandiri.app.utils.SessionManager

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val transactions = remember { mutableStateListOf<TransactionEntity>() }
    val totalThisMonth = remember { mutableIntStateOf(0) }
    val totalThisDay = remember { mutableIntStateOf(0) }
    val userName = remember { mutableStateOf("") }

    val db = remember { AppDatabase.getInstance(context) }
    val transactionDao = db.transactionDao()

    LaunchedEffect(Unit) {
        Logger.d("Dashboard", "Memulai pengambilan data pengguna dan transaksi")

        val userId = SessionManager.getUserId(context) ?: -1

        userName.value = SessionManager.getUserName(context) ?: "User"
        Logger.d("Dashboard", "Nama pengguna: ${userName.value}")

        val allTransactions = transactionDao.getTransactionsThisMonth(userId)
        Logger.d("Dashboard", "Jumlah total transaksi bulan ini: ${allTransactions.size}")

        val todayTransaction = transactionDao.getTransactionsToday(userId)
        Logger.d("Dashboard", "Jumlah total transaksi bulan ini: ${todayTransaction.size}")

        val getLast5 = transactionDao.getLast5(userId)
        Logger.d("Dashboard", "5 transaksi terbaru: ${getLast5.size}")

        transactions.clear()
        transactions.addAll(getLast5.take(5))
        Logger.d("Dashboard", "Menampilkan 5 transaksi terbaru")

        totalThisMonth.intValue = allTransactions.sumOf { it.price }
        Logger.d("Dashboard", "Total pengeluaran bulan ini: Rp${totalThisMonth.intValue}")

        totalThisDay.intValue = todayTransaction.sumOf { it.price }
        Logger.d("Dashboard", "Total pengeluaran hari ini: Rp${totalThisDay.intValue}")
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "Hello, ${userName.value}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Pengeluaran Bulan Ini", style = MaterialTheme.typography.labelLarge)
                Text(
                    "Rp${totalThisMonth.intValue}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Pengeluaran Hari Ini", style = MaterialTheme.typography.labelLarge)
                Text(
                    "Rp${totalThisDay.intValue}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Transaksi Terakhir", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        if (transactions.isEmpty()) {
            Text("Belum ada transaksi", style = MaterialTheme.typography.bodyMedium)
            Logger.d("Dashboard", "Tidak ada transaksi yang ditemukan")
        } else {
            LazyColumn {
                items(transactions) { txn ->
                    Logger.d("Dashboard", "Menampilkan transaksi: ${txn.name}, Rp${txn.totalPrice}, ${txn.place}, ${txn.date}")
                    TransactionCard(txn)
                }
            }
        }
    }
}
