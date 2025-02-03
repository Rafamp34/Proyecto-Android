package com.alaturing.umusicapp.main.playlist.ui

import com.alaturing.umusicapp.main.playlist.model.Playlist

sealed class PlaylistsUiState {
    data object Loading : PlaylistsUiState()
    data class Success(val playlists: List<Playlist>) : PlaylistsUiState()
    data class Error(val message: String) : PlaylistsUiState()
}