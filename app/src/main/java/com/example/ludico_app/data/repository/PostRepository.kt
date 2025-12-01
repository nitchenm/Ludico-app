package com.example.ludico_app.data.repository

import com.example.ludico_app.data.model.Post
import com.example.ludico_app.data.remote.RetrofitInstance

open class PostRepository {

    open suspend fun getPosts(): List<Post> {
        return RetrofitInstance.api.getPosts()
    }
}