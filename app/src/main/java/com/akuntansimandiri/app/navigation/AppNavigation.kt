package com.akuntansimandiri.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.akuntansimandiri.app.data.local.db.AppDatabase
import com.akuntansimandiri.app.data.repository.UserRepositoryImpl
import com.akuntansimandiri.app.presentation.account.AccountScreen
import com.akuntansimandiri.app.presentation.dashboard.DashboardScreen
import com.akuntansimandiri.app.presentation.history.HistoryScreen
import com.akuntansimandiri.app.presentation.login.LoginScreen
import com.akuntansimandiri.app.presentation.register.RegisterScreen
import com.akuntansimandiri.app.presentation.report.ReportScreen
import com.akuntansimandiri.app.presentation.splash.SplashScreen
import com.akuntansimandiri.app.presentation.transaction.TransactionScreen
import com.akuntansimandiri.app.presentation.transaction.manual.ManualInputScreen
import com.akuntansimandiri.app.presentation.transaction.voice.VoiceInputScreen
import com.akuntansimandiri.app.utils.SessionManager

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Home)
    object Transaction : Screen("transaction", "Transaksi", Icons.Filled.AttachMoney)
    object History : Screen("history", "History", Icons.Filled.History)
    object Report : Screen("report", "Report", Icons.Filled.InsertChart)
    object Account : Screen("account", "Account", Icons.Filled.Person)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Transaction,
    Screen.History,
    Screen.Report,
    Screen.Account
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    database: AppDatabase,
    sessionManager: SessionManager
) {
    val context = LocalContext.current
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        topBar = {
            if (currentRoute != "login" && currentRoute != "register") {
                TopAppBar(
                    title = {
                        Text(bottomNavItems.find { it.route == currentRoute }?.title ?: "")
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(padding)
        ) {
            composable("splash") {
                SplashScreen(navController)
            }
            composable("login") {
                LoginScreen(
                    navController = navController,
                    userRepository = UserRepositoryImpl(AppDatabase.getInstance(context).userDao())
                )
            }
            composable("register") {
                RegisterScreen(navController)
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen()
            }
            composable(Screen.Transaction.route) {
                TransactionScreen(navController)
            }
            composable("voice_input") {
                VoiceInputScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("manual_input") {
                ManualInputScreen(navController)
            }

            composable(Screen.History.route) {
                HistoryScreen()
            }
            composable(Screen.Report.route) {
                ReportScreen(
                    sessionManager = sessionManager
                )
            }
            composable("account") {
                AccountScreen(
                    navController = navController,
                    userRepository = UserRepositoryImpl(AppDatabase.getInstance(context).userDao())
                )
            }
        }
    }
}

