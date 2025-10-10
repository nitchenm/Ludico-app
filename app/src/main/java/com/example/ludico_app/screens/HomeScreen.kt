package com.example.ludico_app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ludico_app.R
import com.example.ludico_app.viewmodels.NavViewModel
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.graphics.painter.Painter
import com.example.ludico_app.navigation.NavEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navViewModel: NavViewModel,
               windowSizeClass: WindowSizeClass) {
    val widthSizeClass = windowSizeClass.widthSizeClass
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Ludico App")})
        }
    ) { innerPadding ->
        when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                CompactScreenContent(
                    modifier = Modifier.padding(innerPadding),
                    navViewModel = navViewModel
                    )
            }
            else -> {
                ExpandedScreenContent(
                    modifier = Modifier.padding(innerPadding),
                    navViewModel = navViewModel
                )
            }
        }
    }
}


//Funcion para manejo de pantallas compactas
@Composable
private fun CompactScreenContent(modifier: Modifier = Modifier, navViewModel: NavViewModel){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ){
        WelcomeText()
        TestButton(navViewModel = navViewModel)
        LogoImage()
        SettingsCard()
        CustomDivider()
        NameTextField()
    }
}
//Funcion para manejo de pantallas medias y expandidas (doble columna)
@Composable
private fun ExpandedScreenContent(modifier: Modifier = Modifier, navViewModel: NavViewModel){
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ){

    }
}

@Composable
private fun WelcomeText(){
    Text(
        text = "¡Bienvenido a Ludico!",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun TestButton(navViewModel: NavViewModel){
    Button(
        onClick = {navViewModel.onNavEvent(NavEvent.ToHome)},
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ){
        Text("Ir al Home")
    }
}

@Composable
private fun LogoImage(){
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo App",
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        contentScale = ContentScale.Fit
    )
}
@Composable
private fun SettingsCard() {
    Card(
        modifier = Modifier.padding(top = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ){
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Activar modo oscuro",
                color = MaterialTheme.colorScheme.onSurface
            )
            var checked by remember { mutableStateOf(false) }
            Switch(
                checked = checked,
                onCheckedChange = {checked = it},
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun CustomDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        thickness = 1.dp
    )
}
@Composable
private fun NameTextField() {
    var text by remember {mutableStateOf("")}
    TextField(
        value = text,
        onValueChange = {text = it},
        label = {Text ("Dale con tu nombre")},
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}