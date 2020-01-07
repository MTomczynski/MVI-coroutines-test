package com.mtom.mvitest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTests {

    private val viewModel = MainViewModel()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Test
    fun `is instant view event translated to instant view state`() = runBlockingTest {
        viewModel.process(ViewEvent.Instant)
        assert(viewModel.viewState.receive().buttonClicked == ButtonClicked.INSTANT)
    }

    @Test
    fun `is one sec view event translated to one sec view state`() = runBlockingTest {
        viewModel.process(ViewEvent.OneSec)
        assert(viewModel.viewState.receive().buttonClicked == ButtonClicked.ONE_SEC)
    }

    @Test
    fun `is five sec view event translated to five sec view state`() = runBlockingTest {
        viewModel.process(ViewEvent.FiveSec)
        assert(viewModel.viewState.receive().buttonClicked == ButtonClicked.FIVE_SEC)
    }

    @Test
    fun `is instant view event translated to instant view effect`() = runBlockingTest {
        viewModel.process(ViewEvent.Instant)
        assert(viewModel.viewState.receive().buttonClicked == ButtonClicked.INSTANT)
    }

    @Test
    fun `test channels`() {
        val channel = Channel<Int>()
        val job1 = MainScope().launch(Dispatchers.Unconfined) {
            println("Before receive")
            channel.receive().let {
                println(it)
                assert(it == 1)
            }
            println("After receive")
        }
        val job2 = MainScope().launch(Dispatchers.Unconfined) {
            println("Before offer")
            channel.offer(0)
            println("After offer")
        }
        runBlocking { joinAll(job2, job1) }
    }

    @Test
    fun `is one sec view event translated to one sec view effect`() {
        MainScope().launch {
            println("Before")
            assert(viewModel.viewEffect.receive() is ViewEffect.InstantToast)
            println("After")
        }
        viewModel.process(ViewEvent.OneSec)
    }
}