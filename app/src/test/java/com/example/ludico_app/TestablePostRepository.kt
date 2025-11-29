package com.example.ludico_app

import com.example.ludico_app.data.model.Post
import com.example.ludico_app.data.remote.ApiService
import com.example.ludico_app.data.repository.PostRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class TestablePostRepository(private val testApi: ApiService) : PostRepository() {
    override suspend fun getPosts(): List<Post> {
        return testApi.getPosts()

    }
}

class PostRepositoryTest : StringSpec( {

    "getPosts() debe retornnar una lista de posts simulada"{
        //Simulacion de resultado de la API
        val fakePosts = listOf(
            Post(1,1,"Titulo 1","Cuerpo 1"),
            Post(2,2,"Titulo 2","Cuerpo 2")
        )

        //Creacion de mock de ApiService
        val mockApi = mockk<ApiService>()
        coEvery { mockApi.getPosts() } returns fakePosts

        //Usamos la clase que estamos testeando inyectando el mock
        val repo = TestablePostRepository(mockApi)

        //Ejecucion del test
        runTest {
            val result = repo.getPosts()
            result shouldContainExactly fakePosts

        }

    }
})