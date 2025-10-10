package com.example.ludico_app.navigation

sealed interface NavEvent {
    data object ToHome : NavEvent
    data object ToDetail : NavEvent
    data object ToSettings : NavEvent
    data object Back : NavEvent
}