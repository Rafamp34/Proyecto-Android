package com.alaturing.umusicapp.main.song.ui

import com.alaturing.umusicapp.main.song.model.Song

sealed class SongsUiState {
    data object Loading : SongsUiState()
    data class Success(val songs: List<Song>) : SongsUiState()
    data class Error(val message: String) : SongsUiState()
}