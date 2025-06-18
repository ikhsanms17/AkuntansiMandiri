package com.akuntansimandiri.app.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akuntansimandiri.app.data.local.entity.TransactionEntity

@Composable
fun TransactionCard(transaction: TransactionEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(transaction.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Total: Rp${transaction.price}", style = MaterialTheme.typography.bodyMedium)
            Text("Tempat: ${transaction.place}", style = MaterialTheme.typography.bodyMedium)
            Text("Tanggal: ${transaction.date}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}




