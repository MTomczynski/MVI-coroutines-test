package com.mtom.mvitest

sealed class ViewEffect {
    object InstantToast : ViewEffect()
    object OneSecToast : ViewEffect()
    object FiveSecToast : ViewEffect()
}