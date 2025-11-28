package com.example.ludico_app.data.remote
import com.example.ludico_app.data.model.Post
import retrofit2.http.GET

//La interfaz nos permite definir los endpoints HTTP :D
interface ApiService {

    //Define la solicitud GET al endpoint
    @GET("/´posts")
    suspend fun getPosts(): List<Post>
}