package com.alaturing.umusicapp.main.playlistDetails

import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song

sealed class PlaylistDetailUiState {
    data object Loading : PlaylistDetailUiState()
    data class Success(
        val playlist: Playlist,
        val songs: List<Song>
    ) : PlaylistDetailUiState()
    data class Error(val message: String) : PlaylistDetailUiState()
}