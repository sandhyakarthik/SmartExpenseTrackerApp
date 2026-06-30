package com.sandhya.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sandhya.expensetracker.ui.screen.addexpense.AddExpenseScreen
import com.sandhya.expensetracker.ui.screen.home.HomeScreen
import com.sandhya.expensetracker.ui.screen.reports.ReportsScreen
import com.sandhya.expensetracker.ui.theme.SmartExpenseTrackerAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartExpenseTrackerAppTheme {
                val navController = rememberNavController()
                val items = listOf(
                    Screen.Home,
                    Screen.Reports,
                    Screen.Budget
                )

                Scaffold(
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        
                        // Only show bottom bar on top-level destinations
                        val showBottomBar = items.any { it.route == currentDestination?.route }
                        
                        if (showBottomBar) {
                            NavigationBar {
                                items.forEach { screen ->
                                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                    NavigationBarItem(
                                        icon = { 
                                            Icon(
                                                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon, 
                                                contentDescription = null 
                                            ) 
                                        },
                                        label = { Text(screen.label) },
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
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
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(navController)
                        }
                        composable(Screen.Reports.route) {
                            ReportsScreen()
                        }
                        composable(Screen.Budget.route) {
                            PlaceholderScreen("Budget")
                        }
                        composable(Screen.AddExpense.route) {
                            AddExpenseScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when(name) {
                    "Reports" -> "📊"
                    "Budget" -> "🎯"
                    else -> "🏗️"
                },
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "$name Screen",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Coming soon in the next update!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

sealed class Screen(
    val route: String, 
    val label: String, 
    val selectedIcon: ImageVector, 
    val unselectedIcon: ImageVector
) {
    object Home : Screen(
        "home", "Home", 
        Icons.Filled.Home, Icons.Outlined.Home
    )
    object Reports : Screen(
        "reports", "Reports", 
        Icons.Filled.List, Icons.Outlined.List
    )
    object Budget : Screen(
        "budget", "Budget", 
        Icons.Filled.Settings, Icons.Outlined.Settings
    )
    object AddExpense : Screen(
        "add_expense", "Add Expense", 
        Icons.Filled.Home, Icons.Outlined.Home
    )
}
