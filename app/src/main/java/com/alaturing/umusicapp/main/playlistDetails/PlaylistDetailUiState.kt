package com.alaturing.umusicapp.main.playlistDetails

import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song

sealed class PlaylistDetailUiState {
    data object Loading : PlaylistDetailUiState()
    data class Success(
        val playlist: PlaylistDetailModel,
        val songs: List<Song>
    ) : PlaylistDetailUiState()
    data class Error(val message: String) : PlaylistDetailUiState()
}

data class PlaylistDetailModel(
    val id: Int,
    val name: String,
    val author: String,
    val duration: String,
    val imageUrl: String?,
    val isEditable: Boolean
)