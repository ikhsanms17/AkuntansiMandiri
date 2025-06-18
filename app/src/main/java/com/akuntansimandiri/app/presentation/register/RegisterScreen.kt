package com.akuntansimandiri.app.presentation.register

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.akuntansimandiri.app.data.local.db.AppDatabase
import com.akuntansimandiri.app.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val repeatPassword = remember { mutableStateOf("") }
    val error = remember { mutableStateOf<String?>(null) }
    val emailError = remember { mutableStateOf<String?>(null) }

    val db = remember { AppDatabase.getInstance(context) }
    val userDao = db.userDao()

    fun register() {
        emailError.value = null
        error.value = ""

        when {
            name.value.isBlank() || email.value.isBlank() || password.value.isBlank() || repeatPassword.value.isBlank() ->
                error.value = "Semua field wajib diisi"
            !email.value.endsWith("@gmail.com", ignoreCase = true) ->
                error.value = "Email harus menggunakan @gmail.com"
            password.value != repeatPassword.value ->
                error.value = "Password tidak cocok"
            else -> {
                scope.launch {
                    val existing = userDao.getUserByEmailOrName(email.value)
                    if (existing != null) {
                        error.value = "Email sudah terdaftar"
                    } else {
                        userDao.insertUser(
                            UserEntity(
                                name = name.value,
                                email = email.value,
                                password = password.value
                            )
                        )
                        withContext(Dispatchers.Main) {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    }
                    Log.d("Register", "Register attempt: name=${name}, email=${email}")
                    Log.d("Register", "Register success: inserted userName=$name")

                }
            }
        }
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Nama") })
        OutlinedTextField(
            value = email.value,
            onValueChange = {
            email.value = it
            emailError.value = null
            },
            label = { Text("Email") },
            isError = emailError.value != null,
            supportingText = {
                emailError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
        )
        OutlinedTextField(
            value = password.value, onValueChange = { password.value = it }, label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = repeatPassword.value, onValueChange = { repeatPassword.value = it }, label = { Text("Ulangi Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        error.value?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = { register() }, modifier = Modifier.fillMaxWidth()) {
            Text("Daftar")
        }

        Spacer(Modifier.height(12.dp))
        Row {
            Text("Sudah punya akun?")
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Login",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
    }
}

