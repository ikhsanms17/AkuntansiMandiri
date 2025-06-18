package com.akuntansimandiri.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.akuntansimandiri.app.data.local.db.AppDatabase
import com.akuntansimandiri.app.navigation.AppNavigation
import com.akuntansimandiri.app.ui.theme.AkuntansiMandiriTheme
import com.akuntansimandiri.app.utils.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AkuntansiMandiriTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val database = AppDatabase.getInstance(context)
                val sessionManager = SessionManager
                AppNavigation(
                    navController,
                    database,
                    sessionManager
                )
            }
        }
    }
}