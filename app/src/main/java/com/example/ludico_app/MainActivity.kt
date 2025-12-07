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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.navigation.Routes
import com.example.ludico_app.screens.*
import com.example.ludico_app.ui.all.theme.LudicoappTheme
import com.example.ludico_app.viewmodels.*

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtenemos la instancia de nuestra clase Application, que contiene los repositorios.
        val application = application as LudicoApplication
        val navViewModel: NavViewModel by viewModels()
        setContent {
            LudicoappTheme {
                val windowSizeClass = calculateWindowSizeClass(this)

                // 1. Creamos la Factory UNA SOLA VEZ aquí, pasándole las dependencias de Application.
                val ludicoViewModelFactory = LudicoViewModelFactory(
                    eventRepository = application.eventRepository,
                    userRepository = application.userRepository,
                    sessionManager = application.sessionManager,
                    supportRepository = application.supportRepository,
                    navViewModel = navViewModel
                )

                // 2. Llamamos al contenido principal de la app, que se encargará de la navegación.
                AppContent(
                    navController = rememberNavController(),
                    ludicoViewModelFactory = ludicoViewModelFactory,
                    windowSizeClass = windowSizeClass
                )
            }
        }
    }
}

@Composable
fun AppContent(
    navController: NavHostController,
    ludicoViewModelFactory: LudicoViewModelFactory,
    windowSizeClass: androidx.compose.material3.windowsizeclass.WindowSizeClass
) {
    // 3. Obtenemos la instancia del NavViewModel a través de la factory.
    //    Esto asegura que sea la misma instancia para todo el NavHost.
    val navViewModel: NavViewModel = viewModel(factory = ludicoViewModelFactory)

    // El LaunchedEffect para manejar la navegación.
    val navEvent by navViewModel.navigationEvents.collectAsState(initial = null)
    LaunchedEffect(navEvent) {
        navEvent?.let { event ->
            when (event) {
                is NavEvent.ToHome -> navController.navigate(Routes.Home.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
                is NavEvent.ToDetail -> navController.navigate(Routes.Detail.createRoute(event.eventId))
                is NavEvent.Back -> navController.popBackStack()
                is NavEvent.ToCreateEvent -> navController.navigate(Routes.CreateEvent.route)
                is NavEvent.ToRegister -> navController.navigate(Routes.Register.route)
                is NavEvent.ToLogin -> navController.navigate(Routes.Login.route)
                is NavEvent.ToProfile -> navController.navigate(Routes.Profile.route)
                is NavEvent.ToSettings -> navController.navigate(Routes.Settings.route)
                is NavEvent.ToSupport -> navController.navigate(Routes.Support.route)
                is NavEvent.ToEditEvent -> navController.navigate(Routes.EditEvent.createRoute(event.eventId))
            }
            navViewModel.onNavEvent(event)
        }
    }

    // 4. Se define el NavHost, que es el controlador de qué pantalla se muestra.
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        // 5. AHORA TODAS LAS PANTALLAS OBTIENEN SUS VIEWMODELS DE LA MISMA FACTORY
        composable(Routes.Login.route) {
            val authViewModel: AuthViewModel = viewModel(factory = ludicoViewModelFactory)
            LoginScreen(authViewModel = authViewModel, navViewModel = navViewModel)
        }
        composable(Routes.Register.route) {
            val authViewModel: AuthViewModel = viewModel(factory = ludicoViewModelFactory)
            RegisterScreen(authViewModel = authViewModel, navViewModel = navViewModel)
        }
        composable(Routes.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = ludicoViewModelFactory)
            HomeScreen(navViewModel = navViewModel, homeViewModel = homeViewModel, windowSizeClass = windowSizeClass)
        }
        composable(
            route = Routes.Detail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) {
            val eventDetailViewModel: EventDetailViewModel = viewModel(factory = ludicoViewModelFactory)
            EventDetailScreen(navViewModel = navViewModel, eventDetailViewModel = eventDetailViewModel, windowSizeClass = windowSizeClass)
        }
        composable(Routes.Profile.route) {
            val userViewModel: ProfileViewModel = viewModel(factory = ludicoViewModelFactory)
            ProfileScreen(navViewModel, userViewModel)
        }
        composable(Routes.CreateEvent.route) {
            val createEventViewModel: CreateEventViewModel = viewModel(factory = ludicoViewModelFactory)
            CreateEventScreen(navViewModel, createEventViewModel)
        }
        composable(Routes.Settings.route) {
            SettingsScreen(navViewModel = navViewModel)
        }
        composable(Routes.Support.route){
            val supportViewmodel: SupportViewModel = viewModel(factory = ludicoViewModelFactory)
            SupportScreen(navViewModel = navViewModel, supportViewModel = supportViewmodel)
        }
        composable(route = Routes.EditEvent.route,
            arguments = listOf(navArgument("eventId"){type = NavType.StringType})
        ){
            val createEventViewModel: CreateEventViewModel = viewModel(factory = ludicoViewModelFactory)
            CreateEventScreen(navViewModel, createEventViewModel)
        }
    }
}
