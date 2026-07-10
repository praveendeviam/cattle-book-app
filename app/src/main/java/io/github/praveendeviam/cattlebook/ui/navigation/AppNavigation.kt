package io.github.praveendeviam.cattlebook.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.praveendeviam.cattlebook.R
import io.github.praveendeviam.cattlebook.app
import io.github.praveendeviam.cattlebook.ui.addmilk.AddMilkScreen
import io.github.praveendeviam.cattlebook.ui.history.HistoryScreen
import io.github.praveendeviam.cattlebook.ui.home.HomeScreen
import io.github.praveendeviam.cattlebook.ui.payment.RecordPaymentScreen
import io.github.praveendeviam.cattlebook.ui.paymenthistory.PaymentHistoryScreen
import io.github.praveendeviam.cattlebook.ui.pin.PinEntryScreen
import io.github.praveendeviam.cattlebook.ui.pin.PinSetupScreen
import io.github.praveendeviam.cattlebook.ui.pin.PinViewModel
import io.github.praveendeviam.cattlebook.ui.settings.SettingsScreen
import io.github.praveendeviam.cattlebook.ui.theme.Green700

private const val HOME = "home"
private const val HISTORY = "history"
private const val PAYMENT = "record_payment"
private const val PAYMENT_HISTORY = "payment_history"
private const val PIN_ENTRY = "pin_entry"
private const val PIN_SETUP = "pin_setup"
private const val SETTINGS = "settings"
private const val ADD_MILK = "add_milk"
private const val ADD_MILK_WITH_ID = "add_milk?entryId={entryId}"

private val BOTTOM_NAV_ROUTES = setOf(HOME, HISTORY, PAYMENT_HISTORY)

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val pinVm: PinViewModel = viewModel(factory = PinViewModel.factory(context.app.pinPreferences))
    val pinEnabled: Boolean? by produceState<Boolean?>(null) {
        pinVm.pinEnabled.collect { value = it }
    }

    if (pinEnabled == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Green700)
        }
        return
    }

    val startDestination = if (pinEnabled == true) PIN_ENTRY else HOME
    val navController = rememberNavController()
    val currentBack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBack?.destination?.route

    val showBottomNav = currentRoute in BOTTOM_NAV_ROUTES

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == HOME,
                        onClick = {
                            navController.navigate(HOME) {
                                popUpTo(HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_home)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == HISTORY,
                        onClick = {
                            navController.navigate(HISTORY) {
                                popUpTo(HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_history)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == PAYMENT_HISTORY,
                        onClick = {
                            navController.navigate(PAYMENT_HISTORY) {
                                popUpTo(HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_payments)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == PAYMENT,
                        onClick = { navController.navigate(PAYMENT) },
                        icon = { Icon(Icons.Default.Payments, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_record)) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (showBottomNav) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(ADD_MILK) },
                    containerColor = Green700,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text(stringResource(R.string.nav_add_milk), fontWeight = FontWeight.SemiBold) }
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable(PIN_ENTRY) {
                PinEntryScreen(onUnlocked = {
                    navController.navigate(HOME) { popUpTo(PIN_ENTRY) { inclusive = true } }
                })
            }
            composable(PIN_SETUP) {
                PinSetupScreen(onDone = { navController.popBackStack() })
            }
            composable(HOME) {
                HomeScreen(onSettings = { navController.navigate(SETTINGS) })
            }
            composable(
                route = ADD_MILK_WITH_ID,
                arguments = listOf(navArgument("entryId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { back ->
                val id = back.arguments?.getLong("entryId")?.takeIf { it != -1L }
                AddMilkScreen(onBack = { navController.popBackStack() }, entryId = id)
            }
            composable(HISTORY) {
                HistoryScreen(
                    onEditMilk = { id -> navController.navigate("$ADD_MILK?entryId=$id") }
                )
            }
            composable(PAYMENT_HISTORY) {
                PaymentHistoryScreen()
            }
            composable(PAYMENT) {
                RecordPaymentScreen(onBack = { navController.popBackStack() })
            }
            composable(SETTINGS) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
