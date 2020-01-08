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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
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
            mapToPartialState(event).collect { partialState ->
                _viewState.send(
                    partialState.reduce(_viewState.valueOrNull ?: ViewState())
                )
            }
        }
        launch {
            mapToViewEffect(event).collect { _viewEffect.send(it) }
        }
    }

    private suspend fun mapToPartialState(event: ViewEvent): Flow<PartialState> = callbackFlow {
        offer(
            when (event) {
                ViewEvent.Instant -> PartialState.InstantClicked
                ViewEvent.OneSec -> PartialState.OneSecClicked
                ViewEvent.FiveSec -> PartialState.FiveSecClicked
            }
        )
    }

    private suspend fun mapToViewEffect(event: ViewEvent): Flow<ViewEffect> = callbackFlow {
        when (event) {
            ViewEvent.Instant -> offer(ViewEffect.InstantToast)
            ViewEvent.OneSec -> {
                delay(1000)
                offer(ViewEffect.OneSecToast)
            }
            ViewEvent.FiveSec -> {
                delay(5000)
                offer(ViewEffect.FiveSecToast)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancel()
    }
}
