package com.example.ludico_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.navigation.Routes
import com.example.ludico_app.screens.DetailScreen
import com.example.ludico_app.screens.HomeScreen
import com.example.ludico_app.screens.SettingsScreen
import com.example.ludico_app.ui.all.theme.LudicoappTheme
import com.example.ludico_app.ui.all.utils.AdaptiveScreenFun
import com.example.ludico_app.viewmodels.NavViewModel


class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LudicoappTheme {
                //El navcontroller maneja los stacks de navegacion
                val navController = rememberNavController()
                val windowSizeClass = calculateWindowSizeClass(this)
                val navViewModel: NavViewModel = viewModel()
                //Recolecta los eventos de navegacion
                val navEvent by navViewModel.navigationEvents.collectAsState(initial = null)
                //Manejador que cada vez que navEvent cambia lo maneja como una accion
                LaunchedEffect(navEvent){
                    when (navEvent){
                        is NavEvent.ToHome -> navController.navigate(Routes.Home.route){
                            //Quita todo el stack de navEvent cuando se va a home
                            popUpTo(navController.graph.startDestinationId)
                            //Si ya estoy en home, no se crea otra instancia de este
                            launchSingleTop = true
                        }
                        is NavEvent.ToDetail -> navController.navigate(Routes.Detail.route)
                        is NavEvent.ToSettings -> navController.navigate(Routes.Settings.route)
                        is NavEvent.Back -> navController.popBackStack()
                        null -> Unit
                    }
                }
                //Container que mostrara el destino (pantalla) actual
                NavHost(
                    navController = navController,
                    startDestination = Routes.Home.route // Primera pantalla seteada
                ){
                    composable(Routes.Home.route){
                        //Se le da el navViewModel al homescreen para que pueda trigerear los eventos
                        HomeScreen(navViewModel = navViewModel, windowSizeClass = windowSizeClass)
                    }
                    composable(Routes.Detail.route){
                        DetailScreen(navViewModel = navViewModel)
                    }
                    composable(Routes.Settings.route){
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