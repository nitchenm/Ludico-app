package com.example.ludico_app.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowSizeClass

import androidx.compose.runtime.Composable
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.password
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.AuthViewModel
import com.example.ludico_app.viewmodels.NavViewModel
import com.example.ludico_app.R


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navViewModel: NavViewModel,
    windowSizeClass: WindowSizeClass
) {
    val widthSizeClass = windowSizeClass.widthSizeClass

        when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> CompactLoginScreen(authViewModel, navViewModel)
            else -> ExpandedLoginLayout(authViewModel, navViewModel)

        }
}

@Composable
private fun CompactLoginScreen(authViewModel: AuthViewModel, navViewModel: NavViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginForm(authViewModel = authViewModel, navViewModel = navViewModel)
        }
    }
}
@Composable
private fun ExpandedLoginLayout(authViewModel: AuthViewModel, navViewModel: NavViewModel) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(0.5f)) {
            Image(
                painter = painterResource(id = R.drawable.logo), // Usa tu logo o una imagen relevante
                contentDescription = "Login Image",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentScale = ContentScale.Fit
            )
        }
        Box(
            modifier = Modifier
                .weight(0.5f)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            LoginForm(authViewModel = authViewModel, navViewModel = navViewModel)
        }
    }
}

@Composable
private fun LoginForm(authViewModel: AuthViewModel, navViewModel: NavViewModel) {
    val uiState by authViewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Iniciar Sesión", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { authViewModel.onEmailChange(it) },
            label = { Text("Email") },
            isError = uiState.emailError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        uiState.emailError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { authViewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            isError = uiState.passwordError != null,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        uiState.passwordError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp))
        } else {
            Button(
                onClick = { authViewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Login")
            }
        }

        uiState.generalError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        TextButton(onClick = { navViewModel.onNavEvent(NavEvent.ToRegister) }) {
            Text("¿No tienes una cuenta? Regístrate")
        }
    }
}