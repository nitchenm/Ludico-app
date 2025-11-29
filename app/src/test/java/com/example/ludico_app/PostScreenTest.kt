package com.example.ludico_app

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.ludico_app.data.model.Post
import com.example.ludico_app.data.repository.PostRepository

// 1. CORRECCIÓN: Se elimina la importación incorrecta para PostRepository.
// import com.example.ludico_app.data.repository.PostRepository

// Esta es la importación CORRECTA para PostRepository, asumiendo que vive en la raíz del paquete 'repository'.

// 2. CORRECCIÓN: Ahora que PostScreen.kt tiene el paquete correcto, esta importación funcionará.
import com.example.ludico_app.screens.PostScreen

import com.example.ludico_app.viewmodels.PostViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class PostScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun title_post_screen_is_displayed_correctly() {
        // --- Arrange (Preparar el escenario) ---

        val fakePosts = listOf(
            Post(1, 1, "Titulo 1", "Cuerpo 1"),
            Post(2, 2, "Titulo 2", "Cuerpo 2")
        )
        val mockRepository = mockk<PostRepository>()
        coEvery { mockRepository.getPosts() } returns fakePosts
        val realViewModel = PostViewModel(repository = mockRepository)

        // --- Act (Ejecutar la acción) ---

        composeRule.setContent {
            PostScreen(viewModel = realViewModel)
        }

        // --- Assert (Verificar el resultado) ---

        composeRule.onNodeWithText("Titulo: Titulo 1").assertIsDisplayed()
        composeRule.onNodeWithText("Titulo: Titulo 2").assertIsDisplayed()
    }
}




