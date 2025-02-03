package com.alaturing.umusicapp.main.search

import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song

sealed class SearchUiState {
    data object Initial : SearchUiState()
    data object Loading : SearchUiState()
    data class Success(
        val songs: List<Song>,
        val playlists: List<Playlist>
    ) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}