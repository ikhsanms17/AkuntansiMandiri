package com.akuntansimandiri.app.presentation.transaction.manual

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.akuntansimandiri.app.presentation.transaction.manual.component.ManualInputViewModel
import com.akuntansimandiri.app.utils.Logger
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualInputScreen(
    navController: NavController,
    viewModel: ManualInputViewModel = remember { ManualInputViewModel() }
) {
    val context = LocalContext.current
    val state = viewModel.uiState
    val showDatePicker = remember { mutableStateOf(false) }

    if (showDatePicker.value) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedDate = "$day/${month + 1}/$year"
                viewModel.onDateChange(selectedDate)
                showDatePicker.value = false
                Logger.d("ManualInput", "Tanggal dipilih: $selectedDate")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
        Logger.d("ManualInput", "Menampilkan DatePickerDialog")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Input Manual") },
                navigationIcon = {
                    IconButton(onClick = {
                        Logger.d("ManualInput", "Navigasi kembali ditekan")
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = {
                    viewModel.onNameChange(it)
                    Logger.d("ManualInput", "Nama barang diubah: $it")},
                label = { Text("Nama Barang") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.quantity,
                onValueChange = {
                    viewModel.onQuantityChange(it)
                    Logger.d("ManualInput", "Jumlah diubah: $it")
                },
                label = { Text("Jumlah") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.price,
                onValueChange = {
                    viewModel.onPriceChange(it)
                    Logger.d("ManualInput", "Harga diubah: $it")
                },
                label = { Text("Harga (Rp)") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.place,
                onValueChange = {
                    viewModel.onPlaceChange(it)
                    Logger.d("ManualInput", "Tempat diubah: $it")
                },
                label = { Text("Tempat") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.date,
                onValueChange = {},
                label = { Text("Tanggal") },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDatePicker.value = true
                        Logger.d("ManualInput", "Tanggal diklik untuk pemilihan")
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Logger.d("ManualInput", "Menekan tombol simpan")
                    Logger.d("ManualInput", "Data: nama=${state.name}, harga=${state.price}, jumlah=${state.quantity}, tempat=${state.place}, tanggal=${state.date}")

                    val name = state.name.trim()
                    val quantity = state.quantity.trim()
                    val price = state.price.trim()
                    val place = state.place.trim()

                    if (
                        name.isEmpty() || name == "Tidak ditemukan" ||
                        quantity.isEmpty() || quantity == "Tidak ditemukan" ||
                        price.isEmpty() || price == "Tidak ditemukan" ||
                        place.isEmpty() || place == "Tidak ditemukan"
                    ) {
                        Toast.makeText(context, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.saveTransaction(
                        context = context,
                        onSuccess = {
                            Toast.makeText(context, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onFailed = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Transaksi")
            }
        }
    }
}
