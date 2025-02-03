package com.alaturing.umusicapp.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alaturing.umusicapp.authentication.data.repository.UserRepository
import com.alaturing.umusicapp.authentication.data.repository.SongRepository
import com.alaturing.umusicapp.authentication.data.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadContent()
    }

    private fun loadContent() {
        viewModelScope.launch {
            combine(
                songRepository.observeAll(),
                playlistRepository.observeAll()
            ) { songsResult, playlistsResult ->
                if (songsResult.isSuccess && playlistsResult.isSuccess) {
                    HomeUiState.Success(
                        recentSongs = songsResult.getOrNull() ?: emptyList(),
                        playlists = playlistsResult.getOrNull() ?: emptyList()
                    )
                } else {
                    HomeUiState.Error("Error cargando el contenido")
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            userRepository.logout()
            _uiState.value = HomeUiState.LoggedOut
        }
    }
}