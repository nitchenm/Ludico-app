package com.example.ludico_app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.password
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ludico_app.R
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.NavViewModel
import com.example.ludico_app.viewmodels.*



@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    navViewModel: NavViewModel,
    windowSizeClass: WindowSizeClass
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        RegisterHeader()

        RegisterFormCard(authViewModel, navViewModel)
    }
}

@Composable
private fun RegisterHeader() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(LudicoDarkBG),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Ludico Logo",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Ludico",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
private fun RegisterFormCard(authViewModel: AuthViewModel, navViewModel: NavViewModel) {
    val uiState by authViewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 150.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color.White)
                .padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Campo de Email
            TextField(
                value = uiState.email,
                onValueChange = { authViewModel.onEmailChange(it) },
                placeholder = { Text("Email", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    unfocusedContainerColor = LudicoFieldGray,
                    focusedContainerColor = LudicoFieldGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )
            uiState.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña
            TextField(
                value = uiState.password,
                onValueChange = { authViewModel.onPasswordChange(it) },
                placeholder = { Text("Contraseña", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    unfocusedContainerColor = LudicoFieldGray,
                    focusedContainerColor = LudicoFieldGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) R.drawable.ic_launcher_foreground else R.drawable.ic_launcher_foreground
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painterResource(id = image), "Toggle password visibility")
                    }
                }
            )
            uiState.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Confirmar Contraseña
            TextField(
                value = uiState.confirmPassword,
                onValueChange = { authViewModel.onConfirmPasswordChange(it) },
                placeholder = { Text("Confirmar Contraseña", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    unfocusedContainerColor = LudicoFieldGray,
                    focusedContainerColor = LudicoFieldGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) R.drawable.ic_launcher_foreground else R.drawable.ic_launcher_foreground
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(painterResource(id = image), "Toggle password visibility")
                    }
                }
            )
            uiState.confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Registro (Sign In)
            Button(
                onClick = { authViewModel.register() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LudicoDarkBG, contentColor = Color.White)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Sign In", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Botón para volver a Login
            Button(
                onClick = { navViewModel.onNavEvent(NavEvent.ToLogin) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LudicoGreen, contentColor = Color.Black)
            ) {
                Text("Ya tengo una cuenta", fontWeight = FontWeight.Bold)
            }
        }
    }
}