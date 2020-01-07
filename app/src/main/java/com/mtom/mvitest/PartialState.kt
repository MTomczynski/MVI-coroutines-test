package com.mtom.mvitest

import com.mtom.mvitest.ButtonClicked.FIVE_SEC
import com.mtom.mvitest.ButtonClicked.INSTANT
import com.mtom.mvitest.ButtonClicked.ONE_SEC

sealed class PartialState : Intent<ViewState> {
    object InstantClicked : PartialState() {
        override fun reduce(oldState: ViewState) =
            oldState.copy(buttonClicked = INSTANT)
    }

    object OneSecClicked : PartialState() {
        override fun reduce(oldState: ViewState) =
            oldState.copy(buttonClicked = ONE_SEC)
    }

    object FiveSecClicked : PartialState() {
        override fun reduce(oldState: ViewState) =
            oldState.copy(buttonClicked = FIVE_SEC)
    }
}