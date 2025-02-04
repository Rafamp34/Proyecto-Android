package com.alaturing.umusicapp.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alaturing.umusicapp.authentication.data.repository.PlaylistRepository
import com.alaturing.umusicapp.authentication.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading

            if (query.isEmpty()) {
                _uiState.value = SearchUiState.Initial
                return@launch
            }

            val songsResult = songRepository.readAll()
            val playlistsResult = playlistRepository.readAll()

            if (songsResult.isSuccess && playlistsResult.isSuccess) {
                val songs = songsResult.getOrNull() ?: emptyList()
                val playlists = playlistsResult.getOrNull() ?: emptyList()

                // Actualizada la lógica de filtrado para buscar también en artistas
                val filteredSongs = songs.filter { song ->
                    song.name.contains(query, ignoreCase = true) ||
                            song.artists.any { artist ->
                                artist.name.contains(query, ignoreCase = true)
                            }
                }
                val filteredPlaylists = playlists.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.author.contains(query, ignoreCase = true)
                }

                _uiState.value = SearchUiState.Success(
                    songs = filteredSongs,
                    playlists = filteredPlaylists
                )
            } else {
                _uiState.value = SearchUiState.Error("Error al buscar")
            }
        }
    }
}