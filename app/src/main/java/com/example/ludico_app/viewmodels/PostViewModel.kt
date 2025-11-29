package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.model.Post

import com.example.ludico_app.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// La clase es 'open' para permitir la herencia en los tests.
open class PostViewModel(private val repository: PostRepository) : ViewModel() {

    // Flujo mutable que contiene la lista de posts. Es 'protected' para que los tests puedan acceder.
    protected val _postList = MutableStateFlow<List<Post>>(emptyList())

    // El flujo público expone una versión de solo lectura y segura usando asStateFlow().
    open val postList: StateFlow<List<Post>> = _postList.asStateFlow()

    // El bloque 'init' se llama automáticamente cuando se crea una instancia del ViewModel.
    init {
        fetchPosts()
    }

    // La función es 'open' para permitir que sea sobrescrita en los tests.
    open fun fetchPosts() {
        viewModelScope.launch {
            try {
                // Ahora, 'repository' se resuelve correctamente a la dependencia del constructor.
                _postList.value = repository.getPosts()
            } catch (e: Exception) {
                // Manejo de errores simple. En una app real, aquí podrías emitir un estado de error.
                println("Error al obtener datos: ${e.message}")
            }
        }
    }
}

