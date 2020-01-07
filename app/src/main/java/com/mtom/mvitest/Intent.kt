package com.mtom.mvitest

interface Intent<T> {
    fun reduce(oldState: T): T
}
