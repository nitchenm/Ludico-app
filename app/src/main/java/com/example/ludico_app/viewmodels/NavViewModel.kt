package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.navigation.NavEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NavViewModel : ViewModel() {
    //MutableSharedFlow genera instancias de NavEvent hacia la capa UI
    private val _navigationEvents = MutableSharedFlow<NavEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    //Este metodo es llamado por los composables en el homescreen para trigerear la navegacion
    fun onNavEvent (event : NavEvent){
        viewModelScope.launch {
            _navigationEvents.emit(event)
        }
    }

}