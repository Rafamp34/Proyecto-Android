package com.alaturing.umusicapp.main.playlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alaturing.umusicapp.authentication.data.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val repository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistsUiState>(PlaylistsUiState.Loading)
    val uiState: StateFlow<PlaylistsUiState> = _uiState.asStateFlow()

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            repository.observeAll().collect { result ->
                _uiState.value = when {
                    result.isSuccess -> PlaylistsUiState.Success(result.getOrNull() ?: emptyList())
                    else -> PlaylistsUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
                }
            }
        }
    }
}