package com.example.ludico_app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance {

    // Use 10.0.2.2 to connect from the Android emulator to the host machine's localhost
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create()) // For plain text responses
            .addConverterFactory(GsonConverterFactory.create()) // For JSON responses
            .build()
        retrofit.create(ApiService::class.java)
    }
}
