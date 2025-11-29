package com.example.ludico_app

import com.example.ludico_app.data.model.Post
import com.example.ludico_app.data.repository.PostRepository // 1. Importar el Repositorio
import com.example.ludico_app.viewmodels.PostViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.mockk // 2. Importar mockk para crear el simulacro
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest : StringSpec({

    "postList debe contener los datos esperados despues de fetchPosts()" {

        val fakePosts = listOf(
            Post(1, 1, "Título 1", "Contenido 1"),
            Post(2, 2, "Título 2", "Contenido 2"),
        )

        // 3. CORRECCIÓN: Creamos un mock del repositorio que el constructor necesita
        val mockRepository = mockk<PostRepository>()

        // 4. CORRECCIÓN: Llamamos al constructor con () y le pasamos el mock
        val testViewModel = object : PostViewModel(mockRepository) {
            override fun fetchPosts() {
                _postList.value = fakePosts
            }
        }

        runTest {
            testViewModel.fetchPosts()
            testViewModel.postList.value shouldContainExactly fakePosts
        }
    }
})
