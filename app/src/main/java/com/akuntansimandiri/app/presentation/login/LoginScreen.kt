package com.akuntansimandiri.app.presentation.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.akuntansimandiri.app.R
import com.akuntansimandiri.app.data.local.db.AppDatabase
import com.akuntansimandiri.app.ui.component.EditPasswordDialog
import com.akuntansimandiri.app.utils.SessionManager
import com.akuntansimandiri.app.utils.Logger
import com.akuntansimandiri.app.domain.repository.UserRepository
import com.akuntansimandiri.app.ui.component.ChangePasswordDialog
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    userRepository: UserRepository
) {
    val context = LocalContext.current

    val identifier = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val error = remember { mutableStateOf<String?>(null) }

    val db = remember { AppDatabase.getInstance(context) }
    val userDao = db.userDao()

    val scope = rememberCoroutineScope()
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var changeEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        val session = SessionManager.getSession(context)
        if (session != null) {
            navController.navigate("dashboard") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.akuntan_mandiri_nobg), // ganti dengan nama file logomu
            contentDescription = "Logo",
            modifier = Modifier
                .height(300.dp)
                .width(300.dp)
                .padding(top = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = identifier.value,
            onValueChange = { identifier.value = it },
            label = { Text("Email atau Nama") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(8.dp))
        error.value?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    val user = userDao.getUserByIdentifier(identifier.value, password.value)
                    Logger.d("Login", "Percobaan login dengan input: ${identifier.value}")
                    if (user != null) {
                        SessionManager.saveSession(context, user.id, user.name, user.email)
                        SessionManager.setLoggedIn(context, true)
                        Logger.d(
                            "Login",
                            "Login berhasil: userId=${user.id}, nama=${user.name}"
                        )
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        Logger.w("Login", "Login gagal untuk input: ${identifier.value}")
                        error.value = "Email/nama atau password salah"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Masuk")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Kiri: "Belum punya akun?" + "Daftar"
            Row(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("Belum punya akun?")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Daftar",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        navController.navigate("register")
                    }
                )
            }

            // Kanan: "Lupa Password?"
            Text(
                "Lupa Password?",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable {
                        showChangePasswordDialog = true
                    },
                color = MaterialTheme.colorScheme.primary
            )
        }

    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            email = changeEmail,
            onEmailChange = { changeEmail = it },
            newPassword = newPassword,
            onNewPasswordChange = { newPassword = it },
            repeatPassword = repeatPassword,
            onRepeatPasswordChange = { repeatPassword = it },
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = {
                if (!changeEmail.endsWith("@gmail.com")) {
                    Toast.makeText(context, "Gunakan email @gmail.com", Toast.LENGTH_SHORT)
                        .show()
                    return@ChangePasswordDialog
                }
                if (newPassword != repeatPassword) {
                    Toast.makeText(context, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                    return@ChangePasswordDialog
                }

                scope.launch {
                    val user = userRepository.getUserByEmail(changeEmail)
                    if (user == null) {
                        Toast.makeText(context, "Email tidak ditemukan", Toast.LENGTH_SHORT)
                            .show()
                        return@launch
                    }

                    userRepository.updatePasswordByEmail(changeEmail, newPassword)
                    Toast.makeText(context, "Password berhasil diubah", Toast.LENGTH_SHORT)
                        .show()
                    showChangePasswordDialog = false
                }
            }
        )
    }
}
