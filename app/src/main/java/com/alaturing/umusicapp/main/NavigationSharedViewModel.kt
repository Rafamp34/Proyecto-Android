package com.alaturing.umusicapp.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NavigationEvent {
    data object ToIncidents : NavigationEvent()
    data object ToHome : NavigationEvent()
    // Add more navigation events if needed
}


@HiltViewModel
class NavigationSharedViewModel @Inject constructor(): ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>(replay = 0)
    val navigationEvents: SharedFlow<NavigationEvent> get() = _navigationEvents


    fun onNavigateToIncident() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.ToIncidents)
        }
        //_navigationEvents.tryEmit(NavigationEvent.ToIncidents)
    }

    fun onNavigateToHome() {
        _navigationEvents.tryEmit(NavigationEvent.ToHome)
    }

}