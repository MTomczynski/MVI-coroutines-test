package com.mtom.mvitest

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTests {

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Test
    fun `is instant view event translated to instant view state`() = runBlockingTest {
        val viewModel = MainViewModel(this)
        viewModel.process(ViewEvent.Instant)
        assert(viewModel.viewState.receive().buttonClicked == ButtonClicked.INSTANT)
    }

    @Test
    fun `is one sec view event translated to one sec view state`() = runBlockingTest {
        val viewModel = MainViewModel(this)
        viewModel.process(ViewEvent.OneSec)
        assert(viewModel.viewState.receive().buttonClicked == ButtonClicked.ONE_SEC)
    }

    @Test
    fun `is five sec view event translated to five sec view state`() = runBlockingTest {
        val viewModel = MainViewModel(this)
        viewModel.process(ViewEvent.FiveSec)
        assert(viewModel.viewState.receive().buttonClicked == ButtonClicked.FIVE_SEC)
    }

    @Test
    fun `is one sec view event translated to one sec view effect`() = runBlockingTest {
        val viewModel = MainViewModel(this)
        launch {
            assert(viewModel.viewEffect.receive() is ViewEffect.OneSecToast)
        }
        viewModel.process(ViewEvent.OneSec)
    }

    @Test
    fun `is instant view event translated to instant view effect`() = runBlockingTest {
        val viewModel = MainViewModel(this)
        launch {
            assert(viewModel.viewEffect.receive() is ViewEffect.InstantToast)
        }
        viewModel.process(ViewEvent.Instant)
    }

    @Test
    fun `is five sec view event translated to five sec view effect`() = runBlockingTest {
        val viewModel = MainViewModel(this)
        launch {
            assert(viewModel.viewEffect.receive() is ViewEffect.FiveSecToast)
        }
        viewModel.process(ViewEvent.FiveSec)
    }
}