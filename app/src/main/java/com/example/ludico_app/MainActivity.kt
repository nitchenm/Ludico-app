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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.navigation.Routes
import com.example.ludico_app.screens.CreateEventScreen
import com.example.ludico_app.screens.DetailScreen
import com.example.ludico_app.screens.EventDetailScreen
import com.example.ludico_app.screens.HomeScreen
import com.example.ludico_app.screens.LoginScreen
import com.example.ludico_app.screens.RegisterScreen
import com.example.ludico_app.screens.SettingsScreen
import com.example.ludico_app.ui.all.theme.LudicoappTheme
import com.example.ludico_app.ui.all.utils.AdaptiveScreenFun
import com.example.ludico_app.viewmodels.AuthViewModel
import com.example.ludico_app.viewmodels.CreateEventViewModel
import com.example.ludico_app.viewmodels.EventDetailViewModel
import com.example.ludico_app.viewmodels.HomeViewModel
import com.example.ludico_app.viewmodels.LudicoViewModelFactory
import com.example.ludico_app.viewmodels.NavViewModel


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Obtenemos la instancia de nuestra clase Application
        val application = application as LudicoApplication

        setContent {
            LudicoappTheme {
                val navController = rememberNavController()
                val windowSizeClass = calculateWindowSizeClass(this)
                val navViewModel: NavViewModel = viewModel()

                // 2. Creamos una instancia de nuestra fábrica universal, pasándole las dependencias.
                val ludicoViewModelFactory = LudicoViewModelFactory(
                    navViewModel = navViewModel,
                    eventRepository = application.eventRepository
                )

                val navEvent by navViewModel.navigationEvents.collectAsState(initial = null)

                LaunchedEffect(navEvent) {
                    when (val event = navEvent) {
                        is NavEvent.ToHome -> navController.navigate(Routes.Home.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        is NavEvent.ToDetail -> navController.navigate(Routes.Detail.createRoute(event.eventId))
                        is NavEvent.ToSettings -> navController.navigate(Routes.Settings.route)
                        is NavEvent.Back -> navController.popBackStack()
                        is NavEvent.ToCreateEvent -> navController.navigate(Routes.CreateEvent.route)
                        is NavEvent.ToLogin -> navController.navigate(Routes.Login.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                        is NavEvent.ToRegister -> navController.navigate(Routes.Register.route)
                        is NavEvent.ToProfile -> navController.navigate(Routes.Profile.route)
                        null -> Unit
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Routes.Login.route
                ) {
                    composable(Routes.Login.route) {
                        val authViewModel: AuthViewModel = viewModel(factory = ludicoViewModelFactory)
                        LoginScreen(
                            authViewModel = authViewModel,
                            navViewModel = navViewModel,
                            windowSizeClass = windowSizeClass
                        )
                    }
                    composable(Routes.Register.route) {
                        val authViewModel: AuthViewModel = viewModel(factory = ludicoViewModelFactory)
                        RegisterScreen(
                            authViewModel = authViewModel,
                            navViewModel = navViewModel,
                            windowSizeClass = windowSizeClass
                        )
                    }
                    composable(Routes.CreateEvent.route) {
                        val createEventViewModel: CreateEventViewModel = viewModel(factory = ludicoViewModelFactory)
                        CreateEventScreen(
                            navViewModel = navViewModel,
                            createEventViewModel = createEventViewModel
                        )
                    }

                    // --- CORRECCIÓN CLAVE 1: HomeScreen ---
                    composable(Routes.Home.route) {
                        // Creamos el HomeViewModel usando la fábrica y lo pasamos a la pantalla.
                        val homeViewModel: HomeViewModel = viewModel(factory = ludicoViewModelFactory)
                        HomeScreen(
                            navViewModel = navViewModel,
                            homeViewModel = homeViewModel, // <-- Pasando el ViewModel
                            windowSizeClass = windowSizeClass
                        )
                    }

                    // --- CORRECCIÓN CLAVE 2: EventDetailScreen ---
                    composable(
                        route = Routes.Detail.route,
                        arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                    ) {
                        // Creamos una fábrica especial para EventDetailViewModel que maneja el SavedStateHandle.
                        val eventDetailViewModel: EventDetailViewModel = viewModel(factory = EventDetailViewModel.Factory)
                        EventDetailScreen(
                            navViewModel = navViewModel,
                            eventDetailViewModel = eventDetailViewModel, // <-- Pasando el ViewModel
                            windowSizeClass = windowSizeClass
                        )
                    }

                    composable(Routes.Profile.route) { /* ... */ }
                    composable(Routes.Settings.route) { /* ... */ }
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