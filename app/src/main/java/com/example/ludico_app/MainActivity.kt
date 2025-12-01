package com.example.ludico_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.navigation.Routes
import com.example.ludico_app.screens.*
import com.example.ludico_app.ui.all.theme.LudicoappTheme
import com.example.ludico_app.ui.all.utils.AdaptiveScreenFun
import com.example.ludico_app.viewmodels.*

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application as LudicoApplication

        setContent {
            LudicoappTheme {
                val isInitialized by application.isInitialized.collectAsState()

                val windowSizeClass = calculateWindowSizeClass(this)

                if (isInitialized) {
                    AppContent(application, windowSizeClass)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun AppContent(application: LudicoApplication, windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    val navViewModel: NavViewModel = viewModel()

    val ludicoViewModelFactory = LudicoViewModelFactory(
        navViewModel = navViewModel,
        eventRepository = application.eventRepository,
        userRepository = application.userRepository,
        sessionManager = application.sessionManager
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
                navViewModel = navViewModel
            )
        }
        composable(Routes.Register.route) {
            val authViewModel: AuthViewModel = viewModel(factory = ludicoViewModelFactory)
            RegisterScreen(
                authViewModel = authViewModel,
                navViewModel = navViewModel
            )
        }
        composable(Routes.CreateEvent.route) {
            val createEventViewModel: CreateEventViewModel = viewModel(factory = ludicoViewModelFactory)
            CreateEventScreen(
                navViewModel = navViewModel,
                createEventViewModel = createEventViewModel
            )
        }

        composable(Routes.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = ludicoViewModelFactory)
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
            val eventDetailViewModel: EventDetailViewModel = viewModel(factory = EventDetailViewModel.Factory)
            EventDetailScreen(
                navViewModel = navViewModel,
                eventDetailViewModel = eventDetailViewModel,
                windowSizeClass = windowSizeClass
            )
        }

        composable(Routes.Profile.route) { ProfileScreen(navViewModel) }
        composable(Routes.Settings.route) { /* ... */ }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LudicoappTheme {
        AdaptiveScreenFun()
    }
}
