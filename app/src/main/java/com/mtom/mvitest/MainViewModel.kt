package com.mtom.mvitest

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MainViewModel : ViewModel(), CoroutineScope by MainScope() {

    private val _viewState: ConflatedBroadcastChannel<ViewState> = ConflatedBroadcastChannel()
    val viewState: ReceiveChannel<ViewState>
        get() = _viewState.openSubscription()

    private val _viewEffect: Channel<ViewEffect> = Channel()
    val viewEffect: ReceiveChannel<ViewEffect>
        get() = _viewEffect

    fun process(event: ViewEvent) {
        launch {
            _viewState.offer(
                event.mapToPartialState().reduce(_viewState.valueOrNull ?: ViewState())
            )
        }
        launch { _viewEffect.offer(event.mapToViewEffect()) }
    }

    private suspend fun ViewEvent.mapToPartialState(): PartialState = when (this) {
        ViewEvent.Instant -> PartialState.InstantClicked
        ViewEvent.OneSec -> PartialState.OneSecClicked
        ViewEvent.FiveSec -> PartialState.FiveSecClicked
    }

    private suspend fun ViewEvent.mapToViewEffect(): ViewEffect = when (this) {
        ViewEvent.Instant -> ViewEffect.InstantToast
        ViewEvent.OneSec -> {
            delay(1000)
            ViewEffect.OneSecToast
        }
        ViewEvent.FiveSec -> {
            delay(5000)
            ViewEffect.FiveSecToast
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancel()
    }
}
