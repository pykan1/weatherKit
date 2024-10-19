package com.example.weather.screen.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.screen.main.MainState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class BaseViewModel<S>(private val initState: S) : ViewModel() {

    val stateFlow = MutableStateFlow(initState)
    protected val state: S
        get() = stateFlow.value

    fun reduce(reduce: () -> S) {
        viewModelScope.launch {
            stateFlow.update { reduce() }
        }
    }

}