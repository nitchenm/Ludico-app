package com.example.ludico_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.navigation.Routes
import com.example.ludico_app.screens.CreateEventScreen
import com.example.ludico_app.screens.EventDetailScreen
import com.example.ludico_app.screens.HomeScreen
import com.example.ludico_app.screens.LoginScreen
import com.example.ludico_app.screens.RegisterScreen
import com.example.ludico_app.screens.SettingsScreen
import com.example.ludico_app.ui.all.theme.LudicoappTheme
import com.example.ludico_app.ui.all.utils.AdaptiveScreenFun
import com.example.ludico_app.viewmodels.AuthViewModel
import com.example.ludico_app.viewmodels.HomeViewModel
import com.example.ludico_app.viewmodels.NavViewModel

class MainActivity : ComponentActivity() {
    private val viewModelFactory by lazy {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val navViewModel by viewModels<NavViewModel>()
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    return AuthViewModel(navViewModel) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navViewModel by viewModels<NavViewModel>()
        val authViewModel by viewModels<AuthViewModel> { viewModelFactory }
        setContent {
            LudicoappTheme {
                val navController = rememberNavController()
                val windowSizeClass = calculateWindowSizeClass(this)
                val navViewModel: NavViewModel = viewModel()
                val homeViewModel: HomeViewModel = viewModel()

                val navEvent by navViewModel.navigationEvents.collectAsState(initial = null)

                LaunchedEffect(navEvent) {
                    when (val event = navEvent) {
                        is NavEvent.ToHome -> navController.navigate(Routes.Home.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        is NavEvent.ToDetail -> navController.navigate(Routes.Detail.createRoute(event.eventId))
                        is NavEvent.ToEditEvent -> navController.navigate(Routes.CreateEvent.createRoute(event.eventId))
                        is NavEvent.ToSettings -> navController.navigate(Routes.Settings.route)
                        is NavEvent.ToRegister -> navController.navigate(Routes.Register.route)
                        is NavEvent.Back -> navController.popBackStack()
                        is NavEvent.ToCreateEvent -> navController.navigate(Routes.CreateEvent.route)
                        is NavEvent.ToLogin -> navController.navigate(Routes.Login.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                        null -> Unit
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Routes.Login.route
                ) {
                    composable(Routes.Login.route) {
                        LoginScreen(
                            authViewModel = authViewModel,
                            navViewModel = navViewModel,
                            windowSizeClass = windowSizeClass
                        )
                    }
                    composable(Routes.Register.route) {
                        RegisterScreen(
                            authViewModel = authViewModel,
                            navViewModel = navViewModel,
                            windowSizeClass = windowSizeClass
                        )
                    }
                    composable(
                        route = Routes.CreateEvent.routeWithArgs, // <-- CORREGIDO
                        arguments = listOf(navArgument(Routes.CreateEvent.eventIdArg) { // <-- CORREGIDO
                            type = NavType.StringType
                            nullable = true
                        })
                    ) {
                        CreateEventScreen(navViewModel = navViewModel)
                    }
                    composable(Routes.Home.route) {
                        HomeScreen(
                            navViewModel = navViewModel,
                            homeViewModel = homeViewModel,
                            windowSizeClass = windowSizeClass
                        )
                    }
                    composable(
                        route = Routes.Detail.route,
                        arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                    ) {
                        EventDetailScreen(
                            navViewModel = navViewModel,
                            windowSizeClass = windowSizeClass
                        )
                    }
                    composable(Routes.Settings.route) {
                        SettingsScreen(navViewModel = navViewModel)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LudicoappTheme {
        AdaptiveScreenFun()
    }
}
