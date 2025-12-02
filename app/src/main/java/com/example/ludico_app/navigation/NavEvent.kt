package com.example.ludico_app.navigation

sealed interface NavEvent {
    data object ToLogin : NavEvent
    data object ToRegister : NavEvent

    data object ToHome : NavEvent
    data class ToDetail(val eventId: String) : NavEvent
    data object ToSettings : NavEvent
    data object Back : NavEvent

    data object ToCreateEvent: NavEvent

    data object ToProfile : NavEvent

    data object ToSupport : NavEvent

}