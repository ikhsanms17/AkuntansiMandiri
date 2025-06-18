package com.akuntansimandiri.app.presentation.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.akuntansimandiri.app.domain.repository.UserRepository
import com.akuntansimandiri.app.navigation.Screen
import com.akuntansimandiri.app.ui.component.EditEmailDialog
import com.akuntansimandiri.app.ui.component.EditNameDialog
import com.akuntansimandiri.app.ui.component.EditPasswordDialog
import com.akuntansimandiri.app.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AccountScreen(
    navController: NavHostController,
    userRepository: UserRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showEditEmailDialog by remember { mutableStateOf(false) }
    var showEditPasswordDialog by remember { mutableStateOf(false) }

    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        userName = SessionManager.getUserName(context) ?: "Pengguna"
        userEmail = SessionManager.getUserEmail(context) ?: "-"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Nama", style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
                    Text(userName, style = MaterialTheme.typography.bodyLarge.copy(color = Color.White))
                }
                Text("edit", color = Color.White, modifier = Modifier.clickable {
                    showEditNameDialog = true
                })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Email", style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
                    Text(userEmail, style = MaterialTheme.typography.bodyLarge.copy(color = Color.White))
                }
                Text("edit", color = Color.White, modifier = Modifier.clickable {
                    showEditEmailDialog = true
                })
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Ganti password anda", color = Color.White, modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showEditPasswordDialog = true
                }
                .padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    SessionManager.clearSession(context)
                    withContext(Dispatchers.Main) {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFC107), // Kuning
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text("LOGOUT")
        }
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        EditNameDialog(
            value = newName,
            onValueChange = { newName = it },
            onDismiss = { showEditNameDialog = false },
            onConfirm = {
                scope.launch {
                    val userId = SessionManager.getUserId(context) ?: return@launch
                    userRepository.updateName(userId, newName)
                    SessionManager.saveUserName(context, newName)
                    userName = newName
                    showEditNameDialog = false
                }
            }
        )
    }

    // Edit Email Dialog
    if (showEditEmailDialog) {
        EditEmailDialog(
            value = newEmail,
            onValueChange = { newEmail = it },
            onDismiss = { showEditEmailDialog = false },
            onConfirm = {
                if (!newEmail.endsWith("@gmail.com")) {
                    Toast.makeText(context, "Email harus menggunakan @gmail.com", Toast.LENGTH_SHORT).show()
                    return@EditEmailDialog
                }

                scope.launch {
                    val userId = SessionManager.getUserId(context) ?: return@launch
                    userRepository.updateEmail(userId, newEmail)
                    SessionManager.saveUserEmail(context, newEmail)
                    userEmail = newEmail
                    showEditEmailDialog = false
                }
            }
        )
    }

    // Edit Password Dialog
    if (showEditPasswordDialog) {
        EditPasswordDialog(
            value = newPassword,
            onValueChange = { newPassword = it },
            onDismiss = { showEditPasswordDialog = false },
            onConfirm = {
                scope.launch {
                    val userId = SessionManager.getUserId(context) ?: return@launch
                    userRepository.updatePassword(userId, newPassword)
                    showEditPasswordDialog = false
                }
            }
        )
    }
}




