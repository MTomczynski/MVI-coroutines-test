package com.mtom.mvitest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.mtom.mvitest.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private val coroutineScope: CoroutineScope = MainScope()
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        viewEvents()
            .onEach { event -> viewModel.process(event) }
            .launchIn(coroutineScope)
        initViewModelObservers()
    }

    private fun initViewModelObservers() {
        coroutineScope.launch {
            val viewState = viewModel.viewState
            while (isActive) {
                viewState.receive().render()
            }
            viewState.cancel()
        }

        coroutineScope.launch {
            while (isActive) {
                viewModel.viewEffect.receive().trigger()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    @SuppressLint("SetTextI18n")
    private fun ViewState.render() {
        binding.lastButton.text = "Last button: ${buttonClicked.name}"
    }

    private fun ViewEffect.trigger() = when (this) {
        ViewEffect.InstantToast -> showToast("Instant")
        ViewEffect.OneSecToast -> showToast("One sec")
        ViewEffect.FiveSecToast -> showToast("Five sec")
    }

    private fun viewEvents(): Flow<ViewEvent> {
        val events = listOf(
            binding.instant.clicks().map { ViewEvent.Instant },
            binding.oneSec.clicks().map { ViewEvent.OneSec },
            binding.fiveSec.clicks().map { ViewEvent.FiveSec }
        )
        return events.asFlow().flattenMerge(events.size)
    }
}