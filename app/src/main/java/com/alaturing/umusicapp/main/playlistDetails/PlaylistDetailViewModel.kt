package com.alaturing.umusicapp.main.playlistDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alaturing.umusicapp.authentication.data.repository.PlaylistRepository
import com.alaturing.umusicapp.authentication.data.repository.SongRepository
import com.alaturing.umusicapp.authentication.data.repository.UserRepository
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
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistDetailUiState>(PlaylistDetailUiState.Loading)
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    private var currentPlaylistId: Int? = null
    private var availableSongs: List<Song>? = null

    fun loadPlaylist(playlistId: Int) {
        currentPlaylistId = playlistId
        viewModelScope.launch {
            _uiState.value = PlaylistDetailUiState.Loading

            try {
                val playlistResult = playlistRepository.readById(playlistId)
                val userResult = userRepository.getProfile()

                if (playlistResult.isSuccess && userResult.isSuccess) {
                    val playlist = playlistResult.getOrNull()!!
                    val user = userResult.getOrNull()!!

                    // Log each song with its artists
                    playlist.songs.forEach { song ->
                        Log.d("PlaylistViewModel", "Playlist song data - Name: ${song.name}")
                        Log.d("PlaylistViewModel", "Playlist song data - Artists count: ${song.artists.size}")
                        Log.d("PlaylistViewModel", "Playlist song data - Artists: ${song.artists.joinToString(", ") { it.name }}")
                    }

                    _uiState.value = PlaylistDetailUiState.Success(
                        playlist = PlaylistDetailModel(
                            id = playlist.id,
                            name = playlist.name,
                            author = playlist.author,
                            duration = playlist.duration.toString(),
                            imageUrl = playlist.imageUrl,
                            isEditable = playlist.author == user.userName
                        ),
                        songs = playlist.songs
                    ).also { state ->
                        Log.d("PlaylistViewModel", "Emitting state with ${(state as PlaylistDetailUiState.Success).songs.size} songs")
                    }

                    loadAvailableSongs(playlist.songs)
                } else {
                    _uiState.value = PlaylistDetailUiState.Error("Error cargando la playlist")
                }
            } catch (e: Exception) {
                Log.e("PlaylistViewModel", "Error loading playlist", e)
                _uiState.value = PlaylistDetailUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private suspend fun loadAvailableSongs(currentSongs: List<Song>) {
        val allSongsResult = songRepository.readAll()
        if (allSongsResult.isSuccess) {
            val allSongs = allSongsResult.getOrNull()!!
            availableSongs = allSongs.filterNot { song ->
                currentSongs.any { it.id == song.id }
            }
        }
    }

    fun getAvailableSongs() = availableSongs

    fun addSongToPlaylist(song: Song) {
        currentPlaylistId?.let { playlistId ->
            viewModelScope.launch {
                val result = playlistRepository.addSongToPlaylist(playlistId, song.id)
                if (result.isSuccess) {
                    loadPlaylist(playlistId)
                }
            }
        }
    }

    fun removeSongFromPlaylist(song: Song) {
        currentPlaylistId?.let { playlistId ->
            viewModelScope.launch {
                val result = playlistRepository.removeSongFromPlaylist(playlistId, song.id)
                if (result.isSuccess) {
                    loadPlaylist(playlistId)
                }
            }
        }
    }
}