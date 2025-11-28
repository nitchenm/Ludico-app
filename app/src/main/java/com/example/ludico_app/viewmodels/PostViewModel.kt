package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.model.Post
import com.example.ludico_app.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val repository = PostRepository()
    // FLujo mutable que contiene la lista de posts
    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    //FLujo publico de solo lectura

    val postList: StateFlow<List<Post>> = _postList
    // se llama automaticamente al iniciar

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            try {
                _postList.value = repository.getPosts()
            } catch (e: Exception){
                println("Error al obtener datos: ${e.localizedMessage}")
            }
        }
    }

}