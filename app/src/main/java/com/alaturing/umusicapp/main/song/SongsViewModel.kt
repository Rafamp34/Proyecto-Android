package com.alaturing.umusicapp.main.song.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alaturing.umusicapp.authentication.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SongsUiState>(SongsUiState.Loading)
    val uiState: StateFlow<SongsUiState> = _uiState.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            repository.observeAll().collect { result ->
                _uiState.value = when {
                    result.isSuccess -> SongsUiState.Success(result.getOrNull() ?: emptyList())
                    else -> SongsUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
                }
            }
        }
    }
}