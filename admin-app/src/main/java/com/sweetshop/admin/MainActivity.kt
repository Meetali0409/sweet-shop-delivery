package com.sweetshop.admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.sweetshop.admin.data.api.AuthEvent
import com.sweetshop.admin.data.api.AuthEventManager
import com.sweetshop.admin.data.api.TokenManager
import com.sweetshop.admin.navigation.NavGraph
import com.sweetshop.admin.navigation.Screen
import com.sweetshop.admin.ui.components.AdminDrawer
import com.sweetshop.admin.ui.theme.SweetShopAdminTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var authEventManager: AuthEventManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isLoggedIn = tokenManager.isLoggedIn()

        setContent {
            SweetShopAdminTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var currentRoute by remember { mutableStateOf(Screen.Dashboard.route) }

                val userName by tokenManager.userName.collectAsState(initial = "Admin")
                val userEmail by tokenManager.userEmail.collectAsState(initial = "")

                val startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = currentRoute != Screen.Login.route,
                    drawerContent = {
                        AdminDrawer(
                            currentRoute = currentRoute,
                            userName = userName ?: "Admin",
                            userEmail = userEmail ?: "",
                            onNavigate = { screen ->
                                scope.launch { drawerState.close() }
                                if (screen.route != currentRoute) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Dashboard.route) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                    currentRoute = screen.route
                                }
                            },
                            onLogout = {
                                scope.launch {
                                    drawerState.close()
                                    tokenManager.clearAll()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        onOpenDrawer = {
                            scope.launch { drawerState.open() }
                        }
                    )
                }

                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    authEventManager.authEvents.collect { event ->
                        when (event) {
                            is AuthEvent.SessionExpired -> {
                                Toast.makeText(context, "Session expired. Please login again.", Toast.LENGTH_LONG).show()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                }

                navController.addOnDestinationChangedListener { _, destination, _ ->
                    currentRoute = destination.route ?: Screen.Dashboard.route
                }
            }
        }
    }
}
