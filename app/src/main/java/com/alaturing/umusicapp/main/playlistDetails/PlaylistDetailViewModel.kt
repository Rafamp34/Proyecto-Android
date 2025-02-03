package com.alaturing.umusicapp.main.playlistDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alaturing.umusicapp.authentication.data.repository.PlaylistRepository
import com.alaturing.umusicapp.authentication.data.repository.SongRepository
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistDetailUiState>(PlaylistDetailUiState.Loading)
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            _uiState.value = PlaylistDetailUiState.Loading

            try {
                val playlistResult = playlistRepository.readById(playlistId)
                if (playlistResult.isSuccess) {
                    val playlist = playlistResult.getOrNull()!!
                    // Get songs directly from the playlist data
                    _uiState.value = PlaylistDetailUiState.Success(
                        playlist = playlist,
                        songs = playlist.songs
                    )
                } else {
                    _uiState.value = PlaylistDetailUiState.Error("Error cargando la playlist")
                }
            } catch (e: Exception) {
                _uiState.value = PlaylistDetailUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}