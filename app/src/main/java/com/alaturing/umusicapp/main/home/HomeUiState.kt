package com.alaturing.umusicapp.main.home

import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data object LoggedOut : HomeUiState()
    data class Success(
        val recentSongs: List<Song>,
        val playlists: List<Playlist>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}