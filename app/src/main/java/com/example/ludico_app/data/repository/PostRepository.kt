package com.example.ludico_app.data.repository

import com.example.ludico_app.data.model.Post
import com.example.ludico_app.data.remote.RetrofitInstance

//el repositorio se encarga de acceder a los datos usandos retrofit

class PostRepository {

    // Funcion que obtiene los posts desde la API
    suspend fun getPosts(): List<Post> {
        return RetrofitInstance.api.getPosts()
    }
}