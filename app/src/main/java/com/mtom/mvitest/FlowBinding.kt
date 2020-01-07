package com.mtom.mvitest

import android.view.View
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
fun <E> SendChannel<E>.safeOffer(value: E) = !isClosedForSend && try {
    offer(value)
} catch (t: Throwable) {
    false
}

@ExperimentalCoroutinesApi
fun View.clicks(): Flow<Unit> = callbackFlow {
    setOnClickListener { safeOffer(Unit) }
    awaitClose {
        setOnClickListener(null)
    }
}
