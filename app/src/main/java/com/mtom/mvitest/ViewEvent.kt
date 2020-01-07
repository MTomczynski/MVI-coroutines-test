package com.mtom.mvitest

sealed class ViewEvent {
    object Instant : ViewEvent()
    object OneSec : ViewEvent()
    object FiveSec : ViewEvent()
}