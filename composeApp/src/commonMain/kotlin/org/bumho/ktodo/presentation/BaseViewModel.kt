package org.bumho.ktodo.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<State, Event, Effect> {

    protected val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(initialState())
    val  state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    protected abstract fun initialState(): State

    abstract fun onEvent(event: Event)

    protected fun setState(reducer: State.() -> State){
        _state.value = _state.value.reducer()
    }

    protected suspend fun setEffect(effect: Effect){
        _effect.emit(effect)
    }

    fun onCleared(){
        viewModelScope.cancel()
    }
}