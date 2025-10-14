package com.example.ludico_app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.password


import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ludico_app.R // Asegúrate de que R se importe correctamente
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.NavViewModel
import com.example.ludico_app.viewmodels.AuthViewModel

// --- Colores específicos del diseño (puedes moverlos a tu archivo Theme/Color.kt) ---
val LudicoGreen = Color(0xFF00FF7F)
val LudicoDarkBG = Color(0xFF212121)
val LudicoFieldGray = Color(0xFFF0F0F0)
val LudicoBlueFacebook = Color(0xFF1877F2)

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navViewModel: NavViewModel,
    windowSizeClass: WindowSizeClass // La clase de tamaño sigue siendo útil para la adaptabilidad
) {
    // La lógica de adaptabilidad se puede refinar más adelante si es necesario,
    // pero este diseño funciona bien en una sola columna por ahora.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Usamos un Box para superponer el contenido sobre el encabezado
        LoginHeader()
        LoginFormCard(authViewModel, navViewModel)
    }
}

@Composable
private fun LoginHeader() {
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
private fun LoginFormCard(authViewModel: AuthViewModel, navViewModel: NavViewModel) {
    val uiState by authViewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 150.dp) // Este padding permite que el card se superponga al header
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart = 30.dp,
                        topEnd = 30.dp
                    )
                )
                .background(Color.White)
                .padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar Sesion",
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
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = LudicoFieldGray,
                    focusedContainerColor = LudicoFieldGray
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
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = LudicoFieldGray,
                    focusedContainerColor = LudicoFieldGray
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

            // Fila de "Recuérdame" y "¿Olvidaste tu contraseña?"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = LudicoGreen)
                    )
                    Text("Recuérdame", fontSize = 14.sp)
                }
                Text("¿Olvidaste tu Contraseña?", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.clickable { /* TODO */ })
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Login
            Button(
                onClick = { authViewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LudicoGreen, contentColor = Color.Black)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text("Login", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Botón de Sign In (Registro)
            Button(
                onClick = { navViewModel.onNavEvent(NavEvent.ToRegister) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LudicoDarkBG, contentColor = Color.White)
            ) {
                Text("Sign In", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Botones de inicio de sesión social
            SocialLoginButton(
                text = "Continuar con Facebook",
                icon = R.drawable.ic_launcher_foreground,
                backgroundColor = LudicoBlueFacebook,
                onClick = { /* TODO: Implementar login con Facebook */ }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SocialLoginButton(
                text = "Continuar con Google",
                icon = R.drawable.ic_launcher_foreground,
                backgroundColor = Color.White,
                textColor = Color.Black,
                onClick = { /* TODO: Implementar login con Google */ }
            )
        }
    }
}

@Composable
private fun SocialLoginButton(
    text: String,
    icon: Int,
    backgroundColor: Color,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        border = null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null, // decorative
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontWeight = FontWeight.SemiBold)
        }
    }
}